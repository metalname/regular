package registry.Cell;

import java.util.ArrayList;
import registry.RegistryBuffer;
import registry.RegistryException;
import registry.element.DataElementCollection;
import registry.element.DataElementMap;
import registry.element.DataElement;
import registry.element.ElementType;
import registry.element.FiletimeDataElement;
import registry.element.IntDataElement;
import registry.element.NkFlagsDataElement;
import registry.element.NumberDataElement;
import registry.element.StringDataElement;
import utils.NumFormat;
import utils.WindowsDate;

/**
 *
 * Defines a key node (nk) cell
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#key-node
 */
public class NkCell extends RegistryCell {

    /*
     Len Start Name 
     4 0x00 Size 
     2 0x04 Signature 
     2 0x06 Flags 
     8 0x08 LastWrite Timestamp 
     4 0x10 Spare (as of Windows 8 is now access bits)
     4 0x14 Offset to Parent Cell 
     4 0x18 Subkey Count (Stable) 
     4 0x1c Subkey Count (volatile) 
     4 0x20 Offset to Subkey List (stable) 
     4 0x24 Offset to Subkey List (Volatile) 
     4 0x28 Value Count 
     4 0x2c Offset to Value List 
     4 0x30 Offset to SK 
     4 0x34 Offset to Class Name
     2 0x38 Max Name Length 
     1 0x3a User Flags 
     1 0x3b Debug 
     4 0x3c Class Name Length 
     4 0x40 Max Value Name Length 
     4 0x44 Max Value Data Length 
     4 0x48 Work Var 
     2 0x4c Name Length 
     2 0x4e Class Length 
     0x50 Name   
     */
    
    // defines a registry key entry
    protected enum Element {

        E_FLAGS, E_TIMESTAMP, E_PARENT_OFFSET, E_SUBKEY_COUNT, E_SUBKEY_OFFSET,
        E_VALUE_COUNT, E_VALUE_OFFSET, E_SK_OFFSET, E_CLASS_OFFSET, E_NAME_LENGTH,
        E_KEY_NAME;
    }

    protected ValueListCell valueListCell;  // this cells corresponding velue key (vk) cell
    protected IndexCell indexCell;          // index cell for subkeys of this cell
    protected DataElementMap elements;      // attribute elements

    /**
     * Constructor
     * 
     * @param offset 
     */
    public NkCell(int offset) {
        super(offset);
    }

    /**
     * Getter for attribute elements
     * 
     * @return 
     */
    @Override
    public DataElementCollection elements() {
        return (elements);
    }

    /**
     * Setter for attribute elements
     * 
     * @param collection 
     */
    @Override
    public void elements(DataElementCollection collection) {
        this.elements = (DataElementMap) collection;
    }

    /**
     * Create and load attribute elements
     * 
     * @param buffer
     * @return 
     */
    @Override
    public boolean load(RegistryBuffer buffer) {
        super.loadHeader(buffer);
        elements = new DataElementMap(Element.values());
        elements.setElement(Element.E_FLAGS, DataElement.makeDataElement(ElementType.E_NKFLAGS, 0, 0x06, "Flags", false)).read(offset, buffer);
        elements.setElement(Element.E_TIMESTAMP, DataElement.makeDataElement(ElementType.E_FILETIME, 0, 0x08, "Last Write Timestamp", false)).read(offset, buffer);
        elements.setElement(Element.E_PARENT_OFFSET, DataElement.makeDataElement(ElementType.E_INT, 0, 0x14, "Offset to Parent Key", true)).read(offset, buffer);
        elements.setElement(Element.E_SUBKEY_COUNT, DataElement.makeDataElement(ElementType.E_INT, 0, 0x18, "Subkey Count", false)).read(offset, buffer);
        elements.setElement(Element.E_SUBKEY_OFFSET, DataElement.makeDataElement(ElementType.E_INT, 0, 0x20, "Offset to Subkey List", true)).read(offset, buffer);
        elements.setElement(Element.E_VALUE_COUNT, DataElement.makeDataElement(ElementType.E_INT, 0, 0x28, "Number of Values", false)).read(offset, buffer);
        elements.setElement(Element.E_VALUE_OFFSET, DataElement.makeDataElement(ElementType.E_INT, 0, 0x2c, "Offset to Value List", true)).read(offset, buffer);
        elements.setElement(Element.E_SK_OFFSET, DataElement.makeDataElement(ElementType.E_INT, 0, 0x30, "Offset to Security Key", true)).read(offset, buffer);
        elements.setElement(Element.E_CLASS_OFFSET, DataElement.makeDataElement(ElementType.E_INT, 0, 0x34, "Offset to Class Descriptor", true)).read(offset, buffer);
        elements.setElement(Element.E_NAME_LENGTH, DataElement.makeDataElement(ElementType.E_SHORT, 0, 0x4c, "Key Name Length", false)).read(offset, buffer);
        if (((NkFlagsDataElement) elements.get(Element.E_FLAGS)).isCompressed()) {
            elements.setElement(Element.E_KEY_NAME, DataElement.makeDataElement(ElementType.E_ASCIISZ, elementValue(Element.E_NAME_LENGTH), 0x50, "Key Name", false)).read(offset, buffer);
        } else {
            elements.setElement(Element.E_KEY_NAME, DataElement.makeDataElement(ElementType.E_UNICODESZ, elementValue(Element.E_NAME_LENGTH), 0x50, "Key Name", false)).read(offset, buffer);
        }
        return (true);
    }

