package CBF;

import java.nio.ByteBuffer;

/**
 *
 * Utility class for implementing a sector array
 */
public class CBFSectorList {
    
    private final ByteBuffer buffer;      // entire CBF is loaded into this buffer
    private final int sectorSize;         // will be 512 bytes for version3 files, 4096 for version 4
    private final int offset;             // number of sectors to skip from the start of the file
    
    public CBFSectorList(ByteBuffer buffer, int sectorSize, int offset) {
        this.buffer = buffer;
        this.sectorSize = sectorSize;
        // FAT sectors are relative to header sector
        // offset is the number of sectors to skip from the start of the file
        // should always be 1
        this.offset = offset;
    }
    
    /**
     * Returns a buffer containing data from the given sector
     * 
     * @param sector
     * @return
     * @throws CBFException 
     */
    private byte[] getSectorBuffer(int sector) throws CBFException {
        var position = (sector + offset) * sectorSize;
        buffer.position(position);
        byte[] b = new byte[sectorSize];
        buffer.get(b);
        return(b);
    }
    
    /**
     * Returns a buffer containing data from a sector array
     * The sector array will usually be created by chaining a FAT
     * 
     * @param sectors
     * @return
     * @throws CBFException 
     */
    public ByteBuffer get(int[] sectors) throws CBFException {
        byte[] b = new byte[sectors.length * sectorSize];
        for (int i = 0; i < sectors.length; i++) {
            System.arraycopy(getSectorBuffer(sectors[i]), 0, b, i * sectorSize, sectorSize);
        }
        return CBF.makeByteBuffer(b);
    }
    
    public ByteBuffer get(int sector) throws CBFException {
        return CBF.makeByteBuffer(getSectorBuffer(sector));
    }
}
