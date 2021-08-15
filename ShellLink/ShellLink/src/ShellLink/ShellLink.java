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
    
    public ShellLink(ByteBuffer buffer) {
        this.buffer = buffer;
        load();
    }
    
    private void load() {
        header = new ShellLinkHeader(buffer);
        System.out.println(header.listFlags());
        System.out.println(header.listAttributes());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            try (RandomAccessFile raf = new RandomAccessFile("/home/metataro/100CANON.lnk", "r")) {
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
