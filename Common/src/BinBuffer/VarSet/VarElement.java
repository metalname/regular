/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer.VarSet;

import BinBuffer.BinHelper;
import BinBuffer.Var.VarType;

/**
 *
 * @author 
 */
public interface VarElement {
    
    public abstract String identify();
    public abstract int size();
    public abstract int length();
    public abstract void setRef(int offset);
    public abstract int getRef();
    public abstract int load();
    public abstract void setBuffer(BinHelper buffer);
    public abstract VarType getType();
    public abstract String dump();
    public abstract String toHexString();
}
