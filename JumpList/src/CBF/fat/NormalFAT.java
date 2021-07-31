/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.fat;

import CBF.sector.DataSector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import utils.NumFormat;

/**
 *
 * @author 
 */
public class NormalFAT implements FAT {
    
    private final List<DataSector> FATSectors = new ArrayList<>();
    public static final int EntriesPerSector = DataSector.secSize / DataSector.FATEntrySize;
    public static final int freeEntry = -1;
    
    public NormalFAT(DataSector[] f) {
        FATSectors.addAll(Arrays.asList(f));
    }
    
    @Override
    public int getFATEntry(int index) {
        // determine which sector index is in
        int sector = index / EntriesPerSector;
        int sectorIndex = (index % EntriesPerSector) * DataSector.FATEntrySize;
        return(FATSectors.get(sector).getIntAt(sectorIndex));
    }
    
    /**
     *
     * @param index
     * @param entry
     */
    @Override
    public void setFATEntry(int index, int entry) {
        // determine which sector index is in
        int sector = index / EntriesPerSector;
        int sectorIndex = index % EntriesPerSector;
        FATSectors.get(sector).putIntAt(sectorIndex, entry);
    }
    
    @Override
    public int getNumEntries() {
        return(FATSectors.size() * EntriesPerSector);
    }
    
    @Override
    public String dump() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumEntries() / 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(NumFormat.numToHex(getFATEntry(i * 16 + j)));
            }
            if (sb.length() > 0) {
                sb.append('\n');
            }
        }
        return(sb.toString());
    }
    
    @Override
    public int getNumSectors() {
        return(FATSectors.size());
    }
    
    public List<DataSector> getSectorList() {
        return(FATSectors);
    }
}
