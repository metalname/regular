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
public abstract class Sector {
    
    protected final static String hexChars = "0123456789abcdef";
    public final static int secSize = 512;
    protected ByteBuffer data;
    
    public Sector() {
    }
    
    public abstract void load() throws IOException;
    public abstract String dump();
    
    public static String numToHex(byte b) {
        return ("" + hexChars.charAt((b & 0xf0) >> 4) + hexChars.charAt((b & 0xf)));
    }
    
    public static String numToHex(short s) {
        
        byte l = (byte) ((s & 0xff));
        byte u = (byte) ((s & 0xff00) >> 8);
        return ("" + numToHex(u) + numToHex(l));
    }
    
    public static String numToHex(int i) {
        
        String s = "";
        long l = i;
        
        for (int index = 0; index <= 3; index++) {
            byte b = (byte) (l & 0xff);
            s = numToHex(b) + s;
            l = l / 0x100;
        }
        
        return(s);        
    }    
        
}
