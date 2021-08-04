package registry.Cell;

import registry.RegistryBuffer;
import registry.element.DataElement;
import registry.element.DataElementArray;
import registry.element.ElementType;
import registry.element.IntDataElement;
import utils.NumFormat;

/**
 *
 * Defines a list of values
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#key-values-list
 */
public class ValueListCell extends IndexCell {

    /**
     * Constructor
     * 
     * @param offset
     * @param numValues 
     */
    public ValueListCell(int offset, int numValues) {
        super(offset);
        this.numEntries = numValues;
    }

    @Override
    public RegistryCellType getCellType() {
        return (RegistryCellType.T_VALUE);
    }

    /**
     * Define and load attribute elements
     * 
     * @param buffer
     * @return 
     */
    @Override
    public boolean load(RegistryBuffer buffer) {
        super.loadSize(buffer);
        elements = new DataElementArray();
        int ofs = 4;
        for (int i = 0; i < numEntries; i++) {
            int value = buffer.getInt();
            if (value > 0) {
                ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Offset to value #" + i, true)).read(offset, buffer);
            } else {
                break;
            }
        }
        return (true);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(NumFormat.numToHex(offset)).append("] Value List Cell (");
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
        if (index < elements.size()) {
            return (((IntDataElement) elements.get(index)).getValueI());
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
        if (index < elements.size()) {
            ((IntDataElement) elements.get(index)).setValueI(offset);
        }
    }

    /**
     * Reset offsets after an element has been deleted
     */
    protected void fixOffsets() {
        int ofs = 6 + elements.get(0).getSize();
        for (int index = 0; index < getNumEntries(); index++) {
            elements.get(index).setOffset(ofs);
            ofs += elements.get(index).getSize();
        }
    }

    /**
     * Delete array entry corresponding to specified offset
     * 
     * @param offset 
     */
    public void deleteEntryByOffset(int offset) {
        for (int i = 0; i < numEntries; i++) {
            if (getOffset(i) == offset) {
                deleteEntryByIndex(i);
            }
        }
    }

    /**
     * Delete specified array entry
     * 
     * @param index 
     */
    public void deleteEntryByIndex(int index) {
        // delete specified array index
        deleteCellIndexAt(index);

        // decrement number of entries
        decrementEntries();
    }
    
    @Override
    public void decrementEntries() {
        numEntries--;
    }
    
    @Override
    protected void deleteCellIndexAt(int index)  {
        // move all entries up by one
        for (int i = index; i < getNumEntries() - 1; i++) {
            setOffset(i, getOffset(i + 1));
        }
        // overwrite last entry
        setOffset(getNumEntries() - 1, -1);        
    }       
    
}
