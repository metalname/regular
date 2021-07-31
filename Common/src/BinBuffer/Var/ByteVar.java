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
public class ByteVar extends Var implements NumericVar {
        
    public ByteVar(BinHelper buffer, int ref) {
        super(1, buffer, ref);
    } 
    
    public ByteVar(BinHelper buffer) {
        super(1, buffer);
    }
    
    public void putValue(byte b) {
        buffer.position(ref);
        buffer.put(b);
    }
    
    public byte getValue() {
        buffer.position(ref);
        return(buffer.get());
    }
    
    @Override
    public String toString() {
        return("" + getValue());
    }    
    
    @Override
    public String toHexString() {
        return(NumFormat.numToHex(getValue()));
    }
    
    @Override
    public byte getNumAsByte() {
        return(getValue());
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
        return((long) getValue());
    }
    
    @Override
    public int load() {
        return(size);
    }
    
    @Override
    public VarType getType() {
        return(VarType.T_BYTE);
    }
    
    @Override
    public void setNumAsInt(int num) {
        putValue((byte) num);
    }

    @Override
    public void setNumAsLong(long num) {
        putValue((byte) num);
    }
    
    @Override
    public String identify() {
        return ("Byte");
    }
        
}
