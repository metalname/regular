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
public class NoneValue extends RegistryValue {

    byte[] b;

    public NoneValue(VkCell vkCell) {
        super(vkCell);
    }

    @Override
    public void loadData(DataCell dataCell) throws RegistryException {
        b = dataCell.getData();        
    }

    @Override
    protected void loadInline() {
        b = new byte[4];
        int i = vkCell.getOffsetData();
        for (int j = 3; j >= 0; j--) {
            b[j] = (byte) (i & 0xff);
            i = i >> 8;
        }
    }

    public String toHex(Byte bt) {
        String hex = "0123456789abcdef";
        char lb = hex.charAt(bt & 0x0f);
        char hb = hex.charAt((bt & 0xf0) / 0x10);
        return ("" + hb + lb);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vkCell.getDataLength(); i++) {
            sb.append(toHex(b[i]));
            sb.append(' ');
        }
        return (sb.toString());
    }

    @Override
    public boolean setData(String s) {
        return(true);
    }
    
    @Override
    public boolean matches(String s, boolean ignoreCase) {
        return(false);
    }
    
    @Override
    public void updateValue(RegistryBuffer buffer) {
        
    }
    
    @Override
    public int getByteLength() {
        return(vkCell.getDataLength());
    }     
}
