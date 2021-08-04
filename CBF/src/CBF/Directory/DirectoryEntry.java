package CBF.Directory;

import java.nio.ByteBuffer;

/**
 *
 * Directory entry class
 * Each directory entry is a 128-byte structure
 */
public class DirectoryEntry {
    
    // directory entry types
    // see objectType field below
    public static final int TYPE_UNALLOCATED = 0x00;
    public static final int TYPE_STORAGE = 0x01;
    public static final int TYPE_STREAM = 0x02;
    public static final int TYPE_ROOT = 0x05;
    
    // color indicators
    // see color attribute below
    public static final int COLOR_RED = 0x00;
    public static final int COLOR_BLACK = 0x01;
    
    private final byte[] nameBuffer = new byte[64];     // byte buffer containig Unicode directory name
    private short nameLength;                           // length of directory name
    private byte objectType;                            // type of entry
    private byte color;                                 // directory is a RED/BLACK tree
    private int leftSib, rightSib, child;               // stream ID of left, right and child nodes
    private final byte[] clsid = new byte[16];          // CLSID - object class GUID
    private int stateBits;                              // generally not used for streams
    private long ctime, mtime;                          // creation and modifiication timestamps
    private int startSect;                              // starting sector for stream, root object contains start of miniStream
    private long streamSize;                            // length of stream in bytes
    private String name;                                // decoded Unicode name
    
    private final ByteBuffer buffer;
    
    /**
     * Constructor
     * This method assumes that the buffer is already positioned at the correct offset
     * 
     * @param buffer 
     */
    public DirectoryEntry(ByteBuffer buffer) {
        this.buffer = buffer;
        load();
    }
    
    /**
     * Loads the directory fields from the buffer
     */
    private void load() {
        buffer.get(nameBuffer);
        nameLength = buffer.getShort();
        objectType = buffer.get();
        color = buffer.get();
        leftSib = buffer.getInt();
        rightSib = buffer.getInt();
        child = buffer.getInt();
        buffer.get(clsid);
        stateBits = buffer.getInt();
        ctime = buffer.getLong();
        mtime = buffer.getLong();
        startSect = buffer.getInt();
        streamSize = buffer.getLong();
        
        readName();
    }
    
    /**
     * Builds the name from the name buffer
     */
    private void readName() {
        StringBuilder sb = new StringBuilder();
        // convert each 2-byte set to a short
        for (int i = 0; i < 32; i++) {
            short s = (short) (nameBuffer[i * 2] & 0xff);
            s += (nameBuffer[i * 2 + 1] & 0xff << 8);
            
            // check if we reached the end of the string (null-terminated)
            if (s == 0) {
                break;
            }
            sb.append((char) s);    // append the Unicode character
        }
        name = sb.toString();
    }    
    
    /**
     * Getter for name
     * 
     * @return 
     */
    public String getName() {
        return(name);
    }
    
    /**
     * Dump attributes for debugging
     * 
     * @return 
     */
    public String dump() {
        var sb = new StringBuilder();
        sb.append("Name: ").append(name).
                append(", Type: ").append(translateObjectType()).
                append(", Left: ").append(leftSib).
                append(", Right: ").append(rightSib).
                append(", Child: ").append(child).
                append(", Sector: ").append(startSect).
                append(", Size: ").append(streamSize);
        return(sb.toString());
    }
    
    /**
     * Getter for objectType
     * 
     * @return 
     */
    public int getObjectType() {
        return(objectType);
    }
    
    public String translateObjectType() {
        switch (objectType) {
            case TYPE_UNALLOCATED:
                 return("UNALLOCATED");                 
            case TYPE_STORAGE:
                return("STORAGE");
            case TYPE_STREAM:
                return("STREAM");
            case TYPE_ROOT:
                return("ROOT");
            default:
                return("INVALID-" + Integer.toHexString(objectType));
        }
    }
        
    // color enum
    public enum Color {
        DE_RED, DE_BLACK, DE_INVALID;
    }
    
    /**
     * Convert color to enum
     * 
     * @return 
     */
    public Color getColor() {
        switch(color) {
            case COLOR_RED:
                return(Color.DE_RED);
            case COLOR_BLACK:
                return(Color.DE_BLACK);
            default:
                return(Color.DE_INVALID);
        }
    }
    
    /**
     * Getter for leftSib
     * 
     * @return 
     */
    public int getLeftSib() {
        return(leftSib);
    }
    
    /**
     * Getter for rightSib
     * 
     * @return 
     */
    public int getRightSib() {
        return(rightSib);
    }
    
    /**
     * Getter for child
     * @return 
     */
    public int getChild() {
        return(child);
    }
    
    /**
     * Getter for startSect
     * @return 
     */
    public int getStartSect() {
        return(startSect);
    }
    
    /**
     * Getter for streamSize
     * 
     * @return 
     */
    public long getStreamSize() {
        return(streamSize);
    }
}
