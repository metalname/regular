package registry.element;

import registry.RegistryBuffer;


/**
 *
 * INTEGER data element
 */
public class IntDataElement extends DataElement implements NumberDataElement {

    private int value;

    public IntDataElement(final ElementType type, 
                       final int length, 
                       final int offset, 
                       final String label, 
                       final boolean follow) {    
        super(type, length, offset, label, follow);
    }

    // read int value from file
    @Override
    public int read(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        value = buffer.getInt();
        //unset follow if value is -1
        if (value == -1) {
            follow = false;
        }
        return (offset + 4);        
    }

    // write int value to file
    @Override
    public void write(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        buffer.putInt(value);
    }
    
    // set data value
    public void setValue(final int value) {
        this.value = value;
    }

    // returns string representation of value as hex
    @Override
    public String toString() {
        return (FormatElement.toHexString(value));
    }

    // return data value
    @Override
    public int getValueI() {
        return (value);
    }
    
    // set data value from string
    @Override
    public boolean setData(final String data) {
        try {
            value = Integer.parseInt(data, 16);
            return(true);
        } catch (NumberFormatException e) {
            return(false);
        }
    }
    
    @Override
    public void setValueI(int value) {
        this.value = value;
    }
    
    @Override
    public int getSize() {
        return(4);
    }

}
