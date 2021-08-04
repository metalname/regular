package registry.element;

import registry.RegistryBuffer;

/**
 *
 * ASCII String Element - fixed length
 */
public class AStrLDataElement extends DataElement implements StringDataElement {

    private String value;
    private int size;
    
    public AStrLDataElement(final ElementType type, 
                           final int length, 
                           final int offset, 
                           final String label, 
                           final boolean follow) {
        super(type, length, offset, label, follow);
    }

    // read the string from a file
    @Override
    public int read(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        final StringBuilder sb = new StringBuilder();
        // read bytes from file until 0 or length reached
        byte b = 0;
        for (int i = 0; i < length; i++) {
            if ((b = buffer.getByte()) != 0) {
                sb.append((char) b);
            } else {
                break;
            }
        }
        value = sb.toString();
        // calclulate the new offset
        if (b ==0) {
            size = sb.length() + 1;
        } else {
            size = sb.length();
        }
        return(length);
    }

    // write the string value to a file
    @Override
    public void write(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        int i;
        for (i = 0; i < value.length(); i++) {
            buffer.putByte((byte) value.charAt(i));
        }
        // if the string length is less than the specified length, write a 0
        if (i < length) {
            buffer.putByte((byte) 0);
        }
    }
    
    @Override
    public String toString() {
        return(value);
    }
    
    @Override
    public boolean setData(final String data) {
        value = data;        
        return(true);
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
