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
 * Ascii zero-terminated string
 */
public class AsciiStringSZVar extends Var implements StringVar {
    
    private int maxlen = 0;
    
    public AsciiStringSZVar(BinHelper buffer, int ref) {
        super(0, buffer, ref);
    }
    
    public AsciiStringSZVar(int maxlen, BinHelper buffer, int ref) {
        super(0, buffer, ref);
        this.maxlen = maxlen;
    }    
    
    public AsciiStringSZVar(BinHelper buffer) {
        super(0, buffer);
    }
    
    public AsciiStringSZVar(int maxlen, BinHelper buffer) {
        super(0, buffer);
        this.maxlen = maxlen;
    }
        
    public void putValue(String s) {
        int i;
        buffer.position(ref);
        for (i = 0; i < s.length(); i++) {
            buffer.put((byte) s.charAt(i));
        }
        buffer.put((byte) 0);
        size = i + i;
        length = maxlen = i;
    }
    
    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        buffer.position(ref);
        byte b;
        int i = 0;
        do {
            b = buffer.get();
            i++;
            if (b != 0) {
                sb.append((char) b);
            }
            if ((maxlen > 0) && (i >= maxlen)) {
                break;
            }            
        } while (b != 0);
        size = i;
        length = i - 1;
        return(sb.toString());
    }
    
    @Override
    public String toString() {
        return(getValue());
    }    
    
    @Override
    public String toHexString() {
        StringBuilder sb = new StringBuilder();
        buffer.position(ref);
        for (int i = 0; i < length; i++) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(NumFormat.numToHex((short) buffer.get()));
        }
        return(sb.toString());
    }    
    
    @Override
    public int load() {
        getValue();
        return(size);
    }
    
    @Override
    public VarType getType() {
        return(VarType.T_ASCIISZ);
    }
    
    @Override
    public String identify() {
        return ("ASCII Zero-Terminated String");
    }
    
}
