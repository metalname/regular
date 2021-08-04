package registry.Cell;

import java.nio.ByteBuffer;
import registry.RegistryBuffer;
import registry.element.BinDataElement;
import registry.element.DataElement;
import registry.element.DataElementArray;
import registry.element.DataElementCollection;
import registry.element.ElementType;
import utils.NumFormat;

/**
 *
 * Defines a data cell
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#key-value
 */
public class DataCell extends RegistryCell {
    
    protected int parentlength = -1; // the size of the cell as defined by the parent
    protected DataElementArray elements;
    
    /**
     * Constructor
     * 
     * @param offset 
     */
    public DataCell(int offset) {
        super(offset);
    }
    
    @Override
    public RegistryCellType getCellType() {
        return(RegistryCellType.T_DATA);
    }
    
    @Override
    public DataElementCollection elements() {
        return(elements);
    }
    
    @Override
    public void elements(DataElementCollection collection) {
        this.elements = (DataElementArray) collection;
    }    
    
    @Override
    public boolean load(RegistryBuffer buffer) {
        super.loadSize(buffer);
        elements = new DataElementArray();
        if (length() < 0) {
            System.out.println("Negative array size loading data cell @" + NumFormat.numToHex(offset));
            System.exit(-1);
        }
        elements.addElement(DataElement.makeDataElement(ElementType.E_BINARY, length(), 0x04, "Data", false)).read(offset, buffer);
        return(true);
    }
    
    /**
     * Get cell data as ByteBuffer
     * 
     * @return 
     */
    public ByteBuffer getBuffer() {
        return(((BinDataElement) elements.get(0)).getBuffer());
    }
    
    /**
     * Get cell data as byte array
     * 
     * @return 
     */
    public byte[] getData() {
        return(((BinDataElement) elements.get(0)).getData());
    }
    
    /**
     * Set cell data from byte buffer
     * 
     * @param b 
     */
    public void setData(byte[] b) {
        ((BinDataElement) elements.get(0)).setData(b);
    }
    
    /**
     * Data cell does not have children - return empty array
     * 
     * @return 
     */
    @Override
    public int[] getChildIndexes() {
        return(new int[0]);
    }
    
    /**
     * Data cell does not have children
     * 
     * @param buffer
     * @return 
     */
    @Override
    public RegistryCell[] getChildCells(RegistryBuffer buffer) {
        return(new RegistryCell[0]);
    }
    
    @Override
    public String toString() {
        return ("[offset=" + NumFormat.numToHex(offset) + ",size=" + NumFormat.numToHex(size) + "] Data Cell");
    }
    
    /**
     * Wipe cell
     * 
     * @param buffer
     * @return 
     */
    @Override
    public RegistryCell wipe(RegistryBuffer buffer) {
        buffer.position(offset);
        size = Math.abs(size);        
        // check for suspicious size field
        // in the case of a corrupted registry, an incorrect size field
        // can result in this method overwriting large sections of the registry
        if (parentlength > 0) {
            // if the size of this cell is more than twice the size expected by the VK cell
            // use the parent length instead of the size field
            if (size > parentlength * 2) {
                size = parentlength;
            }
        }
        buffer.putInt(size);
        for (int i = 0; i < size - 4; i++) {
            buffer.putByte((byte) 0);
        }
        //recreate as data cell
        return(this);
    }    
        
}
