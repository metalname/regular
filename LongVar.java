/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BinBuffer.Var;

import BinBuffer.BinHelper;
import utils.NumFormat;

/**
 *
 * @author 0
 */
public class LongVar extends Var implements NumericVar {

    public LongVar(BinHelper buffer, int ref) {
        super(8, buffer, ref);
    } 
    
    public LongVar(BinHelper buffer) {
        super(8, buffer);
    }
    
    public void putValue(long num) {
        buffer.position(ref);
        buffer.putLong(num);
    }
    
    public long getValue() {
        buffer.position(ref);
        return(buffer.getLong());
    }
    
    @Override
    public String toString() {
        return("" + getValue());
    }  
    
    private static String numToHex(long l) {

        String s = "";

        for (int index = 0; index < 8; index++) {
            byte b = (byte) (l & 0xff);
            s = NumFormat.numToHex(b) + s;
            l = l >>> 8;
        }

        return (s);
    }    
    
    @Override
    public String toHexString() {
        return(numToHex(getValue()));
    }    
    
    @Override
    public byte getNumAsByte() {
        return((byte) getValue());
    }
    
    @Override
    public short getNumAsShort() {
        return((short) getValue());
    }
    
    @Override
    public int getNumAsInt() {
        return((int) getValue());
    }
    
    @Override
    public long getNumAsLong() {
        return(getValue());
    }
    
    @Override
    public int load() {
        return(size);
    }
    
    @Override
    public VarType getType() {
        return(VarType.T_LONG);
    }    
    
    @Override
    public void setNumAsInt(int num) {
        putValue((long) num);
    }
    
    @Override
    public void setNumAsLong(long num) {
        putValue(num);
    }
    
    @Override
    public String identify() {
        return ("Long");
    }
    
}
