/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer.Var;

import BinBuffer.BinHelper;
import BinBuffer.VarSet.VarElement;

/**
 *
 * @author 
 */
public class VarArray extends Var {
    
    private final VarElement[] vars;
    private final VarType type;
    
    public VarArray(BinHelper buffer, int ref, int elements, VarType type) {
        super(buffer, ref);
        vars = new VarElement[elements];
        this.type = type;
    }

    public static VarArray makeVarArray(VarElement var, BinHelper buffer, int elements) {
        return(makeVarArray(var.getType(), var.length(), buffer, 0, elements));
    }
    
    public static VarArray makeVarArray(VarElement var, BinHelper buffer, int elements, int ref) {
        VarArray va = new VarArray(buffer, ref, elements, var.getType());
        for (int i = 0; i < elements; i++) {
            va.setElement(i, var);
        }
        return (va);
    }
        
    public static VarArray makeVarArray(VarType type, int size, BinHelper buffer, int ref, int elements) {
        VarArray va = new VarArray(buffer, ref, elements, type);
        for (int i = 0; i < elements; i++) {
            va.setElement(i, Var.makeVar(type, size, buffer));
        }
        return (va);
    }
    
    @Override
    public String toHexString() {
        StringBuilder sb = new StringBuilder();
        for (VarElement v: vars) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(v.toHexString());
        }
        return(sb.toString());
    }
    
    public void setElement(int index, VarElement var) {
        vars[index] = var;
    }
    
    public VarElement getElement(int index) {
        return(vars[index]);
    }
    
    @Override
    public int load() {
        length = 0;
        size = 0;
        int offset = ref;
        for (VarElement var: vars) {
            var.setRef(offset);
            var.load();
            length += var.length();
            size += var.size();
            offset += var.size();
        }
        return(size);
    }
    
    @Override
    public VarType getType() {
        return(type);
    }
    
    @Override
    public String identify() {
        return ("Array");
    }
    
    @Override
    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("ARRAY ").append(vars.length).append(" of ").append(getType()).append(",ref=").append(getRef()).append(",size=").append(size());
        sb.append(",length=").append(length()).append(",value=").append(toHexString());
        return(sb.toString());
    }
    
    
}
