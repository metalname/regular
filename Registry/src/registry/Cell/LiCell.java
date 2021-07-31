package registry.Cell;

import registry.RegistryBuffer;
import registry.element.DataElement;
import registry.element.DataElementArray;
import registry.element.ElementType;
import registry.element.IntDataElement;
import registry.element.NumberDataElement;
import utils.NumFormat;

/**
 *
 * Defines an index leaf (li) cell
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#index-leaf
 */
public class LiCell extends IndexCell {
    
    /**
     * Constructor
     * 
     * @param offset 
     */
    public LiCell(int offset) {
        super(offset);
    }
        
    @Override
    public RegistryCellType getCellType() {
        return(RegistryCellType.T_LI);
    }
    
    /**
     * Create attribute elements
     * 
     * @param buffer
     * @return 
     */
    @Override
    public boolean load(RegistryBuffer buffer) {
        // call superclass to load cell length and signature
        super.loadHeader(buffer);
        
        // create attribute elements
        elements = new DataElementArray();
        
        // read number of array entries from registry buffer
        elements.addElement(DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x06, "Number of Entries", false)).read(offset, buffer);
        ix_numEntries = elements.lastElement();        
        numEntries = ((NumberDataElement) elements.get(ix_numEntries)).getValueI();

        // load array elements
        // each array element is a 4-byte integer offset
        int j = 0x08;
        for (int i = 0; i < getNumEntries(); i++) {
            DataElement intElement = DataElement.makeDataElement(ElementType.E_INT, 0, j, "Offset #" + i, true);
            j = intElement.read(offset, buffer);
            
            if (((NumberDataElement) intElement).getValueI() > 0) {
                elements.add(intElement);            
            } else {
                break;
            }
        }
        return(true);
    }
    
    /**
     * Get offset for specified index
     * 
     * @param index
     * @return 
     */
    @Override
    public int getOffset(int index) {
        int i = index + 1 + ix_numEntries;
        if (i < elements.size()) {
            return (((IntDataElement) elements.get(i)).getValueI());        
        } else {
            return(-1);
        }        
    }
    
    /**
     * Set offset for specified index
     * 
     * @param index
     * @param offset 
     */
    @Override
    public void setOffset(int index, int offset) {
        int i = index + 1 + ix_numEntries;
        if (i < elements.size()) {
            ((IntDataElement) elements.get(i)).setValueI(offset);
        }        
    }    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[offset=").append(NumFormat.numToHex(offset)).append(",size=").append(NumFormat.numToHex(size)).append("] li Cell (");
        for (int index = 0; index < getNumEntries(); index++) {
            if (index > 0) {
                sb.append(",");
            }
            sb.append(NumFormat.numToHex(getOffset(index)));
        }
        sb.append(")");
        return (sb.toString());
    }
    
    /**
     * Decrease number of array elements by 1
     */
    @Override
    public void decrementEntries() {
        numEntries--;
        ((NumberDataElement) elements.get(ix_numEntries)).setValueI(numEntries);
    }
    
    /**
     * Delete array entry at specified index
     * 
     * @param index 
     */
    @Override
    protected void deleteCellIndexAt(int index)  {
        // move array entries up by 1, overwriting specified index
        for (int i = index; i < getNumEntries() - 1; i++) {
            setOffset(i, getOffset(i + 1));
        }
        // overwrite last element
        setOffset(getNumEntries() - 1, -1);        
    }        

}
