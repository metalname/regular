package registry.element;

import java.nio.ByteBuffer;
import registry.RegistryBuffer;

/**
 *
 * UNICODE String - zero terminated
 */
public class UStrZDataElement extends DataElement implements StringDataElement {

    private String value;
    private int size;

    public UStrZDataElement(final ElementType type,
            final int length,
            final int offset,
            final String label,
            final boolean follow) {
        super(type, length, offset, label, follow);
    }

    // set data from byte buffer
    public void setData(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = buffer.getChar();
            if (c == 0) {
                break;
            }
            sb.append(c);
        }
        value = sb.toString();
    }

    // read UNICODE string from file, zero-terminated
    @Override
    public int read(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        StringBuilder sb = new StringBuilder();
        char c = 0;
        for (int i = 0; i < length; i++) {
            if ((c = buffer.getChar()) != 0) {
                sb.append(c);
            } else {
                break;
            }
        }
        value = sb.toString();
        // calculate new offset
        if (c == 0) {
            size = (sb.length() + 1) * 2;
        } else {
            size = sb.length() * 2;
        }
        if (c == 0) {
            return (offset + size);
        } else {
            return (offset + size);
        }
    }

    // write UNICODE value to file
    @Override
    public void write(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        for (int i = 0; i < value.length(); i++) {
            buffer.putChar(value.charAt(i));
        }
        buffer.putChar((char) 0);        
    }

    @Override
    public String toString() {
        return (value);
    }

    @Override
    public boolean setData(final String data) {
        value = data;        
        return (true);
    }
    
    @Override
    public String getValueS() {
        return(value);
    }    
    
    @Override
    public int getSize() {
        return(size);
    }
}
