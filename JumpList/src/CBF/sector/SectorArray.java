/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.sector;

/**
 *
 * @author 
 */
public interface SectorArray {
    
    public byte[] getSector(int index);
    public int getSectorSize();
    public byte getByte(int offset);
    public byte getByte(int sector, int offset);
    public void putByte(int offset, byte b);
    public void putByte(int sector, int offset, byte b);
    
}
