package ShellLink;

import java.nio.ByteBuffer;

/**
 *
 * Defines a ShellLinkHeader structure
 */
public class ShellLinkHeader {
    
    private final ByteBuffer buffer;    // byte buffer containing file data
    private final int offset;           // offset into buffer at whihc header starts (should be 0)
    
    private int headerSize;
    private final byte[] CLSID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int flags;
    private int attributes;
    private final byte[] creationTime = {0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] accessTime = {0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] writeTime = {0, 0, 0, 0, 0, 0, 0, 0};
    private int fileSize;
    private int iconIndex;
    private int showCommand;
    private short hotKey;
    private final byte[] reserved = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] idList;
    
    public ShellLinkHeader(ByteBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        load();
    }
    
    public ShellLinkHeader(ByteBuffer buffer) {
        this(buffer, 0);
    }
    
    private void load() {
        buffer.position(offset);
        headerSize = buffer.getInt();
        buffer.get(CLSID);
        flags = buffer.getInt();
        attributes = buffer.getInt();
        buffer.get(creationTime);
        buffer.get(accessTime);
        buffer.get(writeTime);
        fileSize = buffer.getInt();
        showCommand = buffer.getInt();
        hotKey = buffer.getShort();
        
        // if HasLinkTargetIDList is set, ID list follows header
        if (hasLinkFlags(ShellLinkFlags.HasLinkTargetIDList)) {
            
        }
    }
    
    /**
     * Load the ID List
     */
    private void loadIdList() {
        short idListSize = buffer.getShort();
        idList = new byte[idListSize];
        buffer.get(idList);
    }
    
    /**
     * Check if given flag is set
     * 
     * @param flag
     * @return 
     */
    public boolean hasLinkFlags(ShellLinkFlags flag) {
        return ((flags & flag.mask) != 0);
    }
    
    /**
     * Getter for flags
     * 
     * @return 
     */
    public int flags() {
        return(flags);
    }
    
    /**
     * Dump flags to string for debugging
     * 
     * @return 
     */
    public String listFlags() {
        var sb = new StringBuilder();
        for (var e: ShellLinkFlags.values()) {
            if ((flags & e.mask) != 0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(e);
            }
        }
        return(sb.toString());
    }
    
    /**
     * Dump attributes to string for debugging
     * 
     * @return 
     */
    public String listAttributes() {
        var sb = new StringBuilder();
        for (var e: ShellFileAttributes.values()) {
            if ((attributes & e.mask) != 0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(e);
            }
        }
        return(sb.toString());
    }
    
    
}
