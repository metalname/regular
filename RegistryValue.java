/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registry.value;

import registry.Cell.DataCell;
import registry.Cell.VkCell;
import registry.RegistryBuffer;
import registry.RegistryException;


/**
 *
 * @author 
 */
public abstract class RegistryValue {

    protected final RegDataType type;
    public final VkCell vkCell;
    public DataCell dataCell;
    public int length;

    public RegistryValue(VkCell vkCell) {
        this.vkCell = vkCell;
        int dt = vkCell.getDataType();
        if (dt < RegDataType.values().length) {
            this.type = RegDataType.values()[vkCell.getDataType()];
        } else {
            this.type = RegDataType.REG_BINARY;
        }
    }

    public void load(final RegistryBuffer buffer) throws RegistryException {
        if (vkCell.isInline()) {
            loadInline();
        } else {
            dataCell = buffer.loadDataCell(vkCell.getOffsetData());
            length = dataCell.length();
            loadData(dataCell);            
        }
    }

    public int checkLength(final int len) {
        if (vkCell.getDataLength() > len) {
            System.out.println("Warning - vk cell @" + vkCell.getOffset() + " length " + vkCell.getDataLength()
                    + " exceeds length of associated data cell");
            return (len);
        } else {
            return (vkCell.getDataLength());
        }
    }

    public RegDataType getType() {
        return (type);
    }

    public static RegistryValue makeValue(final VkCell vkCell, final RegistryBuffer buffer) throws RegistryException {
        int dt = vkCell.getDataType();
        if (dt >= RegDataType.values().length) {
            dt = 3;
        }
        RegDataType type = RegDataType.values()[dt];
        
        RegistryValue value;
        switch (type) {
            case REG_SZ:
                value = new StringValue(vkCell);
                break;
            case REG_DWORD:
                value = new DwordValue(vkCell);
                break;
            case REG_QWORD:
                value = new QwordValue(vkCell);
                break;
            case REG_UNKNOWN:
            case REG_BINARY:
                value = new BinaryValue(vkCell);
                break;
            case REG_MULTI_SZ:
                value = new MultiStringValue(vkCell);
                break;
            case REG_EXPAND_SZ:
                value = new ExpandStringValue(vkCell);
                break;
            case REG_RESOURCE_REQUIREMENTS_LIST:
                value = new BinaryValue(vkCell);
                break;
            case REG_RESOURCE_LIST:
                value = new BinaryValue(vkCell);
                break;
            default:
                System.out.println("Unsupported value type " + type + " (" + vkCell.getDataType() + ")");
                return (null);
        }
        value.load(buffer);
        return (value);
    }
    
    public DataCell getDataCell() {
        return(dataCell);
    }
    
    public VkCell getVkCell() {
        return(vkCell);
    }
    
    // set data from supplied string value
    public abstract boolean setData(String s);
    
    @Override
    public abstract String toString();
    
    // load data from data cell
    protected abstract void loadData(final DataCell dataCell) throws RegistryException;

    // if isInline flag is set, data will be in vkCell.dataLength
    protected abstract void loadInline();
    
    // compare value against string
    public abstract boolean matches(String text, boolean ignoreCase);
    
    // write this value back to the buffer
    public abstract void updateValue(RegistryBuffer buffer);
    
    // caclulate the expected length of this value based on the VK cell
    // used as a sanity check for the data cell length
    // this will help prevent registry damage if the data cell is corrupted
    protected abstract int getByteLength();
            
}
