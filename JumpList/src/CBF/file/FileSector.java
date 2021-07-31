/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.file;

/**
 *
 * @author 
 */
public class FileSector {
    
    private final int secNum;
    private final int secIndex;
    private final byte[] buffer;
    
    public FileSector(int secIndex, int secNum, byte[] buffer) {
        this.secNum = secNum;
        this.secIndex = secIndex;
        this.buffer = buffer;
    }
    
    public int getSecIndex() {
        return(secIndex);
    }
    
    public int getSecNum() {
        return(secNum);
    }
    
    public byte[] getBuffer() {
        return(buffer);
    }
}
