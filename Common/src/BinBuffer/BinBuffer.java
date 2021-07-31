/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author 
 */
public class BinBuffer implements BinHelper {

    private final byte[] buffer;
    private int position = 0;
    private ByteOrder order = ByteOrder.LITTLE_ENDIAN;

    public BinBuffer(int size) {
        this(new byte[size]);
    }
    
    public BinBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer byteBuffer() {
        return(ByteBuffer.wrap(buffer));
    }
    
    public byte[] buffer() {
        return(buffer);
    }
    
    @Override
    public byte get() {
        if (position < buffer.length) {
            return (buffer[position++]);
        } else {
            throw new RuntimeException("Buffer overflow");
        }
    }

    @Override
    public void put(byte b) {
        if (position < buffer.length) {
            buffer[position++] = b;
        } else {
            throw new RuntimeException("Buffer overflow");
        }
    }

    @Override
    public int size() {
        return (buffer.length);
    }
    
    public int remaining() {
        return(size() - position);
    }

    @Override
    public int position() {
        return (position);
    }

    @Override
    public void position(int position) {
        if ((position >= 0) && (position < buffer.length)) {
            this.position = position;
        }
    }

    @Override
    public boolean hasRemaining() {
        return (position < buffer.length - 1);
    }

    @Override
    public void order(ByteOrder order) {
        this.order = order;
    }

    @Override
    public ByteOrder order() {
        return (order);
    }

    public byte[] data() {
        return (buffer);
    }
    
    public void skip(int count) {
        position += count;
    }

}
