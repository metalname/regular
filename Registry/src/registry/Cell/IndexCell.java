package registry.Cell;

import registry.RegistryBuffer;
import registry.element.DataElementArray;
import registry.element.DataElementCollection;

/**
 *
 * Abstract class for various index cell types
 */
public abstract class IndexCell extends RegistryCell {
    
    protected int numEntries;               // number of array entries
    protected int ix_numEntries = -1;       // index of element containing number of array entries
    protected DataElementArray elements;    // attribute elements for this cell
            
    /**
     * Constructor 
     * 
     * @param offset 
     */
    public IndexCell(int offset) {
        super(offset);
    }
    
    /**
     * Get number of entries in this index cell
     * 
     * @return 
     */
    public int getNumEntries() {
        return(numEntries);
    }
    
    /**
     * Get cells attribute elements
     * 
     * @return 
     */
    @Override
    public DataElementArray elements() {
        return(elements);
    }
    
    /**
     * Set cells attribute elements
     * 
     * @param collection 
     */
    @Override
    public void elements(DataElementCollection collection) {
        this.elements = (DataElementArray) collection;
    }
        
    
    /**
     * Get offset for specified index
     * 
     * @param index
     * @return 
     */
    public abstract int getOffset(int index);
    
    /**
     * Set offset for specified index
     * @param index
     * @param offset 
     */
    public abstract void setOffset(int index, int offset);
    
    /**
     * Decrease number of key entries
     * This method will be called after an index element is deleted
     * 
     */
    protected abstract void decrementEntries();
    
    /**
     * Delete cell pointer at specified index
     * 
     * @param index 
     */
    protected abstract void deleteCellIndexAt(int index);
    
    /**
     * Get child indexes
     * 
     * @return 
     */
    @Override
    public int[] getChildIndexes() {
        int[] children = new int[getNumEntries()];
        for (int i = 0; i < getNumEntries(); i++) {
            children[i] = getOffset(i);
        }
        return (children);
    }    
    
    /**
     * Get child cells
     * 
     * @param buffer
     * @return 
     */
    @Override
    public RegistryCell[] getChildCells(RegistryBuffer buffer) {
        RegistryCell[] cells = new RegistryCell[getNumEntries()];
        int i = 0;
        for(int index: getChildIndexes()) {
            cells[i++] = RegistryCell.makeRegistryCell(index, buffer);
        }
        return(cells);
    }   
    
    /**
     * Delete cell corresponding to specified offset
     * 
     * @param offset
     * @param buffer 
     */
    public void deleteCellAtOffset(int offset, RegistryBuffer buffer) {
        int i = findCellIndex(offset);
        if (i >= 0) {
            deleteCellIndexAt(i);
            decrementEntries();
        }
    }
    
    /**
     * Find index of cell corresponding to specified offset
     * 
     * @param offset
     * @return 
     */
    protected int findCellIndex(int offset) {
        for (int i = 0; i < getNumEntries(); i++) {
            if (offset == getOffset(i)) {
                return(i);
            }
        }
        return(-1);
    }
        
    /**
     * Replace offset
     * This will be called if a child cell has to be moved to a different location
     * 
     * @param oldOffset
     * @param newOffset 
     */
    public void replaceOffset(int oldOffset, int newOffset) {
        for (int i = 0; i < getNumEntries(); i++) {
            if (getOffset(i) == oldOffset) {
                setOffset(i, newOffset);
            }
        }
    }    
       
}