    @Override
    public RegistryCellType getCellType() {
        return (RegistryCellType.T_NK);
    }

    /**
     * Get value of specified attribute element as int
     * 
     * @param e
     * @return 
     */
    protected int elementValue(Element e) {
        return (((NumberDataElement) elements.get(e)).getValueI());
    }

    /**
     * Update specified attribute element from int
     * 
     * @param e
     * @param value 
     */
    protected void updateElementValue(Enum e, int value) {
        ((NumberDataElement) elements.get(e)).setValueI(value);
    }

    @Override
    public String toString() {
        return ("[offset=" + NumFormat.numToHex(offset) + ",size=" + NumFormat.numToHex(size) + "] Node Key (nk) Cell : " + getKeyName());
    }

    /**
     * Get key name
     * 
     * @return 
     */
    public String getKeyName() {
        return (elements.get(Element.E_KEY_NAME).toString());
    }

    /** 
     * Get key name length
     * 
     * @return 
     */
    public int getNameLength() {
        return (elementValue(Element.E_NAME_LENGTH));
    }

    /**
     * Set key name length
     * 
     * @param length 
     */
    public void setNameLength(int length) {
        ((IntDataElement) elements.get(Element.E_NAME_LENGTH)).setValueI(length);
    }

    /**
     * Set key name
     * 
     * @param name 
     */
    public void setKeyName(String name) {
        elements.get(Element.E_KEY_NAME).setData(name);
        ((NumberDataElement) elements.get(Element.E_NAME_LENGTH)).setValue(name.length());
    }

    /**
     * Get count of child keys
     * 
     * @return 
     */
    public int getSubkeyCount() {
        return (elementValue(Element.E_SUBKEY_COUNT));
    }

    /**
     * Set count of child keys
     * 
     * @param count 
     */
    protected void setSubkeyCount(int count) {
        updateElementValue(Element.E_SUBKEY_COUNT, count);
    }

    /**
     * Get offset of subkey index cell
     * @return 
     */
    public int getSubkeyOffset() {
        return (elementValue(Element.E_SUBKEY_OFFSET));
    }

    /**
     * Set offset of subkey index cell
     * 
     * @param offset 
     */
    protected void setSubkeyOffset(int offset) {
        updateElementValue(Element.E_SUBKEY_OFFSET, offset);
    }

    /**
     * Get count of key values
     * 
     * @return 
     */
    public int getValueCount() {
        return (elementValue(Element.E_VALUE_COUNT));
    }

    /**
     * Set count of key values
     * 
     * @return 
     */
    public int getValueOffset() {
        return (elementValue(Element.E_VALUE_OFFSET));
    }

    /**
     * Get offset of class key
     * 
     * @return 
     */
    public int getClassOffset() {
        return (elementValue(Element.E_CLASS_OFFSET));
    }

    /**
     * Get offset of security key
     * 
     * @return 
     */
    public int getSKOffset() {
        return (elementValue(Element.E_SK_OFFSET));
    }

    /**
     * Get offset of parent key
     * 
     * @return 
     */
    public int getParentOffset() {
        return (elementValue(Element.E_PARENT_OFFSET));
    }

    /** 
     * Get key timestamp as string
     * @return 
     */
    public String getTimestampS() {
        return (((FiletimeDataElement) elements.get(Element.E_TIMESTAMP)).toString());
    }

    /**
     * Get key timestamp as epoch
     * 
     * @return 
     */
    public long getTimestamp() {
        return (((FiletimeDataElement) elements.get(Element.E_TIMESTAMP)).getTimestamp());
    }

    /**
     * Set key timestamp from WindowsDate object
     * 
     * @param dt 
     */
    public void setTimestamp(WindowsDate dt) {
        ((FiletimeDataElement) elements.get(Element.E_TIMESTAMP)).setDate(dt);
    }

