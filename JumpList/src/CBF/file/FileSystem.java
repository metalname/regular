/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CBF.file;

import CBF.fat.FAT;
import CBF.sector.SectorArray;

/**
 *
 * @author 0
 */
public class FileSystem {
    
    private final FAT fat;
    private final SectorArray sectorArray;
    public static final int freeEntry = -1;
    
    public FileSystem(FAT fat, SectorArray sectorArray) {
        this.fat = fat;
        this.sectorArray = sectorArray;
    }
    
    public byte getByte(int index) {
        return(sectorArray.getByte(index));
    }
    
    public byte getByte(int sector, int offset) {
        return(sectorArray.getByte(sector, offset));
    }
    
    public void putByte(int index, byte b) {
        sectorArray.putByte(index, b);
    }   
    
    public void putByte(int sector, int offset, byte b) {
        sectorArray.putByte(sector, offset, b);
    }
    
    public int getFATEntry(int index) {
        return(fat.getFATEntry(index));
    }
    
    public int getNumFATEntries() {
        return(fat.getNumEntries());
    }
    
    public byte[] getSector(int index) {
        return(sectorArray.getSector(index));
    }
    
    public int getSectorSize() {
        return(sectorArray.getSectorSize());
    }
    
    public byte[] getBuffer(int index) {
        return(sectorArray.getSector(index));
    }
    
    public void deleteFATEntry(int index) {
        fat.setFATEntry(index, freeEntry);
    }
    
    public String dumpFAT() {
        return(fat.dump());
    }
    
    public FAT getFAT() {
        return(fat);
    }
}
