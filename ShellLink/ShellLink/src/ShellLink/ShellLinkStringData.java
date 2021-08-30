package ShellLink;

import java.nio.ByteBuffer;

/**
 *
 * @author metataro
 */
public class ShellLinkStringData {

    private final ByteBuffer buffer;
    private final int offset;
    private final ShellLinkHeader header;
    private final boolean isUnicode;

    private String NAME_STRING = "";
    private String RELATIVE_PATH = "";
    private String WORKING_DIR = "";
    private String COMMAND_LINE_ARGUMENTS = "";
    private String ICON_LOCATION = "";

    public ShellLinkStringData(ByteBuffer buffer, int offset, ShellLinkHeader header) {
        this.buffer = buffer;
        this.offset = offset;
        this.header = header;
        isUnicode = header.hasLinkFlags(ShellLinkFlags.IsUnicode);
        load();
    }

    private void load() {
        buffer.position(offset);

        if (header.hasLinkFlags(ShellLinkFlags.HasName)) {
            NAME_STRING = readString();
        }
        
        if (header.hasLinkFlags(ShellLinkFlags.HasRelativePath)) {
            RELATIVE_PATH = readString();
        }
        
        if (header.hasLinkFlags(ShellLinkFlags.HasWorkingDir)) {
            WORKING_DIR = readString();
        }
        
        if (header.hasLinkFlags(ShellLinkFlags.HasArguments)) {
            COMMAND_LINE_ARGUMENTS = readString();
        }                
        
        if (header.hasLinkFlags(ShellLinkFlags.HasIconLocation)) {
            ICON_LOCATION = readString();
        }                
        
    }
    
    private String readString() {
        // string data is here stored with a leading short indicating length
        // in typical Microsoft fashion, this is inconsistent with all other 
        //   strings in the ShellLink structure which are zero-terminated
        
        var sb = new StringBuilder();
        
        // read length
        int length = buffer.getShort();
        
        // read string data
        var ch = 0;
        for (int i = 0; i < length; i++) {
            if (isUnicode) {
                ch = buffer.getShort();
            } else {
                ch = buffer.get();
            }
            sb.append((char) ch);
        }
        
        return (sb.toString());
    }
}
