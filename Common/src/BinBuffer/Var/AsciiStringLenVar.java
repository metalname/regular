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
public class AsciiStringLenVar extends Var implements StringVar {

    public AsciiStringLenVar(int size, BinHelper buffer, int ref) {
        super(size, buffer, ref);
    }

    public AsciiStringLenVar(int size, BinHelper buffer) {
        super(size, buffer);
    }

    public void putValue(String s) {
        int i;
        int l = (s.length() > size ? size : s.length());
        buffer.position(ref);
        for (i = 0; i < l; i++) {
            buffer.put((byte) s.charAt(i));
        }
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        buffer.position(ref);
        for (int i = 0; i < size; i++) {
            char c = (char) buffer.get();
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
            sb.append(NumFormat.numToHex((short) buffer.get()));
        }
        return (sb.toString());
    }

    @Override
    public int load() {
        getValue();
        return (size);
    }

    @Override
    public VarType getType() {
        return (VarType.T_ASCIISL);
    }
    
    @Override
    public String identify() {
        return ("ASCII Fixed-Length String");
    }    
}
