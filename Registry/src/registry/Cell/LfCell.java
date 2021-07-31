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
 * Defines a fast leaf (lf) cell
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#fast-leaf
 */
public class LfCell extends IndexCell {
    
    /**
     * Constructor
     * 
     * @param offset 
     */
    public LfCell(int offset) {
        super(offset);
    }
        
    @Override
    public RegistryCellType getCellType() {
        return(RegistryCellType.T_LF);
    }
        
    /**
     * Load lf cell from registry buffer
     * 
     * @param buffer
     * @return 
     */
    @Override
    public boolean load(RegistryBuffer buffer) {
        // user superclass to load signature and length fields
        super.loadHeader(buffer);
        
        // create attribute list for this cell
        elements = new DataElementArray();
        
        // get number of index entries
        elements.addElement(DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x06, "Number of Entries", false)).read(offset, buffer);
        ix_numEntries = elements.lastElement();        
        numEntries = ((NumberDataElement) elements.get(ix_numEntries)).getValueI();

        // create an array of subkey entries
        // index array starts at offset 0x08 from header
        // each entry is: offset (4 bytes) hash (4 bytes)
        int j = 0x08;
        for (int i = 0; i < getNumEntries(); i++) {
            DataElement intElement = DataElement.makeDataElement(ElementType.E_INT, 0, j, "Offset #" + i, true);
            intElement.read(offset, buffer);
            j += 4;
            DataElement hashElement = DataElement.makeDataElement(ElementType.E_ASCIISL, 4, j, "Hash #" + i, false);            
            hashElement.read(offset, buffer);
            j += 4;
            
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
     * Set offset for specified index
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
     * Get the hash (name hint) at specified index
     * 
     * @param index
     * @return 
     */
    public String getHash(int index) {
        int i = 1 + (index * 2) + ix_numEntries;
        if (i < elements.size()) {
            return (elements.get(i + 1).toString());
        } else {
            return (null);
        }        
    }
    
    /**
     * Set the hash at specified index
     * 
     * @param index
     * @param hash 
     */
    public void setHash(int index, String hash) {
        int i = 1 + (index * 2) + ix_numEntries;
        if (i < elements.size()) {
            elements.get(i + 1).setData(hash);
        }                
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("[offset=").append(NumFormat.numToHex(offset)).append(",size=").append(NumFormat.numToHex(size)).append("] lf Cell (");
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
     * Decrease number of elements in subkey array
     * 
     */
    @Override
    public void decrementEntries() {
        numEntries--;
        ((NumberDataElement) elements.get(ix_numEntries)).setValueI(numEntries);
    }

    /**
     * Delete specified index entry
     * 
     * @param index 
     */
    @Override
    protected void deleteCellIndexAt(int index)  {
        // move array entries up by 1, overwriting specified index
        for (int i = index; i < getNumEntries() - 1; i++) {
            setOffset(i, getOffset(i + 1));
            setHash(i, getHash(i + 1));
        }
        // overwite offset for last entry
        setOffset(getNumEntries() - 1, -1);        
        String s = "\0\0\0\0";
        // overwrite hash value for last entry
        setHash(getNumEntries() - 1, s);
    }        
 
}
