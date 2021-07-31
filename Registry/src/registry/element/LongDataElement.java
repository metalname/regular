package registry.element;

import registry.RegistryBuffer;

/**
 * LONG data element
 */
public class LongDataElement extends DataElement {

    protected long value;

    public LongDataElement(final ElementType type, 
                       final int length, 
                       final int offset, 
                       final String label, 
                       final boolean follow) {
        super(type, length, offset, label, follow);
    }

    // read value from file as long
    @Override
    public int read(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        value = buffer.getLong();
        return (offset + 8);
    }

    // write value to file as long
    @Override
    public void write(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        buffer.putLong(value);
    }

    // format value as hex string
    @Override
    public String toString() {
        return (FormatElement.toHexString(value));
    }
    
    /**
     *
     * @param data
     * @return
     */
    @Override
    public boolean setData(final String data) {
        try {
            long newValue = Long.parseLong(data, 16);
            value = newValue;
            return(true);
        } catch (NumberFormatException e) {
            return(false);
        }
    }
    
    @Override
    public int getSize() {
        return(8);
    }    
    
    public long getValueL() {
        return(value);
    }
    
    public void setValueL(long value) {
        this.value = value;
    }
}
