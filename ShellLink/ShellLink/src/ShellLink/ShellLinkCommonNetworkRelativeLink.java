package ShellLink;

import java.nio.ByteBuffer;

/**
 *
 * Defines a CommonnetworkRelativeLink structure
 */
public class ShellLinkCommonNetworkRelativeLink {
    
    private final ByteBuffer buffer;
    private final int offset;
    
    public ShellLinkCommonNetworkRelativeLink(ByteBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
    }
}
