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
    
    private int linkInfoSize;                       // size of LinkInfo structure
    private int linkInfoHeaderSize;                 // size of header structure
    private int linkInfoFlags;                      // flags
    private int volumeIDOffset;                     // offset to VolumeID structure
    private int localBasePathOffset;                // offset to base path
    private int commonNetworkRelativeLinkOffset;    // offset to CommonNetworkRelativeLink structure
    private int commonPathSuffixOffset;             // offset to CommonPathSuffix structure
    private int localBasePathOffsetUnicode;         // offset to UNICODE version of local base path
    private int commonPathSuffixOffsetUnicode;      // offset to UNICODE version of CommonNetworkRelativeLink structure
    
    private boolean hasVolumeIDAndLocalBasePath = false;        // indicates presence or absence of VolumeID and LocalbasePath fields
    private boolean unicodeVolumeIDAndLocalBasePath = false;    // indicates if the above fields are UNICODE
    
    private ShellLinkVolumeID volumeID;
    private String localBasePath;
    
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
        setFlags();
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
        loadVolumeID();
        loadLocalBasePath();
    }
    
    /**
     * Checks if VolumeID and LocalBasePath are present, and if we need to use the UNICODE versions
     */
    private void setFlags() {
        // utterly moronic conditions to determine flags
        // seriously Microsoft, what the fuck?
        
        // check bit in LinkInfoFlags
        hasVolumeIDAndLocalBasePath = ((linkInfoFlags & FLAG_VolumeIDAndLocalBasePath) != 0);
        // now check size of header to determine if UNICODE versions should be used
        if (hasVolumeIDAndLocalBasePath) {
            unicodeVolumeIDAndLocalBasePath = (linkInfoHeaderSize >= 0x24);
        }
    }
    
    /**
     * Loads VolumeID structure if present
     */
    private void loadVolumeID() {
        if (hasVolumeIDAndLocalBasePath && (volumeIDOffset > 0)) {
            volumeID = new ShellLinkVolumeID(buffer, offset + volumeIDOffset);
        }
    }
    
    private void loadLocalBasePath() {
        if (hasVolumeIDAndLocalBasePath) {
            if (unicodeVolumeIDAndLocalBasePath && (localBasePathOffsetUnicode > 0)) {
                localBasePath = ShellLink.loadUNICODEString(buffer, offset + localBasePathOffsetUnicode);
            } else {
                localBasePath = ShellLink.loadASCIIString(buffer, offset + localBasePathOffset);
            }
        }
    }
}
