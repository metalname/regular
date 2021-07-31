/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CBF.file;

import BinBuffer.BinHelper;
import CBF.directory.DirectoryEntry;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author 
 *
 * Implements a simple byte buffer with an associated sector map This is used to
 * figure out which sectors need to be updated when the file is written
 */
public class FileBuffer implements Iterator<byte[]>, Iterable<byte[]>, BinHelper {

    private final List<Integer> secArray = new ArrayList<>();
    private int position = 0, currentSector = 0, currentOffset = 0, translatedSector = 0;
    private final FileSystem fs;
    private int lastIndex = 0;
    private ByteOrder order = ByteOrder.LITTLE_ENDIAN;
    private int size;
    private DirectoryEntry sid;

    public FileBuffer(FileSystem fs, DirectoryEntry sid) {
        this.fs = fs;
        this.sid = sid;
        if (sid != null) {
            size = sid.getSize();
        }
    }

    public void addSector(int secNum) throws IOException {
        // check that sector has not already been added
        if (secArray.contains(secNum)) {
            throw new IOException("Sector " + secNum + " is already in FileBuffer");
        }
        secArray.add(secNum);
        if (sid == null) {
            size += fs.getSectorSize();
        }
    }
    
    @Override
    public void order(ByteOrder order) {
      this.order = order;  
    }
    
    @Override
    public ByteOrder order() {
        return(order);
    }

    @Override
    public int position() {
        return (position);
    }

    @Override
    public void position(int position) {
        this.position = position;
        setCurrentBuffer();
    }

    protected void setCurrentBuffer() {
        currentSector = position / fs.getSectorSize();
        currentOffset = position % fs.getSectorSize();
        if (position < size()) {
            translatedSector = secArray.get(currentSector);
        }
    }
    
    public boolean eof() {
        return(position >= size());
    }

    protected void advancePointer() {
        position++;
        setCurrentBuffer();
        if (position > size) {
            size = position;
        }
    }

    protected byte[] getCurrentBuffer() {
        return (fs.getBuffer(translatedSector));
    }

    // set byte at current position
    @Override
    public void put(byte b) {
        fs.putByte(translatedSector, currentOffset, b);
        advancePointer();
    }
    
    // return byte at current position
    @Override
    public byte get() {
        byte b = fs.getByte(translatedSector, currentOffset);
        advancePointer();
        return (b);
    }

    // returns data for specified sector
    public byte[] getBufferForIndex(int index) {
        return (fs.getBuffer(index));
    }

    @Override
    public int size() {
        return (size);
    }
    
    public int maxSize() {
        return(secArray.size() * fs.getSectorSize());
    }

    public void wipe() {
        position(0);
        for (int i = 0; i < size(); i++) {
            put((byte) 0);
        }
    }
    
    public void delete() {
        // update FAT entries
        for (int sector: secArray) {
            fs.deleteFATEntry(sector);
        }
    }

    @Override
    public boolean hasNext() {
        return (lastIndex < secArray.size());
    }

    @Override
    public byte[] next() {
        return (fs.getBuffer(secArray.get(lastIndex++)));
    }

    @Override
    public Iterator<byte[]> iterator() {
        lastIndex = 0;
        return (this);
    }
    
    @Override
    public boolean hasRemaining() {
        return(!eof());
    }    
    
    public void truncate() {
        position(0);
        size = 0;
    }
    
    public void close() {
        if (sid != null) {
            sid.setSize(size);
        }
    }
    
    public FileSystem getFileSystem() {
        return(fs);
    }
    
    public List<Integer> getSectorList() {
        return(secArray);
    }
}
