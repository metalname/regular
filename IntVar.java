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
public class IntVar extends Var implements NumericVar {
    
    public IntVar(BinHelper buffer, int ref) {
        super(4, buffer, ref);
    } 
    
    public IntVar(BinHelper buffer) {
        super(4, buffer);
    }
    
    public void putValue(int num) {
        buffer.position(ref);
        buffer.putInt(num);
    }
    
    public int getValue() {
        buffer.position(ref);
        return(buffer.getInt());
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
        return((short) getValue());
    }
    
    @Override
    public int getNumAsInt() {
        return(getValue());
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
        return(VarType.T_INT);
    }
    
    @Override
    public void setNumAsInt(int num) {
        putValue(num);
    }
    
    @Override
    public void setNumAsLong(long num) {
        putValue((int) num);
    }
    
    @Override
    public String identify() {
        return ("Integer");
    }
    
}
