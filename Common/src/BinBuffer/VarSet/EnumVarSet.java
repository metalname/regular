/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer.VarSet;

import BinBuffer.BinHelper;
import BinBuffer.Var.Var;

/**
 *
 * @author 
 * @param <E>
 */
public class EnumVarSet<E extends Enum<E>> extends VarSet {

    private Var[] map;
    private Object[] enums;

    public EnumVarSet(BinHelper buffer, Class<E> enumType) {
        super(buffer);
        prepareEnum(enumType);
    }
    
    public EnumVarSet(Class<E> enumType) {
        this(null, enumType);
    }

    private void prepareEnum(Class<E> enumType) {
        // store enum constants
        enums = new Object[enumType.getEnumConstants().length];
        // allocate arraylist
        map = new Var[enumType.getEnumConstants().length];
        for (int i = 0; i < enumType.getEnumConstants().length; i++) {
            enums[i] = enumType.getEnumConstants()[i];
            map[i] = null;
        }
    }

    private E getEnum(String label) {
        for (int i = 0; i < enums.length; i++) {
            if (((E) enums[i]).toString().equals(label)) {
                return ((E) enums[i]);
            }
        }
        throw new RuntimeException("Label " + label + " does not match any enum constant");
    }

    public boolean isMapped(int i) {
        return (map[i] != null);
    }

    public boolean isMapped(E e) {
        return (isMapped(e.ordinal()));
    }

    public Var getVar(String label) {
        E e = getEnum(label);
        return (getVar(e));
    }

    public Var getVar(E e) {
        if (isMapped(e)) {
            return (getVar(e.ordinal()));
        } else {
            throw new RuntimeException("Label " + e.toString() + " is not mapped");
        }
    }

    public void addVar(Var var, E e) {
        map[e.ordinal()] = var;
    }

    public void addVar(String spec, E e) {
        addVar(Var.makeVar(spec, buffer), e);
    }

    @Override
    public Var getVar(int index) {
        return (map[index]);
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < enums.length; i++) {
            E e = (E) enums[i];
            sb.append(e.toString() + "=" + map[i].toString() + "\n");
        }
        return(sb.toString());
    }

    @Override
    public int load() {
        int ref = 0;
        for (Var var: map) {
            var.setRef(ref);
            ref += var.load();
        }
        return(ref);
    }
}
