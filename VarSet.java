/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer.VarSet;

import BinBuffer.BinHelper;
import BinBuffer.Var.Var;
import BinBuffer.Var.VarType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author 
 */
public class VarSet implements VarElement, Iterable<VarElement> {

    private List<VarElement> varList;
    protected BinHelper buffer;
    protected int totalSize;
    protected int ref = 0;
    protected int size;
    protected int length;

    public VarSet() {
        this(null);
    }
    
    public VarSet(BinHelper buffer) {
        varList = new ArrayList<>();
        this.buffer = buffer;
    }

    public void addVar(Var var) {
        varList.add(var);
    }

    public int addVar(VarType type, int size, int ref) {
        Var var = Var.makeVar(type, size, buffer, ref);
        varList.add(var);
        return (ref + var.load());
    }
    
    @Override
    public void setRef(int ref) {
        this.ref = ref;
    }
    
    @Override
    public int getRef() {
        return(ref);
    }

    public void setVar(int i, Var var) {
        varList.set(i, var);
    }

    public VarElement getVar(int i) {
        return (varList.get(i));
    }

    protected int lastIndex() {
        return (varList.size() - 1);
    }

    protected void throwError(String message) {
        throw new RuntimeException(message);
    }

    @Override
    public Iterator<VarElement> iterator() {
        return(varList.iterator());
    }
    
    protected void calcTotalSize() {
        totalSize = 0;
        for (VarElement var: this) {
            totalSize += var.size();
        }
    }
    
    @Override
    public int load() {
        size = 0;
        length = 0;
        int offset = 0;
        for (VarElement var: this) {
            var.setRef(offset);
            offset += var.load();
            size += var.size();
            length += var.length();
        }
        return(size);
    }
    
    @Override
    public int size() {
        return(size);
    }
    
    @Override
    public int length() {
        return(size);
    }
    
    public List<VarElement> getVarList() {
        return(varList);
    }
    
    public void setVarList(List<VarElement> varList) {
        this.varList = varList;
    }
 
    @Override
    public void setBuffer(BinHelper buffer) {
        this.buffer = buffer;
        for (VarElement var: this) {
            var.setBuffer(buffer);
        }        
    }
    
    @Override
    public String identify() {
        return("Structure");
    }
    
    /**
     *
     * @return
     */
    @Override
    public VarType getType() {
        return(VarType.T_VARSET);
    }
    
    @Override
    public String dump() {
        return("Structure");        
    }
    
    @Override
    public String toHexString() {
        return("");
    }
}
