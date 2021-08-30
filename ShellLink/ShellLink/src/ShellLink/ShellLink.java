package ShellLink;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * Defines a MS-SHLLNK structure
 * 
 * https://winprotocoldoc.blob.core.windows.net/productionwindowsarchives/MS-SHLLINK/%5bMS-SHLLINK%5d-210407.pdf
 */
public class ShellLink {
    
    private final ByteBuffer buffer;
    private ShellLinkHeader header;
    private ShellLinkInfo linkInfo;
    private ShellLinkStringData stringData;
    
    public ShellLink(ByteBuffer buffer) throws ShellLinkException {
        this.buffer = buffer;
        load();
    }
    
    private void load() throws ShellLinkException {
        header = new ShellLinkHeader(buffer);
        System.out.println(header.listFlags());
        System.out.println(header.listAttributes());
        if (header.hasLinkFlags(ShellLinkFlags.HasLinkInfo)) {
            linkInfo = new ShellLinkInfo(buffer, buffer.position());
        }
        stringData = new ShellLinkStringData(buffer, buffer.position(), header);
    }
    
    public static String loadASCIIString(ByteBuffer buffer, int offset) {
        return (loadString(buffer, offset, false));    
    }
    
    public static String loadUNICODEString(ByteBuffer buffer, int offset) {
        return (loadString(buffer, offset, true));
    }
    
    public static String loadString(ByteBuffer buffer, int offset, boolean isUnicode) {
        buffer.position(offset);
        var sb = new StringBuilder();
        int ch = 0;
        do {
            if (isUnicode) {
                ch = buffer.getShort();
            } else {
                ch = buffer.get();
            }
            if (ch != 0) {
                sb.append((char) ch);
            }
        } while (ch != 0);
        return (sb.toString());        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            try (RandomAccessFile raf = new RandomAccessFile("/home/metataro/14668049.pdf.lnk", "r")) {
                var b = new byte[(int) raf.length()];
                raf.read(b);
                var buffer = ByteBuffer.wrap(b);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                var lnk = new ShellLink(buffer);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
}
