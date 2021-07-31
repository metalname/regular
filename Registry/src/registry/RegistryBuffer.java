package registry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import registry.Cell.DataCell;
import registry.Cell.RegistryCell;
import utils.NumFormat;

/**
 *
 * Wrapper for a byte buffer
 * Buffer will map an entire registry hive
 */
public class RegistryBuffer {

    private final ByteBuffer buffer;

    /**
     * Constructor
     * @param b byte buffer to be wrapped
     */
    public RegistryBuffer(byte[] b) {
        buffer = ByteBuffer.wrap(b);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Gets position of buffer pointer
     * Cell offsets are relative to start of first chunk, not the registry header
     * @return 
     */
    public int position() {
        return (buffer.position() - RegistryHeader.headerSize);
    }

    /**
     * Sets buffer position
     * @param position 
     */
    public void position(int position) {
        buffer.position(position + RegistryHeader.headerSize);
    }

    /**
     * Writes a byte to buffer
     * @param b 
     */
    public void putByte(byte b) {
        buffer.put(b);
    }

    /**
     * Reads a byte from buffer
     * @return 
     */
    public byte getByte() {
        return (buffer.get());
    }

    /** 
     * Writes a short (2-byte) number to buffer
     * @param s 
     */
    public void putShort(short s) {
        buffer.putShort(s);
    }

    /**
     * Reads a short (2-byte) number from buffer
     * @return 
     */
    public short getShort() {
        return (buffer.getShort());
    }

    /**
     * Writes a char to buffer
     * @param c 
     */
    public void putChar(char c) {
        buffer.putChar(c);
    }

    /**
     * Reads a char from buffer
     * @return 
     */
    public char getChar() {
        return (buffer.getChar());
    }

    /**
     * Writes an int to buffer
     * @param i 
     */
    public void putInt(int i) {
        buffer.putInt(i);
    }

    /**
     * Reads an int from buffer
     * @return 
     */
    public int getInt() {
        return (buffer.getInt());
    }

    /**
     * Writes a long int to buffer
     * @param l 
     */
    public void putLong(long l) {
        buffer.putLong(l);
    }

    /**
     * Reads a long int from buffer
     * @return 
     */
    public long getLong() {
        return (buffer.getLong());
    }

    /**
     * Returns byte buffer as array
     * @return 
     */
    public byte[] array() {
        return (buffer.array());
    }

    /**
     * Creates a registry cell from specified position
     * @param offset 
     * @return
     * @throws RegistryException 
     */
    public RegistryCell loadCell(int offset) throws RegistryException {
        if ((offset > 0) && (offset < buffer.capacity())) {
            RegistryCell cell = RegistryCell.makeRegistryCell(offset, this);
            if (cell != null) {
                return (cell);
            } else {
                throw new RegistryException("Could not load cell @" + NumFormat.numToHex(offset));
            }
        } else {
            throw (new RegistryException("Offset " + offset + " is out of range"));
        }
    }

    /**
     * Creates a data cell from specified position
     * @param offset
     * @return
     * @throws RegistryException 
     */
    public DataCell loadDataCell(int offset) throws RegistryException {
        if ((offset > 0) && (offset < buffer.capacity())) {
            return (RegistryCell.makeDataCell(offset, this));
        } else {
            throw (new RegistryException("Offset " + offset + " is out of range"));
        }
    }
    
    /**
     * Returns buffer size
     * @return 
     */
    public int capacity() {
        return(buffer.capacity());
    }
}
