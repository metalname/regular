/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.sector;

import java.io.IOException;
import utils.BufferedRandomAccessFile;

/**
 *
 * @author 
 */
public class DataSector {
    
    public static final int secSize = 512;
    public static final int FATEntrySize = 4;
        
    private byte[] ab;
    //protected int offset;
    protected int secNum;
    
    public DataSector(int secNum) {
        //this.offset = offset;
        this.secNum = secNum;
    }
    
    public void read(BufferedRandomAccessFile raf) throws IOException {
        ab = new byte[secSize];
        raf.seek(secNum * secSize);
        raf.read(ab);
    }
    
    public void write(BufferedRandomAccessFile raf) throws IOException {
        raf.seek(secNum * secSize);
        raf.write(ab);        
    }
    
    public byte[] getData() {
        return(ab);
    }
    
    public void setData(byte[] b) {
        ab = b;
    }
    
    public int getOffset() {
        return(secNum * secSize);
    }
    
    public int getSecNum() {
        return(secNum);
    }
    
    public int getIntAt(int index) {
        int j = 0;
        for (int i = 3; i >= 0; i--) {
            j = (j << 8) + (ab[index + i] & 0xff);
        }
        return(j);
    }
    
    public void putIntAt(int index, int data) {
        int j = data;
        for (int i = 0; i <= 4; i++) {
            byte b = (byte) (j & 0xff);
            ab[index + i] = b;
            j = (j >>> 8);
        }
    }    
    
    public int getFATCount() {
        return(secSize / FATEntrySize);
    }
}
