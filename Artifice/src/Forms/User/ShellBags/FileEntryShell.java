/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.ShellBags;

import Forms.User.ShellBags.Extensions.ExtensionBlock;
import BinBuffer.BinBuffer;

/**
 *
 * @author 
 */
public class FileEntryShell {

    private final BinBuffer buffer;
    private int length;
    private int clsid;
    private int subtype;
    private int size;
    private FATDate modifiedDT;
    private int attributes;
    private String primaryname;
    private ExtensionBlock extensionBlock;

    public FileEntryShell(BinBuffer buffer) {
        this.buffer = buffer;
        unpack();
    }

    private static int offsetModifiedDT = 0x08;
    
    private void unpack() {
        buffer.position(0);
        length = buffer.getShort();
        clsid = buffer.getByte();
        subtype = clsid & 0x70;
        buffer.position(0x04);
        size = buffer.getInt();
        buffer.position(offsetModifiedDT);        
        modifiedDT = new FATDate(buffer.getInt());
        attributes = buffer.getShort();
        // subtype determines if primary name is UNICODE or ASCII
        if ((subtype & 0x04) != 0) {
            primaryname = buffer.getUnicodeStringZ(buffer.remaining());
        } else {
            primaryname = buffer.getAsciiStringZ(buffer.remaining());
        }
        // ASCII name is 16-bit aligned
        // need to shift the buffer up by one byte if the string length is odd (including zero terminator)
        if ((subtype & 0x04) == 0) {
            if ((primaryname.length() % 2) == 0) {
                buffer.skip(1);
            }
        }
        // look for extension block
        extensionBlock = ExtensionBlock.makeExtensionBlock(buffer);
        if (extensionBlock != null) {
            if (extensionBlock.toString() != null) {
                primaryname = extensionBlock.toString();
            }
        }
    }
    
    public FATDate modifiedDT() {
        return(modifiedDT);
    }
    
    public void modifiedDT(FATDate dt) {
        buffer.position(offsetModifiedDT);
        modifiedDT = dt;
        buffer.putInt(dt.timestamp());
    }
    
    public String primaryname() {
        return(primaryname);
    }
    
    public ExtensionBlock extensionBlock() {
        return(extensionBlock);
    }
    
}
