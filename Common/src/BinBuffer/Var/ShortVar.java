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
public class ShortVar extends Var implements NumericVar {
    
    public ShortVar(BinHelper buffer, int ref) {
        super(2, buffer, ref);
    } 
    
    public ShortVar(BinHelper buffer) {
        super(2, buffer);
    }
    
    public void putValue(short num) {
        buffer.position(ref);
        buffer.putShort(num);
    }
    
    public short getValue() {
        buffer.position(ref);
        return(buffer.getShort());
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
        return((byte) getValue());
    }
    
    @Override
    public short getNumAsShort() {
        return(getValue());
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
        return(VarType.T_SHORT);
    }
    
    @Override
    public void setNumAsInt(int num) {
        putValue((short) num);
    }
    
    @Override
    public void setNumAsLong(long num) {
        putValue((short) num);
    }
        
    @Override
    public String identify() {
        return ("Short");
    }
    
}
