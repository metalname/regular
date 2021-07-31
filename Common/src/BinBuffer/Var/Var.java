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
 * @author 0
 */
public abstract class Var implements VarElement {
    
    protected int size;     //size of variable in bytes
    protected int length;
    protected int ref;
    protected BinHelper buffer;
    protected long flags;
    
    private static class VarSpec {
        private String prefix;
        private int size;
        private int elements;
        
        public VarSpec() {
            size = elements = 0;
        }
        
    }
    
    public Var(int size, BinHelper buffer, int ref) {
        this.size = size;
        this.buffer = buffer;
        this.ref = ref;
        this.length = size;
    }
    
    public Var(BinHelper buffer, int ref) {
        this(0, buffer, ref);
    }
    
    public Var(BinHelper buffer) {
        this(0, buffer, -1);
    }
    
    public Var(int size, BinHelper buffer) {
        this(size, buffer, -1);
    }
    
    @Override
    public void setRef(int ref) {
        this.ref = ref;
    }
    
    @Override
    public int getRef() {
        return(ref);
    }
    
    @Override
    public void setBuffer(BinHelper buffer) {
        this.buffer = buffer;
    }
    
    public BinHelper getBuffer() {
        return(buffer);
    }
    
    @Override
    public int size() {
        return(size);
    }
    
    @Override
    public int length() {
        return(length);
    }
        
    public static Var makeVar(VarType type, int size, BinHelper buffer) {
        return(makeVar(type, size, buffer, 0));
    }
    
    public static Var makeVar(VarType type, int size, BinHelper buffer, int ref) {
        switch (type) {
            case T_BYTE:
                return(new ByteVar(buffer, ref));
            case T_SHORT:
                return(new ShortVar(buffer, ref));
            case T_INT:
                return(new IntVar(buffer, ref));
            case T_LONG:
                return(new LongVar(buffer, ref));
            case T_ASCIISZ:
                return(new AsciiStringSZVar(buffer, ref));
            case T_UNICODESZ:
                return(new UnicodeStringSZVar(buffer, ref));
            case T_ASCIISL:
                return(new AsciiStringLenVar(size, buffer, ref));
            case T_UNICODESL:
                return(new UnicodeStringLenVar(size, buffer, ref));                
            case T_BARRAY:
                return(new ByteArrayVar (size, buffer, ref));
            default:
                System.out.println("Type not implemented: " + type);
                System.exit(-1);
                return(null);
        }
    }
    
    //create varlist from string spec
    // spec = list of type identifiers
    // types (case sensitive) -
    //  i | int = Int
    //  o | short = Short
    //  b | byte = Byte
    //  l | long = Long
    //  Bn | bytesn = Byte Array of size n
    //  Zn | stringuzn = Unicode String null-terminated (max lengh n)
    //  zn | stringazn = ASCII String null-terminated (max length n)
    //  Sn | stringuln = Unicode string of length n
    //  sn | stringaln = ASCII String of length n
    //  array specified by appending [ n ]    
    public static Var makeVar(String spec, BinHelper buffer) {        
        VarSpec vs = splitVar(spec);
        Var v = makeSimpleVar(vs, buffer);
        if (v == null) {
            throw new RuntimeException("Unknown format '" + spec + "'");
        }
        if (vs.elements > 0) {
            return(VarArray.makeVarArray(v, buffer, vs.elements));
        } else {
            return(v);
        }
    }
    
    public static Var makeVar(String spec) {   
        return(makeVar(spec, null));
    }
    
    private static Var makeSimpleVar(VarSpec vs, BinHelper buffer) {
        switch (vs.prefix) {
            case "i":
            case "int":
                return (Var.makeVar(VarType.T_INT, 0, buffer));
            case "o":
            case "short":
                return (Var.makeVar(VarType.T_SHORT, 0, buffer));
            case "byte":
            case "b":
                return (Var.makeVar(VarType.T_BYTE, 0, buffer));
            case "long":
            case "l":
                return (Var.makeVar(VarType.T_LONG, 0, buffer));
            case "stringuz":
            case "Z":
                return (Var.makeVar(VarType.T_UNICODESZ, vs.size, buffer));
            case "stringaz":
            case "z":
                return (Var.makeVar(VarType.T_ASCIISZ, vs.size, buffer));     
            case "bytes":
            case "B":
                return (Var.makeVar(VarType.T_BARRAY, vs.size, buffer));
            case "stringul":
            case "S":
                return (Var.makeVar(VarType.T_UNICODESL, vs.size, buffer));
            case "stringal":
            case "s":
                return (Var.makeVar(VarType.T_ASCIISL, vs.size, buffer));                
            default:
                return(null);
        }        
    }
    
    private static VarSpec splitVar(String spec) {
        StringBuilder sb = new StringBuilder();
        VarSpec vs = new VarSpec();
        //get prefix
        int pos = 0;
        while (pos < spec.length() && isAlpha(spec.charAt(pos))) {
            sb.append(spec.charAt(pos++));
        }
        vs.prefix = sb.toString();
        //get size specifier, if any
        sb.setLength(0);
        while (pos < spec.length() && isNumeric(spec.charAt(pos))) {
            sb.append(spec.charAt(pos++));
        }        
        if (sb.length() > 0) {
            vs.size = Integer.parseInt(sb.toString());
        }
        //see if array spec
        sb.setLength(0);
        if ((pos < spec.length()) && (spec.charAt(pos) == '[')) {
            //get elements
            while (pos < spec.length() && isNumeric(spec.charAt(pos))) {
                sb.append(spec.charAt(pos++));
            }        
            if (sb.length() > 0) {
                vs.elements = Integer.parseInt(sb.toString());
            }
        }
        return(vs);
    }
    
    private static boolean isNumeric(char c) {
        return ((c >= '0') && (c <= '9'));
    }
    
    private static boolean isAlpha(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }
    
    @Override
    public abstract int load();
    @Override
    public abstract VarType getType();
    
    public void init() {
        buffer.position(ref);
        for (int i = 0; i < size; i++) {
            buffer.put((byte) 0);
        }
    }
    
    public void setFlags(long flags) {
        this.flags = flags;
    }
    
    public long getFlags() {
        return(flags);
    }
    
    @Override
    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType()).append(",ref=").append(getRef()).append(",size=").append(size());
        sb.append(",length=").append(length()).append(",value=").append(toHexString());
        return(sb.toString());
    }
    
}
