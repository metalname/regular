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
 * Defines a hash leaf (lh) cell
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#hash-leaf
 */
public class LhCell extends IndexCell {
    
    /**
     * Constructor
     * 
     * @param offset 
     */
    public LhCell(int offset) {
        super(offset);
    }
        
    @Override
    public RegistryCellType getCellType() {
        return(RegistryCellType.T_LH);
    }
    
    /**
     * Create attribute elements
     * 
     * @param buffer
     * @return 
     */
    @Override
    public boolean load(RegistryBuffer buffer) {
        // call superclass to load cell size and signature
        super.loadHeader(buffer);
        
        // build attributes elements
        elements = new DataElementArray();
        elements.addElement(DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x06, "Number of Entries", false)).read(offset, buffer);
        ix_numEntries = elements.lastElement();
        numEntries = ((NumberDataElement) elements.get(ix_numEntries)).getValueI();

        // load subkeys into an element array
        // each array entry has this format: offset (4 bytes) hash (4 bytes)
        int j = 0x08;
        for (int i = 0; i < getNumEntries(); i++) {
            DataElement intElement = DataElement.makeDataElement(ElementType.E_INT, 0, j, "Offset #" + i, true);
            j = intElement.read(offset, buffer);
            DataElement hashElement = DataElement.makeDataElement(ElementType.E_INT, 4, j, "Hash #" + i, false);
            j = hashElement.read(offset, buffer);
            
            if (((NumberDataElement) intElement).getValueI() > 0) {
                elements.add(intElement);            
                elements.add(hashElement);            
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
        int i = 1 + (index * 2) + ix_numEntries;
        if (i < elements.size()) {
            return (((IntDataElement) elements.get(i)).getValueI());
        } else {
            return (-1);
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
        int i = 1 + (index * 2) + ix_numEntries;
        if (i < elements.size()) {
            ((IntDataElement) elements.get(i)).setValueI(offset);
        }        
    }    
    
    /**
     * Get hash value for specified index
     * 
     * @param index
     * @return 
     */
    public int getHash(int index) {
        int i = 1 + (index * 2) + ix_numEntries;
        if (i < elements.size()) {
            return (((IntDataElement) elements.get(i + 1)).getValueI());
        } else {
            return (-1);
        }        
    }
    
    /**
     * Set hash value for specified index
     * 
     * @param index
     * @param hash 
     */
    public void setHash(int index, int hash) {
        int i = 1 + (index * 2) + ix_numEntries;
        if (i < elements.size()) {
            ((IntDataElement) elements.get(i + 1)).setValueI(hash);
        }                
    }    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[offset=").append(NumFormat.numToHex(offset)).append(",size=").append(NumFormat.numToHex(size)).append("] lh Cell (");
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
     * Decrease number of array entries by 1
     */
    @Override
    public void decrementEntries() {
        numEntries--;
        ((NumberDataElement) elements.get(ix_numEntries)).setValueI(numEntries);
    }
    
    @Override
    protected void deleteCellIndexAt(int index)  {
        // move all array entries up by 1, overwriting specified index
        for (int i = index; i < getNumEntries() - 1; i++) {
            setOffset(i, getOffset(i + 1));
            setHash(i, getHash(i + 1));
        }
        
        // overwrite offset for last entry
        setOffset(getNumEntries() - 1, -1);        
        // overwrite hash value for last entry
        setHash(getNumEntries() - 1, 0);

    }     
        
}
