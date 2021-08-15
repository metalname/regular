package ShellLink;

import java.nio.ByteBuffer;

/**
 *
 * Defines a ShellLinkInfo structure
 */
public class ShellLinkInfo {
    
    private static final int FLAG_VolumeIDAndLocalBasePath = 0x01;
    private static final int FLAG_CommonNetworkRelativeLinkAndPathSuffix = 0x02;
    
    private final ByteBuffer buffer;
    private final int offset;
    
    private int linkInfoSize;
    private int linkInfoHeaderSize;
    private int linkInfoFlags;
    private int volumeIDOffset;
    private int localBasePathOffset;
    private int commonNetworkRelativeLinkOffset;
    private int commonPathSuffixOffset;
    private int localBasePathOffsetUnicode;
    private int commonPathSuffixOffsetUnicode;
    
    public ShellLinkInfo(ByteBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        load();
    }
    
    private void load() {
        buffer.position(offset);
        
        linkInfoSize = buffer.getInt();
        linkInfoHeaderSize = buffer.getInt();
        linkInfoFlags = buffer.getInt();
        volumeIDOffset = buffer.getInt();
        localBasePathOffset = buffer.getInt();
        commonNetworkRelativeLinkOffset = buffer.getInt();
        commonPathSuffixOffset = buffer.getInt();
        // LocalBasePathOffsetUnicode and CommonPathSuffixOffsetUnicode are 
        // present only if LinkInfoHeaderSize is >= 0x24
        if (linkInfoHeaderSize >= 0x24) {
            localBasePathOffsetUnicode = buffer.getInt();
            commonPathSuffixOffsetUnicode = buffer.getInt();
        }
    }
}
