/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.sector;

import CBF.file.FileBuffer;

/**
 *
 * @author 
 */
public class MiniSectorArray implements SectorArray {
    
    private final FileBuffer miniStream;
    
    public MiniSectorArray(FileBuffer miniStream) {
        this.miniStream = miniStream;
    }
    
    @Override
    public int getSectorSize() {
        return(MiniDataSector.secSize);
    }
    
    @Override
    public byte[] getSector(int index) {
        byte[] b = new byte[getSectorSize()];
        miniStream.position(index * getSectorSize());
        miniStream.get(b);
        return(b);
    }
    
    @Override
    public byte getByte(int index) {
        miniStream.position(index);
        return(miniStream.get());
    }
    
    @Override
    public byte getByte(int sector, int offset) {
        return(getByte(sector * getSectorSize() + offset));
    }
    
    @Override
    public void putByte(int index, byte b) {
        miniStream.position(index);
        miniStream.put(b);
    }    
    
    @Override
    public void putByte(int sector, int offset, byte b) {
        putByte(sector * getSectorSize() + offset, b);
    }
}

