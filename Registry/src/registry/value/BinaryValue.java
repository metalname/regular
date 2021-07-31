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
import registry.element.FormatElement;

/**
 *
 * @author 
 */
public class BinaryValue extends RegistryValue {

    byte[] b;
    private static final String hexString = "0123456789ABCDEF";

    public BinaryValue(VkCell vkCell) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vkCell.getDataLength(); i++) {
            sb.append(FormatElement.toHexString(b[i]));
            sb.append(' ');
        }
        return (sb.toString());
    }

    public byte[] getValue() {
        return (b);
    }

    public void setValue(byte[] data) {
        System.arraycopy(data, 0, b, 0, data.length);
    }

    @Override
    public void updateValue(RegistryBuffer buffer) {
        if (vkCell.isInline()) {
            // convert binary data back to int
            int value = 0;
            for (int i = 0; i < 4; i++) {
                if (i >= b.length) {
                    break;
                }
                value = (value << 8) + b[i];
            }
            vkCell.setOffsetData(value);
            vkCell.update(buffer);
        } else {
            dataCell.update(buffer);
            // don't need to move data if not inline, since b is a reference to the element buffer            
        }
    }

    @Override
    public boolean setData(String data) {
    // sets the data from the string value
        // string is assumed to be a series of hex byte digits, e.g. "01 fa 7b"
        // method will return false if data is not in this format
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        final byte[] newValue = new byte[length];
        for (int i = 0; i < data.length(); i++) {
            //get next byte
            char c = Character.toUpperCase(data.charAt(i));
            if ((c >= '0') && (c <= 'F')) {
                // check current length of sb
                if (sb.length() <= 2) {
                    sb.append(c);
                } else {
                    // error - byte substring is too long
                    return (false);
                }
            } else if (c == ' ') {
                // space - end of current byte string
                // break if past current data length
                if (i < length) {
                    newValue[index++] = cvtToByte(sb);
                    sb.setLength(0);
                } else {
                    break;
                }
            } else {
                // error - unknown character
                return (false);
            }
        }
        // check if still data in buffer
        if ((sb.length() > 0) && (index < length)) {
            // convert last byte
            newValue[index] = cvtToByte(sb);
        }
        b = newValue;
        return (true);
    }

    protected byte cvtToByte(StringBuilder sb) {
        int msb = hexString.indexOf(sb.charAt(0));
        int lsb = hexString.indexOf(sb.charAt(1));
        return ((byte) (msb * 16 + lsb));
    }

    @Override
    public boolean matches(String s, boolean ignoreCase) {
        return (false);
    }
    
    @Override
    public int getByteLength() {
        return(vkCell.getDataLength());
    }    
}
