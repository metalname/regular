/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.ShellBags.Extensions;

import Forms.User.ShellBags.FATDate;
import BinBuffer.BinBuffer;

/**
 *
 * @author 
 */
public class ExBEEF0004 extends ExtensionBlock {
    
    private FATDate createDT;
    private FATDate accessDT;
    private int winVersion;
    private long fileRef;
    private int longStringSize;
    private String localizedName;
    private String name;
    private int offset;
    
    public ExBEEF0004(BinBuffer buffer) {
        super(buffer);
        offset = buffer.position();
        unpack();
    }
    
    private void unpack() {
        buffer.position(offset);
        createDT = new FATDate(buffer.getInt());
        accessDT = new FATDate(buffer.getInt());
        winVersion = buffer.getShort();
        if (version >= 7) {
            buffer.skip(2);
            fileRef = buffer.getLong();
            buffer.skip(8);
        }
        if (version >= 3) {
            longStringSize = buffer.getShort();
        }
        if (version >= 9) {
            buffer.skip(4);
        }
        if (version >= 8) {
            buffer.skip(4);
        }
        if (version >= 3) {
            name = buffer.getUnicodeStringZ(buffer.remaining());
        }
        if ((version >= 3) && (longStringSize > 0)) {
                localizedName = buffer.getAsciiStringZ(buffer.remaining());
        }
        if ((version >= 7) && (longStringSize > 0)) {
                localizedName = buffer.getUnicodeStringZ(buffer.remaining());
        }
    }
    
    @Override
    public String toString() {
        if (localizedName != null) {
            return(name + "," + localizedName);
        } else {
            return(name);
        }
    }
    
    public FATDate createDT() {
        return(createDT);
    }
    
    public void createDT(FATDate dt) {
        createDT = dt;
        buffer.position(offset);
        buffer.putInt(dt.timestamp());
    }
    
    public FATDate accessDT() {
        return(accessDT);
    }
    
    public void accessDT(FATDate dt) {
        accessDT = dt;
        buffer.position(offset);
        buffer.skip(4);
        buffer.putInt(dt.timestamp());
    }
    
    @Override
    public ExtensionType type() {
        return(ExtensionType.E_BEEF0004);
    }
    
}
