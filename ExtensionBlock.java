/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.ShellBags.Extensions;

import BinBuffer.BinBuffer;
import utils.NumFormat;

/**
 *
 * @author 
 */
public abstract class ExtensionBlock {
    
    protected BinBuffer buffer;
    protected int offset;
    public int size;
    public int version;
    
    public ExtensionBlock(BinBuffer buffer) {
        this.buffer = buffer;
        offset = buffer.position();
        size = buffer.getShort();
        version = buffer.getShort();
        buffer.skip(4);
    }
    
    public static ExtensionBlock makeExtensionBlock(BinBuffer buffer) {
        // check if buffer is long enough for an extension block
        if (buffer.remaining() > 8) {
            // store position
            int position = buffer.position();
            // read size
            int size = buffer.getShort();
            int version = buffer.getShort();
            byte[] signature = new byte[4];
            buffer.get(signature);
            switch (getExtensionType(signature)) {
                case E_BEEF0004:
                    buffer.position(position);
                    return(new ExBEEF0004(buffer));
            }
            return(null);
        } else {
            return(null);
        }
    }
    
    public abstract ExtensionType type();
    
    public static enum ExtensionType {E_UNKNOWN, E_BEEF0004; }
    
    private static ExtensionType getExtensionType(byte[] signature) {
        if (((signature[3] & 0xff) == 0xbe) &&
            ((signature[2] & 0xff) == 0xef) &&
            ((signature[1] & 0xff) == 0x00) &&
            ((signature[0] & 0xff) == 0x04)) {
            return(ExtensionType.E_BEEF0004);
        } else {
            BinBuffer b = new BinBuffer(signature);
            System.out.println("Warning - unhandled extension type " + NumFormat.numToHex(b.getInt()));
            return(ExtensionType.E_UNKNOWN);
        }
    }
    
}
