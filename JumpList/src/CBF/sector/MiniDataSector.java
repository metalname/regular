/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.sector;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author 
 */
public class MiniDataSector {
    public static final int secSize = 64;
    
    private byte[] ab;
    protected int offset;
    
    public MiniDataSector(int offset) {
        this.offset = offset;
    }
    
    public void read(ByteBuffer miniStream) {
        ab = new byte[secSize];
        miniStream.position(offset);
        miniStream.get(ab);
    }
    
    public void write(ByteBuffer miniStream) throws IOException {
        miniStream.position(offset);
        miniStream.put(ab);        
    }
    
    public byte[] getData() {
        return(ab);
    }
    
    public int getOffset() {
        return(offset);
    }
    
    public int getIntAt(int index) {
        int j = 0;
        for (int i = 3; i >= 0; i--) {
            j = (j * 0x100) + ab[index + i];
        }
        return(j);
    }
    
}
