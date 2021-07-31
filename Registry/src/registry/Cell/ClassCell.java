package registry.Cell;

import registry.RegistryBuffer;
import registry.element.*;
import utils.NumFormat;

/**
 *
 * Defines a class name cell
 *
 * This cell will not be read directly from disk, but will be created from a
 * data cell
 */
public class ClassCell extends RegistryCell {

    protected static final int ix_ClassName = 0;
    protected DataElementArray elements;

    public ClassCell(int offset) {
        super(offset);
    }

    @Override
    public RegistryCellType getCellType() {
        return (RegistryCellType.T_CLASS);
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
        elements.addElement(DataElement.makeDataElement(ElementType.E_UNICODESZ, length() / 2, 0x04, "Class Name", false));
        return(true);
    }

    @Override
    public String toString() {
        return ("[offset=" + NumFormat.numToHex(offset) + ",size=" + NumFormat.numToHex(size) + "] Class Name Cell");
    }

    public boolean matches(String text, boolean ignoreCase) {
        if (ignoreCase) {
            return (((StringDataElement) elements.get(ix_ClassName)).getValueS().toLowerCase().contains(text));
        } else {
            return (((StringDataElement) elements.get(ix_ClassName)).getValueS().contains(text));
        }
    }
    
    @Override
    public int[] getChildIndexes() {
        return(new int[0]);
    }
    
    /**
     *
     * @return
     */
    @Override
    public RegistryCell[] getChildCells(RegistryBuffer buffer) {
        return(new RegistryCell[0]);
    }
}
