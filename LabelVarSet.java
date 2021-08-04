/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer.VarSet;

import BinBuffer.BinHelper;
import BinBuffer.Var.NumericVar;
import BinBuffer.Var.StringVar;
import BinBuffer.Var.Var;
import BinBuffer.Var.VarType;
import java.util.Iterator;
import java.util.Set;
import utils.IndexedHashMap;

/**
 *
 * @author 0
 */
public class LabelVarSet extends VarSet {

    private IndexedHashMap<String, VarElement> map;

    public LabelVarSet(BinHelper buffer) {
        super(buffer);
        map = new IndexedHashMap<>();
    }
    
    public VarElement getVar(String label) {
        VarElement var = map.get(label);
        if (var != null) {
            return(var);
        } else {
            throwError("Unknown label " + label);
        }
        return(null);
    }
    
    public void addVar(VarType type, int size, int ref, String label) {       
        map.put(label, Var.makeVar(type, size, buffer, ref));
    }    
    
    public void addVar(VarElement var, String label) {
        map.put(label, var);
    }
    
    @Override
    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("LabelVarSet at ").append(getRef()).append(":");
        Iterator<String> itr = map.keyIterator();
        while (itr.hasNext()) {
            String s = itr.next();
            if (sb.length() > 0) {
                sb.append("\n");
            }            
            VarElement v = getVar(s);
            sb.append(s).append(": ").append(v.dump());
        }
        return(sb.toString());
    }
    
    @Override
    public Iterator<VarElement> iterator() {
        return(map.iterator());
    }
    
    public Iterator<String> keyIterator() {
        return(map.keyIterator());
    }
    
    public Set<String> labels() {
        return(map.getKeys());
    }
    
    public short getShort(String label) {
        return(((NumericVar) getVar(label)).getNumAsShort());
    }
        
    public int getInt(String label) {
        return(((NumericVar) getVar(label)).getNumAsInt());
    }
    
    public long getLong(String label) {
        return(((NumericVar) getVar(label)).getNumAsLong());
    }    
    
    public String getString(String label) {
        return(((StringVar) getVar(label)).getValue());
    }
    
    public void setInt(String label, int num) {
        ((NumericVar) getVar(label)).setNumAsInt(num);
    }
    
    public void setLong(String label, long num) {
        ((NumericVar) getVar(label)).setNumAsLong(num);
    }
        
}
