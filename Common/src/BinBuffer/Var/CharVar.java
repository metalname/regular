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
public class CharVar extends Var implements NumericVar {
    
    public CharVar(BinHelper buffer, int ref) {
        super(buffer, ref);
    } 
    
    public CharVar(BinHelper buffer) {
        super(buffer);
    }    
    
    public char getValue() {
        buffer.position(ref);
        return(buffer.getChar());
    }
    
    public void putValue(char c) {
        buffer.position(ref);
        buffer.putChar(c);
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
        return(VarType.T_CHAR);
    }
    
    @Override
    public void setNumAsInt(int num) {
        putValue((char) num);
    }
    
    @Override
    public void setNumAsLong(long num) {
        putValue((char) num);
    }
    
    @Override
    public String identify() {
        return ("Char");
    }
    
}
