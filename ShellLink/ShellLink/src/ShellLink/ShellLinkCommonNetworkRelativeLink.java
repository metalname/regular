package ShellLink;

import java.nio.ByteBuffer;

/**
 *
 * Defines a CommonnetworkRelativeLink structure
 */
public class ShellLinkCommonNetworkRelativeLink {
    
    private final ByteBuffer buffer;
    private final int offset;
    
    private int commonNetworkRelativeLinkSize;
    private int commonNetworkRelativeLinkFlags;    
    private int netNameOffset;
    private int deviceNameOffset;
    private int networkProviderType;
    private int netNameOffsetUnicode = 0;
    private int deviceNameOffsetUnicode = 0;
    private String netName;
    private String deviceName;
    
    public ShellLinkCommonNetworkRelativeLink(ByteBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        load();
    }
    
    private void load() {
        buffer.position(offset);
        
        commonNetworkRelativeLinkSize = buffer.getInt();
        commonNetworkRelativeLinkFlags = buffer.getInt();
        netNameOffset = buffer.getInt();
        deviceNameOffset = buffer.getInt();
        networkProviderType = buffer.getInt();
        
        // more idiotic conditional fields
        if (netNameOffset > 0x14) {
            netNameOffsetUnicode = buffer.getInt();
        }
        if (deviceNameOffset > 0x14) {
            deviceNameOffsetUnicode = buffer.getInt();
        }
        
        loadNetName();
        loadDeviceName();
    }
    
    private void loadNetName() {
        if (netNameOffsetUnicode > 0) {
            netName = ShellLink.loadUNICODEString(buffer, offset + netNameOffsetUnicode);
        } else {
            netName = ShellLink.loadASCIIString(buffer, offset + netNameOffset);
        }
    }
    
    private void loadDeviceName() {
        if (deviceNameOffsetUnicode > 0) {
            deviceName = ShellLink.loadUNICODEString(buffer, offset + deviceNameOffsetUnicode);
        } else if (deviceNameOffset > 0){
            deviceName = ShellLink.loadASCIIString(buffer, offset + deviceNameOffset);
        }        
    }
}
