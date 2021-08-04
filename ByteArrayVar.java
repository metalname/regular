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
public class ByteArrayVar extends Var {

    public ByteArrayVar(int size, BinHelper buffer, int ref) {
        super(size, buffer, ref);
    }

    public ByteArrayVar(int size, BinHelper buffer) {
        super(size, buffer);
    }

    public void setValue(byte[] b) {
        int l = (b.length > size) ? size : b.length;
        buffer.position(ref);
        for (int i = 0; i < l; i++) {
            buffer.put(b[i]);
        }
    }

    public void getValue(byte[] b) {
        buffer.position(ref);
        for (int i = 0; i < size; i++) {
            b[i] = buffer.get();
        }
    }

    @Override
    public String toString() {
        byte[] b = new byte[size];
        getValue(b);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append("" + b[i]);
        }
        return (sb.toString());
    }
    
    @Override
    public String toHexString() {
        StringBuilder sb = new StringBuilder();
        buffer.position(ref);
        for (int i = 0; i < length; i++) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(NumFormat.numToHex(buffer.get()));
        }
        return(sb.toString());
    }      
    
    @Override
    public int load() {
        return(size);
    }
    
    @Override
    public VarType getType() {
        return(VarType.T_BARRAY);
    }
    
    @Override
    public String identify() {
        return ("Byte Array");
    }
    
}
