package utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author 
 */
public class BufferedRandomAccessFile {

    protected ByteBuffer buffer;
    protected final static int minBufSize = 0x1000000; // set mimimum buffer size to 16 MB
    protected final static int extentSize = 0x800000; // set next extent size to 8 MB
    protected int fsize = 0;
    protected RandomAccessFile raf;
    protected boolean bufferDirty = false;

    public BufferedRandomAccessFile(File file, String mode) throws FileNotFoundException, IOException {
        this(file, false, mode);
    }

    public BufferedRandomAccessFile(String file, String mode) throws FileNotFoundException, IOException {
        this(new File(file), false, mode);
    }

    public BufferedRandomAccessFile(File file, boolean append, String mode) throws FileNotFoundException, IOException {
        raf = new RandomAccessFile(file, mode);

        // allocate buffer
        if (raf.length() >= Integer.MAX_VALUE) {
            throw new IOException("File exceeds maximum buffer size");
        }
        fsize = (int) raf.length();
        if (fsize < minBufSize) {
            buffer = ByteBuffer.allocate(minBufSize);
        } else {
            buffer = ByteBuffer.allocate(append ? (fsize * 2) : fsize);
        }
        // read buffer from file
        raf.read(buffer.array(), 0, fsize);
        // if append flag is set, move buffer pointer to end of file
        if (append) {
            buffer.position(fsize);
        }
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public long length() {
        return (fsize);
    }

    public BufferedRandomAccessFile(String file, boolean append, String mode) throws FileNotFoundException, IOException {
        this(new File(file), append, mode);
    }

    public long getFilePointer() {
        return (buffer.position());
    }

    public void flushBuffer() throws IOException {
        if (bufferDirty) {
            raf.seek(0);
            raf.write(buffer.array(), 0, fsize);
        }
        bufferDirty = false;
    }

    public void close() throws IOException {
        // write buffer back to file
        flushBuffer();
        raf.close();
    }

    public boolean eof() throws IOException {
        return (buffer.position() >= fsize);
    }

    // returns the number of bytes between the buffer pointer and the end of the file
    protected int remainingBytes() {
        return (buffer.capacity() - buffer.position());
    }

    // checks if there is enough space in the buffer to read the specified number of bytes
    protected boolean canRead(int i) throws IOException {
        if (remainingBytes() >= i) {
            return (true);
        } else {
            throw new EOFException();
        }
    }

    public byte read() throws IOException {
        // check buffer length
        if (canRead(DataSize.BYTE)) {
            return (buffer.get());
        } else {
            throw new IOException("Attempt to read past end of file in BufferedRandomAccessFile.read()");
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        // check buffer length

        int i;
        for (i = 0; i < len; i++) {
            byte bb = (byte) read();
            b[off + i] = bb;
        }
        return (len);
    }

    public int read(byte[] b) throws IOException {
        return (read(b, 0, b.length));
    }

    // changed buffer position by specified number of bytes
    protected void adjustBufferPosition(int n) {
        buffer.position(buffer.position() + n);
    }

    public int skipBytes(int n) throws IOException {
        if (n > remainingBytes()) {
            adjustBufferPosition(n);
            return (n);
        } else {
            int i = remainingBytes();
            adjustBufferPosition(i);
            return (i);
        }
    }

    // check if there is enough space to write n bytes
    // if not, add another extent to the buffer
    protected boolean prepareWrite(int i) throws IOException {
        bufferDirty = true;
        if ((buffer.position() + i) > fsize) {
            fsize += i;
        }
        if (remainingBytes() >= i) {
            return (true);
        } else {
            extendBuffer();
            return (true);
        }
    }

    // extend buffer
    protected void extendBuffer() throws IOException {
        if ((buffer.capacity() + extentSize) >= Integer.MAX_VALUE) {
            throw new IOException("Maximum buffer size exceeded");
        }
        // allocate new buffer
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() + extentSize);
        newBuffer.position(buffer.position());
        // copy to new buffer
        newBuffer.put(buffer.array());
        buffer = newBuffer;
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    protected void adjustFilesize() {
        if (buffer.position() > fsize) {
            fsize = buffer.position();
        }
    }

    public void write(byte b) throws IOException {
        // check buffer size
        if (prepareWrite(DataSize.BYTE)) {
            buffer.put((byte) b);
        }
        //adjustFilesize();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (prepareWrite(len)) {
            for (int i = 0; i < len; i++) {
                write(b[off + i]);
            }
        }
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void seek(long pos) throws IOException {
        if (pos < fsize) {
            //System.out.println("Seeking to " + pos + " while size is " + fsize + " and buffer capacity is " + buffer.capacity());
            buffer.position((int) pos);
        } else {
            throw new IOException("Attempt to seek past end of file");
        }
    }

    public void setLength(long newLength) throws IOException {
        if (newLength < length()) {
            fsize = (int) newLength;
            if (buffer.position() > newLength) {
                buffer.position(fsize);
            }
        } else {
            throw new IOException("Attempt to seek past end of file");
        }
    }

    public boolean readBoolean() throws IOException {
        return (readByte() != 0);
    }

    public byte readByte() throws IOException {
        if (canRead(1)) {
            return (read());
        } else {
            return (-1);
        }
    }

    public short readShort() throws IOException {
        if (canRead(DataSize.SHORT)) {
            return (buffer.getShort());
        } else {
            return (-1);
        }
    }

    public char readChar() throws IOException {
        if (canRead(DataSize.CHAR)) {
            return (buffer.getChar());
        } else {
            return ('\0');
        }
    }

    public int readInt() throws IOException {
        if (canRead(DataSize.INT)) {
            return (buffer.getInt());
        } else {
            return (-1);
        }
    }

    public long readLong() throws IOException {
        if (canRead(DataSize.LONG)) {
            return (buffer.getLong());
        } else {
            return (-1);
        }
    }

    public float readFloat() throws IOException {
        if (canRead(DataSize.FLOAT)) {
            return (buffer.getFloat());
        } else {
            return (-1);
        }
    }

    public double readDouble() throws IOException {
        if (canRead(DataSize.DOUBLE)) {
            return (buffer.getDouble());
        } else {
            return (-1);
        }
    }

    public void writeByte(byte b) throws IOException {
        if (prepareWrite(DataSize.BYTE)) {
            buffer.put(b);
        }
    }

    public void writeBoolean(boolean b) throws IOException {
        writeByte(b ? (byte) 1 : (byte) 0);
    }

    public void writeShort(short s) throws IOException {
        if (prepareWrite(DataSize.SHORT)) {
            buffer.putShort(s);
        }
    }

    public void writeChar(char c) throws IOException {
        if (prepareWrite(DataSize.CHAR)) {
            buffer.putChar(c);
        }
    }

    public void writeInt(int i) throws IOException {
        if (prepareWrite(DataSize.INT)) {
            buffer.putInt(i);
        }
    }

    public void writeLong(long l) throws IOException {
        if (prepareWrite(DataSize.LONG)) {
            buffer.putLong(l);
        }
    }

    public void writeFloat(float f) throws IOException {
        if (prepareWrite(DataSize.FLOAT)) {
            buffer.putFloat(f);
        }
    }

    public void writeDouble(double d) throws IOException {
        if (prepareWrite(DataSize.DOUBLE)) {
            buffer.putDouble(d);
        }
    }

    // write ASCII string, zero-terminated
    public void writeASCIIStringZ(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            write((byte) s.charAt(i));
        }
        write((byte) 0);
    }

    // write ASCII string, length first
    public void writeASCIIStringL(String s) throws IOException {
        // write length
        writeInt(s.length());
        for (int i = 0; i < s.length(); i++) {
            write((byte) s.charAt(i));
        }
    }

    // write UNICODE string, zero-terminated
    public void writeUNICODEStringZ(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            writeChar(s.charAt(i));
        }
        writeChar((char) 0);
    }

    // write UNICODE string, length first
    public void writeUNICODEStringL(String s) throws IOException {
        // write length
        writeInt(s.length());
        for (int i = 0; i < s.length(); i++) {
            writeChar(s.charAt(i));
        }
    }

    // read ASCII string, zero-terminated
    public String readASCIIStringZ() throws IOException {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = readByte()) != 0) {
            sb.append((char) b);
        }
        return (sb.toString());
    }

    // read ASCII string, length first
    public String readASCIIStringL() throws IOException {
        StringBuilder sb = new StringBuilder();
        // read length
        int l = readInt();
        for (int i = 0; i < l; i++) {
            sb.append((char) readByte());
        }
        return (sb.toString());
    }

    // read UNICODE string, zero-terminated
    public String readUNICODEStringZ() throws IOException {
        StringBuilder sb = new StringBuilder();
        char c;
        while ((c = readChar()) != 0) {
            sb.append(c);
        }
        return (sb.toString());
    }

    // read UNICODE string, length first
    public String readUNICODEStringL() throws IOException {
        StringBuilder sb = new StringBuilder();
        // read length
        int l = readInt();
        for (int i = 0; i < l; i++) {
            sb.append(readChar());
        }
        return (sb.toString());
    }
}
