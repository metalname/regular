package CBF;

import CBF.Directory.Directory;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * Utility package for reading and writing Compound Binary Files (CBF) A CBF is
 * a file that contains a FAT filesystem within itself. It is used by
 * Microsoft's OLE system
 *
 * Spec:
 * https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-cfb/53989ce4-7b05-4f8d-829b-d08d6148375b
 */
public class CBF {

    private final String filename;      // path to the CBF
    private ByteBuffer buffer;          // the entire CBF will be loaded into this buffer
    private CBFHeader header;           // CBF header
    private boolean hasDIFAT;           // indicates that the DIFAT is used
    private boolean hasMiniStream;      // indicates that the miniStream is used
    private CBFSectorList sectorList;   // sector handler
    private CBFFAT FAT;
    private CBFSectorList miniFATSectorList;    // sector handler for miniStream
    private CBFFAT miniFAT;                     // mini FAT
    private Directory directory;                // directory object
    private CBFSectorList miniStream;           // sector handler for miniStream

    /**
     * Constructor
     *
     * @param filename
     * @throws CBFException
     */
    public CBF(String filename) throws CBFException {
        this.filename = filename;
        load();
        init();
    }

    /**
     * Static method for creating ByteBuffer with LITTLE_ENDIAN set
     *
     * @param b
     * @return
     */
    public static ByteBuffer makeByteBuffer(byte[] b) {
        var bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return (bb);
    }

    /**
     * Reads the contents of the CBF into the buffer
     */
    private void load() throws CBFException {
        byte[] b;
        try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
            b = new byte[(int) raf.length()];
            raf.read(b);
        } catch (IOException e) {
            throw new CBFException(e.getMessage());
        }
        buffer = makeByteBuffer(b);
    }

    /**
     * Loads CBF structures
     *
     * @throws CBFException
     */
    private void init() throws CBFException {
        // load header
        header = new CBFHeader(buffer, filename);
        System.out.println(header.dump());

        // set flags
        hasDIFAT = header.getCountDIFATSectors() > 0;
        hasMiniStream = header.getCountMiniFATSectors() > 0;

        // for now, we don't support files with DIFAT
        if (hasDIFAT) {
            throw new CBFException("Unsupported file " + filename + " - DIFAT entries");
        }

        // create a sector handler
        sectorList = new CBFSectorList(buffer, header.getSectorSize(), 1);

        // create the FAT
        createFAT();

        // create the mini FAT
        createMiniFAT();

        // create the directory structure
        createDirectory();

        // create miniStream
        createMiniStream();
    }

    /**
     * Create a FAT object from the buffer
     *
     * @throws CBFException
     */
    private void createFAT() throws CBFException {
        var sectorSize = header.getSectorSize();
        // create a buffer from the DIFAT sectors
        byte[] b = new byte[(header.getCountDIFATSectors() + 1) * sectorSize];

        for (int i = 0; i < header.getCountDIFATSectors() + 1; i++) {
            System.arraycopy(sectorList.get(header.getDIFAT()[i]).array(), 0, b, i * sectorSize, sectorSize);
        }
        // create a FAT handler
        FAT = new CBFFAT(makeByteBuffer(b), sectorSize);
    }

    /**
     * Creates a buffer from the sector handler for the specified sector
     *
     * @param sector
     * @return
     * @throws CBFException
     */
    private ByteBuffer getBufferForSector(int sector) throws CBFException {
        // get chain of sectors from FAT
        var sectors = FAT.getChain(sector);
        // careet buffer from sector handler
        return (sectorList.get(sectors));
    }

    /**
     * Creates a buffer from the mini sector handler for the specified sector
     *
     * @param sector
     * @return
     * @throws CBFException
     */
    private ByteBuffer getBufferForMiniSector(int sector) throws CBFException {
        // get chain of sectors from FAT
        var sectors = miniFAT.getChain(sector);
        // careet buffer from sector handler
        return (miniStream.get(sectors));
    }

    /**
     * Load the miniStream
     *
     */
    private void createMiniFAT() throws CBFException {
        // the miniStream FAT data is stored as a regular file in the outer FAT
        // the start sector of the chain is located in the header

        // skip the miniFAT creation if the header indicates that miniFAT is not used        
        if (hasMiniStream) {
            // get a buffer from the FAT for miniFAT starting sector
            var miniFATBuffer = getBufferForSector(header.getFirstMiniFATSector());
            // create the miniFAT
            miniFAT = new CBFFAT(miniFATBuffer, header.getMiniSectorSize());
        }
    }

    /**
     * Creates the directory structure
     *
     * @throws CBFException
     */
    private void createDirectory() throws CBFException {
        // directory stream is located in the outer FAT
        // starting sector is specified in the header
        // create a buffer containing the directory entries
        var dirBuffer = getBufferForSector(header.getFirstDirectorySector());
        // create the directory object
        directory = new Directory(dirBuffer);
    }

    /**
     * Creates the miniStream
     */
    private void createMiniStream() throws CBFException {
        // skip the miniFAT creation if the header indicates that miniFAT is not used
        if (hasMiniStream) {
            // starting sector and size of miniStream is located in the root directory entry
            var entry = directory.getEntryByName("Root Entry");
            if (entry == null) {
                throw new CBFException("No Root Entry for file " + filename);
            }
            // starting sector of miniStream is in startSect of root entry
            // create a buffer for miniStream
            var miniBuffer = getBufferForSector(entry.getStartSect());
            // create a sector handler from the miniBuffer
            miniStream = new CBFSectorList(miniBuffer, header.getMiniSectorSize(), 0);
        }
    }

    public ByteBuffer getStream(String name) throws CBFException {
        var entry = directory.getEntryByName(name);
        if (entry != null) {
            // check stream size to dtermine if it is located in FAT or miniFAT
            var size = entry.getStreamSize();
            if (size < header.getMiniStreamCutoffSize()) {
                return (getBufferForMiniSector(entry.getStartSect()));
            } else {
                return (getBufferForSector(entry.getStartSect()));
            }
        }
        return (null);
    }

}
