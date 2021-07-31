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
public class UnicodeStringSZVar extends Var implements StringVar {

    private int maxlen = 0;

    public UnicodeStringSZVar(BinHelper buffer, int ref) {
        super(buffer, ref);
    }

    public UnicodeStringSZVar(int length, BinHelper buffer, int ref) {
        super(buffer, ref);
        // maxlen specifies maximum length of string, 0 = unlimited
        this.maxlen = length;
    }

    public UnicodeStringSZVar(BinHelper buffer) {
        super(buffer);
    }

    public UnicodeStringSZVar(int length, BinHelper buffer) {
        super(buffer);
        // maxlen specifies maximum length of string, 0 = unlimited
        this.maxlen = length;
    }

    public void putValue(String s) {
        int i;
        buffer.position(ref);
        for (i = 0; i < s.length(); i++) {
            buffer.putChar(s.charAt(i));
        }
        buffer.putChar((char) 0);
        size = (i + i) * 2;
        length = maxlen = i;
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        buffer.position(ref);
        char c;
        int i = 0;
        do {
            c = buffer.getChar();
            if (c != 0) {
                sb.append(c);
            }
            if ((maxlen > 0) && (i >= maxlen)) {
                break;
            }
        } while (c != 0);
        size = i * 2;
        length = i - 1;
        return (sb.toString());
    }

    @Override
    public String toString() {
        return ("" + getValue());
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
        return (sb.toString());
    }

    @Override
    public int load() {
        getValue();
        return(size);
    }
    
    @Override
    public VarType getType() {
        return(VarType.T_UNICODESZ);
    }
    
    @Override
    public String identify() {
        return ("UNICODE Zero-Terminated String");
    }
    
}
