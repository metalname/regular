/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package registry.value;

import java.io.IOException;
import registry.Cell.DataCell;
import registry.Cell.VkCell;
import registry.RegistryBuffer;
import registry.RegistryException;


/**
 *
 * @author 
 */
public class DwordValue extends RegistryValue {
    
    public int value;
    
    public DwordValue (VkCell vkCell) {
        super(vkCell);
    }
    
    @Override
    public void loadData(DataCell dataCell) throws RegistryException {
        value = dataCell.getBuffer().getInt();
    }
    
    @Override
    protected void loadInline() {
        value = vkCell.getOffsetData();
    }
    
    @Override
    public String toString() {
        return(Integer.toHexString(value));
    }
    
    public int getValueI() {
        return(value);
    }
    
    public void setValueI(int value) {
        this.value = value;
    }
    
    @Override
    public void updateValue(RegistryBuffer buffer) {
        if (vkCell.isInline()) {
            vkCell.setOffsetData(value);
            vkCell.update(buffer);
        } else {
            dataCell.getBuffer().rewind();
            dataCell.getBuffer().putInt(value);
            dataCell.update(buffer);
        }
    }
    
    @Override
    public boolean setData(String data) {        
        try {
            int i = Integer.parseInt(data);
            value = i;
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