    /**
     * Set value of specified attribute element from int
     * 
     * @param index
     * @param value 
     */
    protected void setElementValue(int index, int value) {
        ((NumberDataElement) elements.get(index)).setValueI(value);
    }

    /**
     * Get child keys index cell
     * Lazy loading - will create index cell on first call
     * 
     * @param buffer
     * @return
     * @throws RegistryException 
     */
    private IndexCell indexCell(RegistryBuffer buffer) throws RegistryException {
        // create index cell if needed
        if (indexCell == null) {
            if (getSubkeyOffset() > 0) {
                RegistryCell iCell = buffer.loadCell(getSubkeyOffset());
                if (iCell instanceof IndexCell) {
                    indexCell = (IndexCell) iCell;
                }
            } else {
                indexCell = null;
            }
        }
        return (indexCell);
    }

    /**
     * Get child keys as array of key nodes
     * 
     * @param buffer
     * @return
     * @throws RegistryException 
     */
    public NkCell[] getChildren(RegistryBuffer buffer) throws RegistryException {
        if (indexCell(buffer) == null) {
            return (new NkCell[0]);
        }
        switch (indexCell.getCellType()) {
            case T_LH:
            case T_LI:
            case T_LF:
                NkCell[] skeys = new NkCell[getSubkeyCount()];
                processSimpleIndex(skeys, indexCell, buffer);
                return (skeys);
            case T_RI:
                // use an arraylist instead of array
                // this is because the RI entry count rarely matches the NK cell subkey count
                ArrayList<NkCell> ckeys = new ArrayList<>(getSubkeyCount());
                processComplexIndex(ckeys, indexCell, buffer);
                NkCell[] keys = new NkCell[ckeys.size()];
                for (int i = 0; i < ckeys.size(); i++) {
                    keys[i] = ckeys.get(i);
                }
                if (getSubkeyCount() != keys.length) {
                    System.out.println("Warning: NK cell count is "
                            + getSubkeyCount() + " but RI cell has " + keys.length + " entries");
                }
                return (keys);
            default:
                throw new RegistryException("Unexpected cell type " + indexCell.getCellType() + " when processing NK cell @" + NumFormat.numToHex(getOffset()));
        }
    }

    /**
     * Loads subkeys for a simple index cell (li, lf, lh)
     * @param keys
     * @param iCell
     * @param buffer
     * @throws RegistryException 
     */
    private void processSimpleIndex(NkCell[] keys, IndexCell iCell, RegistryBuffer buffer) throws RegistryException {
        for (int i = 0; i < keys.length; i++) {
            if (iCell.getOffset(i) > 0) {
                RegistryCell cell = buffer.loadCell(iCell.getOffset(i));
                if (cell.getCellType() == RegistryCellType.T_NK) {
                    keys[i] = (NkCell) cell;
                } else {
                    throw new RegistryException("Unexpected cell type " + cell.getCellType() + " when processing index " + i + " of cell @" + NumFormat.numToHex(indexCell.getOffset()));
                }
            } else {
                keys[i] = null;
            }
        }
    }

    /**
     * Load subkeys for a complex index cell (ri)
     * 
     * @param keys
     * @param indexCell
     * @param buffer
     * @throws RegistryException 
     */
    private void processComplexIndex(ArrayList<NkCell> keys, IndexCell indexCell, RegistryBuffer buffer) throws RegistryException {
        int m = 0;
        for (int i = 0; i < indexCell.getNumEntries(); i++) {
            if (indexCell.getOffset(i) > 0) {
                RegistryCell cell = buffer.loadCell(indexCell.getOffset(i));
                switch (cell.getCellType()) {
                    case T_LF:
                    case T_LI:
                    case T_LH:
                        indexCell = (IndexCell) cell;
                        NkCell[] subKeys = new NkCell[indexCell.getNumEntries()];
                        processSimpleIndex(subKeys, indexCell, buffer);
                        for (int j = 0; j < subKeys.length; j++) {
                            //keys[j + m] = subKeys[j];
                            if ((subKeys[j] != null) && (!subKeys[j].isDeleted())) {
                                keys.add(subKeys[j]);
                            }
                        }
                        m += subKeys.length;
                        break;
                    case T_NK:
                        keys.add((NkCell) buffer.loadCell(indexCell.getOffset(i)));
                        break;
                    default:
                        throw new RegistryException("Unexpected cell type " + cell.getCellType() + " when processing index " + i + " of RI cell @" + NumFormat.numToHex(indexCell.getOffset()));
                }
            }
        }
    }

