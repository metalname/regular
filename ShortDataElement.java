package registry.element;

import registry.RegistryBuffer;

/**
 *
 * SHORT data element
 */
public class ShortDataElement extends DataElement implements NumberDataElement {

    protected short value;

    public ShortDataElement(final ElementType type, 
                       final int length, 
                       final int offset, 
                       final String label, 
                       final boolean follow) {
        super(type, length, offset, label, follow);
    }

    // read value from file as short
    @Override
    public int read(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        value = buffer.getShort();
        return (offset + 2);
    }

    // write value to file as short
    @Override
    public void write(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        buffer.putShort(value);
    }

    @Override
    public String toString() {
        return (FormatElement.toHexString(value));
    }
    
    @Override
    public boolean setData(final String data) {
        try {
            short newValue = Short.parseShort(data, 16);
            value = newValue;
            return(true);
        } catch(NumberFormatException e) {
            return(false);
        }
    }
    
    @Override
    public int getValueI() {
        return((int) value);
    }
    
    @Override
    public void setValueI(int value) {
        this.value = (short) value;
    }
    
    @Override
    public int getSize() {
        return(2);
    }  
    
    @Override
    public void setValue(int value) {
        setValueI(value);
    }
}
