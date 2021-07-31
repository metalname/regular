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
public class QwordValue extends RegistryValue {

    public long value;

    public QwordValue(VkCell vkCell) {
        super(vkCell);
    }

    @Override
    public void loadData(DataCell dataCell) throws RegistryException {
        value = dataCell.getBuffer().getLong();
    }

    @Override
    protected void loadInline() {
        value = vkCell.getOffsetData();
    }

    @Override
    public String toString() {
        return (Long.toHexString(value));
    }

    public long getValueL() {
        return (value);
    }

    public void setValueL(long value) {
        this.value = value;
    }

    @Override
    public void updateValue(RegistryBuffer buffer) {
        dataCell.getBuffer().rewind();
        dataCell.getBuffer().putLong(value);
        dataCell.update(buffer);
    }
    
    @Override
    public boolean setData(String data) {
        try {
            long l = Integer.parseInt(data);
            value = l;
            return(true);
        } catch (NumberFormatException e) {
            return(false);
        }        
    }

    @Override
    public boolean matches(String s, boolean ignoreCase) {
        return(false);
    }    
    
    @Override
    public int getByteLength() {
        return(vkCell.getDataLength());
    }     
}
