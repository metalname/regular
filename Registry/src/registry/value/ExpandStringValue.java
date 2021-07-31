/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registry.value;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import registry.Cell.DataCell;
import registry.Cell.VkCell;
import registry.RegistryBuffer;
import registry.RegistryException;


/**
 *
 * @author 
 */
public class ExpandStringValue extends RegistryValue {

    protected String value;

    public ExpandStringValue(VkCell vkCell) {
        super(vkCell);
    }

    @Override
    public void loadData(DataCell dataCell) throws RegistryException {
        StringBuilder sb = new StringBuilder();
        char ch;

        int len = checkLength(dataCell.getData().length);
        for (int i = 0; i < len; i++) {
            ch = dataCell.getBuffer().getChar();
            if (ch != 0) {
                sb.append(ch);
            } else {
                break;
            }
        }
        value = sb.toString();
    }

    @Override
    protected void loadInline() {
        ByteBuffer bt = ByteBuffer.allocate(4);
        bt.order(ByteOrder.LITTLE_ENDIAN);
        bt.putInt(vkCell.getDataLength());
        value = new String(bt.array());
    }

    @Override
    public String toString() {
        return (value);
    }

    @Override
    public boolean matches(String text, boolean ignoreCase) {
        if (ignoreCase) {
            return (value.toLowerCase().contains(text));
        } else {
            return (value.contains(text));
        }
    }

    @Override
    public boolean setData(String s) {
        if (s.length() > value.length()) {
            value = s.substring(0, value.length());
        } else {
            value = s;
        }
        return (true);
    }
    
    @Override
    public void updateValue(RegistryBuffer buffer) {
        byte[] b = new byte[(value.length() + 1) * 2];
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            b[i * 2] = (byte) (c & 0x0ff);
            b[i * 2 + 1] = (byte) ((c & 0xff00) >> 8);
        }
        dataCell.setData(b);
        dataCell.update(buffer);
    }
    
    @Override
    public int getByteLength() {
        return(vkCell.getDataLength() * 2);
    }    
}
