package registry.element;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import registry.RegistryBuffer;

/**
 *
 * Binary data element
 */
public class BinDataElement extends DataElement {

    private byte[] value;
    private ByteBuffer buffer;
    private static final String hexString = "0123456789ABCDEF";

    public BinDataElement(final ElementType type,
            final int length,
            final int offset,
            final String label,
            final boolean follow) {
        super(type, length, offset, label, follow);
    }

    // read binary data from file up to specified length
    @Override
    public int read(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        value = new byte[length];
        for (int i = 0; i < length; i++) {
            value[i] = buffer.getByte();
        }
        setBuffer();
        return (offset + length);
    }

    // craete a byte buffer from the data
    protected void setBuffer() {
        buffer = ByteBuffer.wrap(value);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    // write binary data to file
    @Override
    public void write(int origin, final RegistryBuffer buffer) {
        buffer.position(origin + offset);
        if (value.length > length) {
            for (int i = 0; i < length; i++) {
                buffer.putByte(value[i]);
            }
        } else {
            for (int i = 0; i < value.length; i++) {
                buffer.putByte(value[i]);
            }
            for (int i = value.length; i < length; i++) {
                buffer.putByte((byte) 0);
            }
        }
    }

    // create a string representation of the data as hex bytes
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(FormatElement.toHexString((byte) value[i]));
        }
        return (sb.toString());
    }

    // return data as a byte array
    public byte[] getData() {
        return (value);
    }

    // return data as a byte buffer
    public ByteBuffer getBuffer() {
        return (buffer);
    }

    // clears the data
    public void zero() {
        value = new byte[length];
        for (int i = 0; i < length; i++) {
            value[i] = 0;
        }
        setBuffer();
    }

    // sets the data from the string value
    // string is assumed to be a series of hex byte digits, e.g. "01 fa 7b"
    // method will return false if data is not in this format
    @Override
    public boolean setData(final String data) {
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
        value = newValue;
        return (true);
    }

    public void setData(byte[] buffer) {
        value = buffer;
    }

    protected byte cvtToByte(StringBuilder sb) {
        int msb = hexString.indexOf(sb.charAt(0));
        int lsb = hexString.indexOf(sb.charAt(1));
        return ((byte) (msb * 16 + lsb));
    }

    @Override
    public int getSize() {
        return (length);
    }
}
