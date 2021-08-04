package CBF;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * The header occupies the first sector of the CBF. It is either 512 bytes or
 * 4096 bytes, depending on the header version number
 */
public class CBFHeader {

    private static final byte[] magic = 
        {(byte) 0xd0, (byte) 0xcf, (byte) 0x11, (byte) 0xe0, 
         (byte) 0xa1, (byte) 0xb1, (byte) 0x1a, (byte) 0xe1};
    private static final int headerOffset = 76;
    private static final int countDIFATEntries = 109;
    
    private final ByteBuffer buffer;
    private final String filename;
    private final byte[] signature = new byte[8];
    private final byte[] classID = new byte[16];
    private short minorVersion;
    private short majorVersion;
    private short byteOrder;
    private short sectorShift;
    private int sectorSize;
    private short miniSectorShift;
    private int miniSectorSize;
    private final byte[] reserved = new byte[6];
    private int countDirectorySectors;
    private int countFATSectors;
    private int firstDirectorySector;
    private int txSig;
    private int miniStreamCutoffSize;
    private int firstMiniFATSector;
    private int countMiniFATSectors;
    private int firstDIFATSector;
    private int countDIFATSectors;
    private int[] DIFAT = new int[countDIFATEntries];
    
    public CBFHeader(ByteBuffer buffer, String filename) throws CBFException {
        this.buffer = buffer;
        this.filename = filename;
        load();
    }

    private void load() throws CBFException {
        buffer.position(0);
        buffer.get(signature);
        buffer.get(classID);
        minorVersion = buffer.getShort();
        majorVersion = buffer.getShort();
        byteOrder = buffer.getShort();
        sectorShift = buffer.getShort();
        sectorSize = 1 << (int) sectorShift;
        miniSectorShift = buffer.getShort();
        miniSectorSize = 1 << (int) miniSectorShift;     
        buffer.get(reserved);
        countDirectorySectors = buffer.getInt();
        countFATSectors = buffer.getInt();
        firstDirectorySector = buffer.getInt();
        txSig = buffer.getInt();
        miniStreamCutoffSize = buffer.getInt();
        firstMiniFATSector = buffer.getInt();
        countMiniFATSectors = buffer.getInt();
        firstDIFATSector = buffer.getInt();
        countDIFATSectors = buffer.getInt();
        
        // sanity check - offset must be 76
        if (buffer.position() != headerOffset) {
            throw new CBFException("Unexpected offset " + buffer.position() + " in header - should be " + headerOffset);
        }
        
        // load DIFAT
        for (var i = 0; i < countDIFATEntries; i++) {
            DIFAT[i] = buffer.getInt();
        }
        
        // validate header
        validate();
    }
    
    /**
     * Check that the header has a valid signature
     * 
     * @throws CBFException 
     */
    private void validate() throws CBFException {
        if (Arrays.equals(magic, signature)) {
            // only major versions 3 and 4 are supported
            if (!((majorVersion == 3) || (majorVersion == 4))) {
                throw new CBFException("Unsupported version " + majorVersion);
            }
        } else {
            throw new CBFException("Bad signature in CBF header for " + this.filename);
        }
    }
    
    /**
     * Write all values to a string for debugging
     * 
     * @return 
     */
    public String dump() {
       var sb = new StringBuilder();
       sb.append("Minor Version: ").append(minorVersion).append('\n').
               append("Major Version: ").append(majorVersion).append('\n').
               append("Sector Size: ").append(sectorSize).append('\n').
               append("Mini Sector Size: ").append(miniSectorSize).append('\n').
               append("Number of Directory Sectors: ").append(countDirectorySectors).append('\n').
               append("Number of FAT sectors: ").append(countFATSectors).append('\n').
               append("First Directory Sector: ").append(firstDirectorySector).append('\n').
               append("Mini Stream Cutoff Size: ").append(miniStreamCutoffSize).append('\n').
               append("First MiniFAT Sector: ").append(firstMiniFATSector).append('\n').
               append("Number of MiniFAT Sectors: ").append(countMiniFATSectors).append('\n').
               append("First DIFAT Sector: ").append(firstDIFATSector).append('\n').
               append("Number of DIFAT Sectors: ").append(countDIFATSectors).append('\n');
       return(sb.toString());
    }
    
    /**
     * Getter for countDIFATSectors
     * 
     * @return 
     */
    public int getCountDIFATSectors() {
        return(countDIFATSectors);
    }
    
    /**
     * Getter for countMiniFATSectors
     * 
     * @return 
     */
    public int getCountMiniFATSectors() {
        return(countMiniFATSectors);
    }
    
    /**
     * Getter for sectorSize
     * 
     * @return 
     */
    public int getSectorSize() {
        return(sectorSize);
    }
    
    /**
     * Getter for DIFAT array
     * 
     * @return 
     */
    public int[] getDIFAT() {
        return(DIFAT);
    }
     
    /**
     * Getter for firstMiniFATSector
     * @return 
     */
    public int getFirstMiniFATSector() {
        return(firstMiniFATSector);
    }
    
    /**
     * Getter for miniSectorSize
     * @return 
     */
    public int getMiniSectorSize() {
        return(miniSectorSize);
    }
    
    /**
     * Getter for firstDirectorySector
     * @return 
     */
    public int getFirstDirectorySector() {
        return(firstDirectorySector);
    }
    
    /**
     * Getter for miniStreamCutoffSize
     * @return 
     */
    public int getMiniStreamCutoffSize() {
        return(miniStreamCutoffSize);
    }
}
