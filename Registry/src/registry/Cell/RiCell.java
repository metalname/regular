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
 * Defines an index root (ri) cell
 * This is an array of arrays of pointers to child cells
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#index-root
 */
public class RiCell extends IndexCell {

    /**
     * Constructor
     * 
     * @param offset 
     */
    public RiCell(int offset) {
        super(offset);
    }

    @Override
    public RegistryCellType getCellType() {
        return (RegistryCellType.T_RI);
    }

    /**
     * Create and load attribute elements
     * 
     * @param buffer
     * @return 
     */
    @Override
    public boolean load(RegistryBuffer buffer) {
        // call superclass to load cell size and signature
        super.loadHeader(buffer);
        
        // get number of elements in array
        elements = new DataElementArray();
        elements.addElement(DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x06, "Number of Entries", false)).read(offset, buffer);
        ix_numEntries = elements.lastElement();
        numEntries = ((NumberDataElement) elements.get(ix_numEntries)).getValueI();

        // load array elements
        // each element is a 4-byte pointer to another index cell
        int j = 0x08;
        for (int i = 0; i < getNumEntries(); i++) {
            DataElement intElement = DataElement.makeDataElement(ElementType.E_INT, 0, j, "Offset #" + i, true);
            j = elements.addElement(intElement).read(offset, buffer);

            if (((NumberDataElement) intElement).getValueI() > 0) {
                elements.add(intElement);
            } else {
                break;
            }
        }
        return(true);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[offset=").append(NumFormat.numToHex(offset)).append(",size=").append(NumFormat.numToHex(size)).append("] ri Cell (");
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
     * Get offset at specified index
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
     * Set offset at specified index
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
    
    /**
     * Decrease number of array entries by 1
     */
    @Override
    public void decrementEntries() {
        numEntries--;
        ((NumberDataElement) elements.get(ix_numEntries)).setValueI(numEntries);
    }
    
    /**
     * Delete specified index element
     * 
     * @param index 
     */
    @Override
    protected void deleteCellIndexAt(int index)  {
        // move array elements up by 1, overwriting specified index
        for (int i = index; i < getNumEntries() - 1; i++) {
            setOffset(i, getOffset(i + 1));
        }
        
        // overwrite last array entry
        setOffset(getNumEntries() - 1, -1);        
    }       
    
}
