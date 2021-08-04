package CBF;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * Implements a File Allocation Table
 */
public class CBFFAT {
    
    private static final int entrySize = 4;     // number of bytes occupied by FAT sector number (int)
    private static final int endOfChain = -2;
    
    private final ByteBuffer buffer;
    private final int sectorSize;
    //private final int entriesPerSector;
    
    /**
     * Constructor
     * FAT is created with a byte buffer
     * 
     * @param buffer 
     * @param sectorSize 
     */
    public CBFFAT(ByteBuffer buffer, int sectorSize) {
        this.buffer = buffer;
        this.sectorSize = sectorSize;
        //this.entriesPerSector = sectorSize / entrySize;
    }
    
    /**
     * Returns a list of sectors in the chain starting at startSector
     * 
     * @param startSector
     * @return 
     * @throws CBF.CBFException 
     */
    public int[] getChain(int startSector) throws CBFException {
        var list = new ArrayList<Integer>();
        list.add(startSector);
        // walk chain of sectors
        var next = getFATEntry(startSector);
        while (next != endOfChain) {
            list.add(next);
            next = getFATEntry(next);
        }
        var chain = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            chain[i] = list.get(i);
        }
        return chain;
    }
    
    /**
     * returns the FAT entry for the given index
     * @param index
     * @return 
     * @throws CBF.CBFException 
     */
    public int getFATEntry(int index) throws CBFException {
        // determine which sector this index is in
        var sectorOffset = index * entrySize;
        
        // validate
        if (sectorOffset < buffer.capacity()) {
            buffer.position(sectorOffset);
            return(buffer.getInt());
        } else {
            throw new CBFException ("Index " + index + " is out of bounds for FAT");
        }
    }
}
