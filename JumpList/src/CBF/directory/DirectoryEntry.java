/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.directory;

import CBF.file.FileBuffer;
import utils.NumFormat;
import utils.WindowsDate;

/**
 *
 * @author 
 */
public class DirectoryEntry {

    public final static int STGTY_INVALID = 0;
    public final static int STGTY_STORAGE = 1;
    public final static int STGTY_STREAM = 2;
    public final static int STGTY_LOCKBYTES = 3;
    public final static int STGTY_PROPERTY = 4;
    public final static int STGTY_ROOT = 5;

    public final static int DE_RED = 0;
    public final static int DE_BLACK = 1;

    public final static int nameLength = 32;
    public final static int dirSize = 128;

    private final byte[] bname = new byte[64];
    private short _cb;
    private byte _mse;
    private byte _bflags;
    private int _leftSib, _rightSib, _child;
    private final byte[] _clsid = new byte[16];
    private int _dwUserFlags;
    private long _ctime, _mtime;
    private int _sectStart, _ulSize;
    private short _dptPropType;
    private String name;
    
    private int parent;
    
    public static enum Color {DE_RED, DE_BLACK, DE_INVALID};

    public DirectoryEntry() {
    }

    public void read(FileBuffer file) {
        file.get(bname);
        _cb = file.getShort();
        _mse = file.get();
        _bflags = file.get();
        _leftSib = file.getInt();
        _rightSib = file.getInt();
        _child = file.getInt();
        file.get(_clsid);
        _dwUserFlags = file.getInt();
        _ctime = file.getLong();
        _mtime = file.getLong();
        _sectStart = file.getInt();
        _ulSize = file.getInt();
        _dptPropType = file.getShort();
        readName();
    }

    public void write(FileBuffer file) {
        writeName();
        file.put(bname);        
        file.putShort(_cb);
        file.put(_mse);
        file.put(_bflags);
        file.putInt(_leftSib);
        file.putInt(_rightSib);
        file.putInt(_child);
        file.put(_clsid);
        file.putInt(_dwUserFlags);
        file.putLong(_ctime);
        file.putLong(_mtime);
        file.putInt(_sectStart);
        file.putInt(_ulSize);
        file.putShort(_dptPropType);
        writeName();
    }

    protected void readName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            short s = (short) (bname[i * 2] & 0xff);
            s += (bname[i * 2 + 1] & 0xff << 8);
            if (s == 0) {
                break;
            }
            sb.append((char) s);
        }
        name = sb.toString();
    }

    protected void writeName() {
        for (int i = 0; i < 32; i++) {
            if (i < name.length()) {
                char c = name.charAt(i);
                bname[i * 2] = (byte) (c & 0xff);
                bname[i * 2 + 1] = (byte) ((c & 0xff00) >>> 8);
            } else {
                bname[i * 2] = (byte) 0;
                bname[i * 2 + 1] = (byte) 0;
            }
        }
    }

    public String getName() {
        return (name);
    }

    public String getType() {
        switch (_mse) {
            case STGTY_INVALID:
                return ("STGTY_INVALID");
            case STGTY_STORAGE:
                return ("STGTY_STORAGE");
            case STGTY_STREAM:
                return ("STGTY_STREAM");
            case STGTY_LOCKBYTES:
                return ("STGTY_LOCKBYTES");
            case STGTY_PROPERTY:
                return ("STGTY_PROPERTY");
            case STGTY_ROOT:
                return ("STGTY_ROOT");
            default:
                return ("INVALID");
        }
    }

    public Color getColor() {
        switch (_bflags) {
            case DE_RED:
                return Color.DE_RED;
            case DE_BLACK:
                return Color.DE_BLACK;
            default:
                return Color.DE_INVALID;
        }
    }

    public void setColor(Color color) {
        switch (color) {
            case DE_RED:
                _bflags = DE_RED;
                break;
            case DE_BLACK:
                _bflags = DE_BLACK;
                break;
        }
    }
    public int getStartSect() {
        return (_sectStart);
    }

    public int getSize() {
        return (_ulSize);
    }
    
    public void setSize(int size) {
        _ulSize = size;
    }

    public int getLeftSib() {
        return (_leftSib);
    }
    
    public void setLeftSib(int l) {
        _leftSib = l;
    }

    public int getRightSib() {
        return (_rightSib);
    }
    
    public void setRightSib(int r) {
        _rightSib = r;
    }

    public int getChild() {
        return (_child);
    }
    
    public void setChild(int c) {
        _child = c;
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append(" (").append(getType()).append(")").append("\n");
        sb.append("Size: ").append(NumFormat.numToHex(_ulSize)).append("\n");
        sb.append("Color: ").append(getColor()).append("\n");
        sb.append("Start Sector: ").append(NumFormat.numToHex(_sectStart)).append("\n");
        sb.append("Siblings: L:").append(NumFormat.numToHex(_leftSib)).append(" R:")
                .append(NumFormat.numToHex(_rightSib)).append(" C:").append(NumFormat.numToHex(_child)).append("\n");
        sb.append("Create Time: ").append(new WindowsDate(_ctime).toString()).append("\n");
        sb.append("Modify Time: ").append(new WindowsDate(_mtime).toString()).append("\n");
        return (sb.toString());
    }

    public void wipe() {
        //vs.setInt("_mse", 0);
    }
    
    public int getParent() {
        return(parent);
    }
    
    public void setParent(int parent) {
        this.parent = parent;
    }
}
