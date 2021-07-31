/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.sector;

import java.util.ArrayList;

/**
 *
 * @author 
 */
public class NormalSectorArray implements SectorArray {

    private final ArrayList<DataSector> sectors;

    public NormalSectorArray(ArrayList<DataSector> sectors) {
        this.sectors = sectors;
    }

    @Override
    public int getSectorSize() {
        return (DataSector.secSize);
    }

    // sectors are 1-based because the CBF header occupies the first sector in the file
    // sector index 0 starts immediately after the header sector    
    @Override
    public byte[] getSector(int index) {
        return (sectors.get(index).getData());
    }

    @Override
    public byte getByte(int offset) {
        return(getByte(offset / DataSector.secSize, offset % DataSector.secSize));
    }
    
    @Override
    public byte getByte(int sector, int offset) {
        return(sectors.get(sector).getData()[offset]);
    }

    @Override
    public void putByte(int offset, byte b) {
        putByte(offset / DataSector.secSize, offset % DataSector.secSize, b);
    }
    
    @Override
    public void putByte(int sector, int offset, byte b) {
        sectors.get(sector).getData()[offset] = b;
    }

}