    /**
     * Get parent nk cell
     * 
     * @param buffer
     * @return
     * @throws RegistryException 
     */
    public NkCell getParent(RegistryBuffer buffer) throws RegistryException {
        RegistryCell cell = buffer.loadCell(getParentOffset());
        if (cell.getCellType() == RegistryCellType.T_NK) {
            return ((NkCell) cell);
        } else {
            throw new RegistryException("Unexpected cell type " + cell.getCellType() + " when getting parent key for NK cell @" + NumFormat.numToHex(offset));
        }
    }

    /**
     * Get key values as array of vk cells
     * 
     * @param buffer
     * @return
     * @throws RegistryException 
     */
    public VkCell[] getValues(RegistryBuffer buffer) throws RegistryException {
        VkCell[] values = new VkCell[getValueCount()];
        if (valueListCell(buffer) == null) {
            return (values);
        }
        for (int i = 0; i < values.length; i++) {
            int index = valueListCell.getOffset(i);
            RegistryCell vkCell = buffer.loadCell(index);
            if (vkCell.getCellType() == RegistryCellType.T_VK) {
                values[i] = (VkCell) vkCell;
            } else {
                throw new RegistryException("Unexpected cell type " + vkCell.getCellType() + " when fetching value " + i + " for NK cell @" + NumFormat.numToHex(offset));
            }
        }
        return (values);
    }

    /**
     * Create value list cell for this key node
     * 
     * @param buffer
     * @return 
     */
    public ValueListCell valueListCell(RegistryBuffer buffer) {
        if (getValueOffset() > 0) {
            if (valueListCell == null) {
                valueListCell = RegistryCell.makeValueListCell(getValueOffset(), getValueCount(), buffer);
            }
            return (valueListCell);
        } else {
            return (null);
        }
    }

    /**
     * Return array of subkey offsets
     * 
     * @return 
     */
    @Override
    public int[] getChildIndexes() {
        return (new int[]{getSubkeyOffset(), getValueOffset(), getClassOffset(), getSKOffset()});
    }

    /**
     * Get child cells as array of RegistryCell
     * 
     * @param buffer
     * @return 
     */
    @Override
    public RegistryCell[] getChildCells(RegistryBuffer buffer) {
        return (new RegistryCell[]{RegistryCell.makeRegistryCell(getSubkeyOffset(), buffer),
            RegistryCell.makeValueListCell(getValueOffset(), getValueCount(), buffer),
            RegistryCell.makeClassCell(getClassOffset(), buffer),
            RegistryCell.makeRegistryCell(getSKOffset(), buffer)
        });
    }

    /**
     * Chains deletes to child keys
     * 
     * @param index
     * @param buffer
     * @throws RegistryException 
     */
    public void notifyChildDeleted(int index, RegistryBuffer buffer) throws RegistryException {
        // get index cell
        if (indexCell(buffer) != null) {
            ((IndexCell) indexCell).deleteCellAtOffset(index, buffer);
            indexCell.update(buffer);
            //update subkey count
            int count = getSubkeyCount() - 1;
            if (count == 0) {
                //all subkeys deleted
                setSubkeyOffset(-1);
            } else {
                setSubkeyCount(count);
            }
            update(buffer);
        }
    }

    /**
     * Chains deletes to cell values
     * 
     * @param index
     * @param buffer 
     */
    public void notifyValueDeleted(int index, RegistryBuffer buffer) {
        // is this the last value?
        if (getValueCount() == 1) {
            // delete the entire value list cell
            valueListCell.delete(buffer);
            // update the value offset
            ((IntDataElement) elements.get(Element.E_VALUE_OFFSET)).setValueI(-1);
            // zero value count
            ((IntDataElement) elements.get(Element.E_VALUE_COUNT)).setValueI(0);
        } else if (getValueCount() > 1) {
            // delete this entry from the value list cell
            valueListCell.deleteEntryByOffset(index);
            // decrement the value count
            ((IntDataElement) elements.get(Element.E_VALUE_COUNT)).setValueI(elementValue(Element.E_VALUE_COUNT) - 1);
        }
        valueListCell.update(buffer);
    }

    /**
     * Search key name for specified text
     * 
     * @param text
     * @param ignoreCase
     * @return 
     */
    public boolean matches(String text, boolean ignoreCase) {
        if (ignoreCase) {
            return (((StringDataElement) elements.get(Element.E_KEY_NAME)).getValueS().toLowerCase().contains(text));
        } else {
            return (((StringDataElement) elements.get(Element.E_KEY_NAME)).getValueS().contains(text));
        }
    }

    /**
     * Return amount of unused space in the cell
     * @return 
     */
    public int getSlack() {        
        return (Math.abs(size) - 0x50 - getNameLength() - 1);
    }
    
    /**
     * Getter for indexCell
     * @return 
     */
    public IndexCell getIndexCell() {
        return(indexCell);
    }
}
