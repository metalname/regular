package registry.Cell;

import registry.RegistryBuffer;
import registry.element.DataElement;
import registry.element.DataElementArray;
import registry.element.DataElementCollection;
import registry.element.ElementType;
import registry.element.IntDataElement;
import utils.NumFormat;

/**
 *
 * Defines a security key (sk) cell
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#key-security
 */
public class SkCell extends RegistryCell {
    
   // defines a security key entry
    
    protected final static int flink = 0;
    protected final static int blink = 1;
    protected final static int refCount = 2;
    protected final static int descLength = 3;
    protected final static int revision = 4;
    protected final static int control = 5;
    protected final static int offsetOwner = 6;
    protected final static int offsetGroup = 7;
    protected final static int offsetSACL = 8;
    protected final static int offsetDACL = 9;
    
    protected DataElementArray elements;
    
    
    /*********************************************************************************
     * Len  Start   Name
     * 4    0x00    Size
     * 2    0x04    Signature
     * 2    0x06    Reserved
     * 4    0x08    Flink
     * 4    0x0c    Blink
     * 4    0x10    Reference Count
     * 4    0x14    Descriptor length
     * 1    0x18    Revision
     * 1    0x19    Spare
     * 2    0x1a    Control
     * 4    0x1c    Offset to Owner
     * 4    0x20    Offset to Group
     * 4    0x24    Offset to SACL
     * 4    0x28    Offset to DACL
     * 
     */
    
    /**
     * Constructor
     * 
     * @param offset 
     */
    public SkCell(int offset) {
        super(offset);
    }
        
    @Override
    public RegistryCellType getCellType() {
        return(RegistryCellType.T_SK);
    }   
    
    @Override
    public DataElementCollection elements() {
        return(elements);
    }
    
    @Override
    public void elements(DataElementCollection collection) {
        this.elements = (DataElementArray) collection;
    }
        
    /**
     * Create and load cell attributes
     * 
     * @param buffer
     * @return 
     */
    @Override
    public boolean load(RegistryBuffer buffer)  {
        // call superclass to load cell size and signature        
        super.loadHeader(buffer);  
        
        // create attribute array
        elements = new DataElementArray();
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x08, "Forward Link", true)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x0c, "Backward Link", true)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x10, "Reference Count", false)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x14, "Descriptor Length", false)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x18, "Revision", false)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x1a, "Control", false)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x1c, "Offset to Owner", false)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x20, "Offset to Group", false)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x24, "Offset to SACL", false)).read(offset, buffer);
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x28, "Offset to DACL", false)).read(offset, buffer);
        return(true);
    }

    @Override
    public String toString() {
        return("[offset=" + NumFormat.numToHex(offset) + ",size=" + NumFormat.numToHex(size) + "] Security Key (sk) Cell");
    }

    protected int elementValue(int index) {
        return(((IntDataElement) elements.get(index)).getValueI());
    }
    
    @Override
    public void delete(RegistryBuffer buffer) {
        // TODO
    }
    
    @Override
    public RegistryCell wipe(RegistryBuffer buffer) {
        return(this);
    }    
    
    @Override
    public int[] getChildIndexes() {
        return(new int[0]);
    }
    
    @Override
    public RegistryCell[] getChildCells(RegistryBuffer buffer) {
        return(new RegistryCell[0]);
    }    
    
}
