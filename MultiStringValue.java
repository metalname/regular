/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registry.value;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import registry.Cell.DataCell;
import registry.Cell.VkCell;
import registry.RegistryBuffer;
import registry.RegistryException;

/**
 *
 * @author 
 */
public class MultiStringValue extends RegistryValue {

    public ArrayList<String> values = new ArrayList();

    public MultiStringValue(VkCell vkCell) {
        super(vkCell);
    }

    @Override
    public void loadData(DataCell dataCell) throws RegistryException {
        StringBuilder sb = new StringBuilder();
        char ch;

        int len = checkLength(dataCell.getData().length);
        dataCell.getBuffer().rewind();
        for (int i = 0; i < (len / 2); i++) {
            if (dataCell.getBuffer().hasRemaining()) {
                ch = dataCell.getBuffer().getChar();
            } else {
                System.out.println("Buffer exhausted when processing multi string cell @" + dataCell.getOffset() + 
                        " from vk cell @" + vkCell.getOffset());
                break;
            }
            if (ch == 0) {
                values.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        if(sb.length() > 0) {
            values.add(sb.toString());
        }
    }

    @Override
    protected void loadInline() {
        ByteBuffer bt = ByteBuffer.allocate(4);
        bt.order(ByteOrder.LITTLE_ENDIAN);
        bt.putInt(vkCell.getDataLength());
        values.add(new String(bt.array()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : values) {
            sb.append(s);
            sb.append(' ');
        }
        return (sb.toString());
    }
    
    @Override
    public boolean matches(String text, boolean ignoreCase) {
        for (String s: values) {
            if (ignoreCase) {
                if (s.toLowerCase().contains(text)) {
                    return(true);
                }
            } else {
                if (s.contains(text)) {
                    return(true);
                }                
            }
        }
        return(false);
    }    
    
    @Override
    public boolean setData(String data) {       
        values = new ArrayList<>(Arrays.asList(data.split(" ")));
        return(true);
    }
    
    @Override
    public void updateValue(RegistryBuffer buffer) {
        int l = 0;
        for (String s: values) {
            l += s.length() + 1;
        }
        final byte[] b = new byte[l * 2];
        int index = 0;
        for (String s: values) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                b[index++] = (byte) (c & 0x0ff);
                b[index++] = (byte) ((c & 0xff00) >> 8);
            }
            b[index++] = 0;
            b[index++] = 0;
        }
        dataCell.setData(b);
        dataCell.update(buffer);
    }
    
    @Override
    public int getByteLength() {
        return(vkCell.getDataLength() * 2);
    }    
}
