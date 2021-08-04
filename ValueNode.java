package registry.Nodes;

import registry.Cell.DataCell;
import registry.Cell.RegistryCell;
import registry.Cell.RegistryCellType;
import registry.Cell.VkCell;
import registry.RegistryException;
import registry.value.BinaryValue;
import registry.value.RegDataType;
import registry.value.RegistryValue;

/**
 *
 * Wraps a value cell
 */
public class ValueNode {

    private VkCell vkCell;              // value cell
    private final KeyNode keyNode;      // NK node that contains this value
    private final RegistryValue value;  // actual value

    /**
     * Constructor
     * @param vkCell
     * @param keyNode
     * @throws RegistryException 
     */
    public ValueNode(VkCell vkCell, KeyNode keyNode) throws RegistryException {
        this.vkCell = vkCell;
        this.keyNode = keyNode;
        value = RegistryValue.makeValue(vkCell, keyNode.getRegistryBuffer());
    }

    /**
     * Get name of value cell
     * @return 
     */
    public String name() {
        return (vkCell.getValueName());
    }

    /**
     * Set value name
     * @param newName
     * @throws RegistryException 
     */
    public void name(String newName) throws RegistryException {
        if (newName.length() > name().length()) {
            // check if there is enough space in the cell for the new name
            int needBytes = newName.length() - name().length();
            if (vkCell.getSlack() < needBytes) {
                // not enough space for the new name
                // look for an empty cell of the correct size
                // 0x18 is the offset of the key name in the vk cell
                int offset = keyNode.hive().findEmptyCell(0x18 + newName.length() + 1);
                if (offset >= 0) {
                    VkCell newCell = (VkCell) RegistryCell.newRegistryCell(offset, RegistryCellType.T_VK, keyNode.getRegistryBuffer());
                    // copy elements from old cell
                    newCell.elements(vkCell.elements());
                    // wipe the old cell
                    vkCell.wipe(keyNode.getRegistryBuffer());
                    // update nkCell with new value location
                    keyNode.updateValueOffset(vkCell.getOffset(), offset, keyNode.getRegistryBuffer());
                    // repoint
                    vkCell = newCell;
                } else {
                    throw new RegistryException("Could not allocate space for new cell");
                }
            }
        }
        vkCell.setValueName(newName);    
        vkCell.update(keyNode.getRegistryBuffer());
        keyNode.hive().hiveChanged(true);
    }
    
    /**
     * Getter for value
     * @return 
     */
    public RegistryValue value() {
        return (value);
    }

    /**
     * Get value type
     * @return 
     */
    public RegDataType type() {
        if (vkCell.getDataType() < RegDataType.values().length) {
            return (RegDataType.values()[vkCell.getDataType()]);
        } else {
            return (RegDataType.REG_UNKNOWN);
        }
    }

    /**
     * Getter for keyNode
     * @return 
     */
    public KeyNode getKeyNode() {
        return (keyNode);
    }

    /**
     * Change value using string
     * @param data
     * @return 
     */
    public boolean setData(String data) {
        if (value.setData(data)) {
            value.updateValue(keyNode.hive().getRegistryBuffer());
            keyNode.hive().hiveChanged(true);
            return (true);
        }
        return (false);
    }

    /**
     * Change value using byte array
     * @param data
     * @return 
     */
    public boolean setData(byte[] data) {
        if (value instanceof BinaryValue) {
            ((BinaryValue) value).setValue(data);
            value.updateValue(keyNode.hive().getRegistryBuffer());
            keyNode.hive().hiveChanged(true);
            return (true);
        }
        return (false);
    }
    
    /**
     * Return value as byte array
     * @return 
     */
    public byte[] getData() {
        if (value instanceof BinaryValue) {
            return (((BinaryValue) value).getValue());
        } else {
            return(null);
        }
    }

    @Override
    public String toString() {
        return (value().toString());
    }
    
    /**
     * Getter for vkCell
     * @return 
     */
    public VkCell vkCell() {
        return(vkCell);
    }
    
    /**
     * Delete value
     * @param wipe
     * @throws RegistryException 
     */
    public void delete(boolean wipe) throws RegistryException {
        // delete data cell if not inline
        if (!vkCell.isInline()) {
            DataCell dataCell = value.getDataCell();
            if (dataCell != null) {
                keyNode.deleteSpecifiedCell(dataCell, wipe);
            }
        }
        // save the offset
        int offset = vkCell.getOffset();
        // delete vk cell
        keyNode.deleteSpecifiedCell(vkCell, wipe);
        // notify the nk cell that a value has been deleted
        keyNode.nkCell().notifyValueDeleted(offset, keyNode.getRegistryBuffer());
        keyNode.nkCell().update(keyNode.getRegistryBuffer());
        keyNode.hive().hiveChanged(true);
        keyNode.reloadValues();
    }
    
    public String path() {
        return(keyNode.path() + "/" + name());
    }
    
}
