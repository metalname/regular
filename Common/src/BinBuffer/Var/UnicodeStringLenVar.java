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
public class UnicodeStringLenVar extends Var implements StringVar {

    public UnicodeStringLenVar(int length, BinHelper buffer, int ref) {
        super(length, buffer, ref);
        this.size = length * 2;
    }

    public UnicodeStringLenVar(int length, BinHelper buffer) {
        this(length, buffer, 0);
    }

    public void putValue(String s) {
        int i;
        buffer.position(ref);
        int l = (s.length() > size ? size : s.length());
        for (i = 0; i < l; i++) {
            buffer.putChar(s.charAt(i));
        }
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        buffer.position(ref);
        char c;
        for (int i = 0; i < length; i++) {
            c = buffer.getChar();
            if (c == 0) {
                break;
            }
            sb.append(c);
        }
        return (sb.toString());
    }

    @Override
    public String toString() {
        return (getValue());
    }
    
    @Override
    public String toHexString() {
        StringBuilder sb = new StringBuilder();
        buffer.position(ref);
        for (int i = 0; i < length; i++) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(NumFormat.numToHex(buffer.getShort()));
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
        return(VarType.T_UNICODESL);
    }
    
    @Override
    public String identify() {
        return ("UNICODE Fixed-Length String");
    }
        
}
