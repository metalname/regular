package registry;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import registry.Cell.NkCell;
import registry.Cell.RegistryCell;
import registry.Cell.RegistryCellType;
import registry.Nodes.KeyNode;

/**
 *
 * RegistryHive is the main handler class for a registry file
 */
public class RegistryHive {

    // registryBuffer - byte buffer image of registry file
    protected RegistryBuffer registryBuffer;
    // filename
    protected String filename;
    // isDirty - boolean flag that indicates if the registry buffer has been modified    
    protected boolean isDirty;
    // isOpen - boolean flag that indicates if the registry is open
    protected boolean isOpen;
    // registryHeader
    protected RegistryHeader registryHeader;
    // error listener
    protected ErrorListener errorListener;
    // root key node
    protected KeyNode rootNode;

    /**
     * Constructor
     * @param filename: path to registry file
     */
    public RegistryHive(String filename) {
        this.filename = filename;
    }

    /* Open registry */
    /* Reads entire registry file into buffer */
    public boolean open() throws RegistryException {
        try {
            RandomAccessFile raf = new RandomAccessFile(filename, "r");
            byte[] b = new byte[(int) raf.length()];        // allocate byte buffer
            raf.read(b);                                    // read entire registry into buffer
            registryBuffer = new RegistryBuffer(b);         // create a RegistryBuffer object from byte buffer
            raf.close();
            
            registryHeader = new RegistryHeader();          // create RegistryHeader object from byte buffer
            if (registryHeader.loadHeader(registryBuffer)) {
                if (loadRootKey()) {        // locate root key
                    isOpen = true;
                    return (true);
                } else {
                    close();
                    return (false);
                }
            } else {
                error("Invalid signature"); // this is not a registry file
                close();
                return (false);
            }
        } catch (IOException e) {
            error(e.getMessage());
            return (false);
        }
    }

    /**
     * Overloaded open method
     * Sets error handler interface
     * @param errorListener instance of ErrorListener interface
     * @return success or failure of open
     * @throws RegistryException 
     */
    public boolean open(ErrorListener errorListener) throws RegistryException {
        this.errorListener = errorListener;
        return (open());
    }

    /**
     * Error handler
     * @param message : error message
     */
    private void error(String message) {
        // if this hive has an error hanlder attached, use that, else print to stdout
        if (errorListener != null) {
            errorListener.notify(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Save registry
     * Rewrites buffer to disk
     * @return success or failure of save 
     */
    public boolean save() {
        if (saveAs(filename)) {
            isDirty = false;
            return (true);
        } else {
            return (false);
        }
    }

        /**
     * Save registry to specified filename
     * 
     * @param f
     * @return success or failure of save 
     */
    public boolean saveAs(String f) {
        try {
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            raf.write(registryBuffer.array());
            raf.close();
            return (true);
        } catch (IOException e) {
            error(e.getMessage());
            return (false);
        }
    }

    /**
     * Close registry
     */
    public void close() {
        registryHeader = null;
        registryBuffer = null;
        isOpen = false;
        isDirty = false;
    }

    /**
     * Getter for filename
     * 
     * @return 
     */
    public String getFilename() {
        return(filename);
    }
    
    /**
     * Locates and loads root key
     * @return success or failure of operation
     * @throws RegistryException 
     */
    protected boolean loadRootKey() throws RegistryException {
        // create registry cell at location pointed to by registry header
        RegistryCell rc = RegistryCell.makeRegistryCell(registryHeader.getRootKeyOffset(), registryBuffer);
        // check cell type - must be NK
        if (rc.getCellType() == RegistryCellType.T_NK) {
            rootNode = new KeyNode((NkCell) rc, this);
            return (true);
        } else {
            error("Root key corrupted");    // something went wrong
            return (false);
        }
    }

    /**
     * Attempts to load a key given a path
     * @param path path to key from root
     * @return KeyNode object if successful, null if not
     * @throws RegistryException 
     */
    public KeyNode loadKeyFromPath(String path) throws RegistryException {
        return (new KeyNode(path, this));
    }

    /**
     * Returns root node
     * @return 
     */
    public KeyNode getRootNode() {
        return (rootNode);
    }

    /**
     * Getter for isDirty flag
     * @return 
     */
    public boolean isDirty() {
        return (isDirty);
    }

    /**
     * Getter for isOpen flag
     * @return 
     */
    public boolean isOpen() {
        return (isOpen);
    }

    /**
     * Getter for registryHeader object
     * @return 
     */
    public RegistryHeader getRegistryHeader() {
        return (registryHeader);
    }

    /**
     * Getter for registryBuffer object
     * @return 
     */
    public RegistryBuffer getRegistryBuffer() {
        return (registryBuffer);
    }

    /**
     * Alias: setter for isDirty flag
     * @param flag 
     */
    public void hiveChanged(boolean flag) {
        isDirty = flag;
    }

    /**
     * Alias: Getter for isDirty flag
     * @return 
     */
    public boolean hiveChanged() {
        return (isDirty);
    }

    /**
     * Creates a RegistrySearcher object from this root key
     * @return instance of RegistrySearcher class
     * @throws RegistryException 
     */
    public RegistrySearcher newSearch() throws RegistryException {
        return (new RegistrySearcher(rootNode));
    }

    /**
     * Traverses all cells in hive and calls supplied function
     * @param function function to be executed for each cell
     * @return 
     */
    public boolean traverse(HiveFunction function) {
        // iterate over chunks
        int offset = 0;
        while (registryBuffer.position() < registryBuffer.capacity()) {
            Hbin hbin = new Hbin(offset);
            hbin.loadHeader(registryBuffer);
            int ofs = Hbin.headerSize;
            // iterate over cells in chunck
            while (ofs < hbin.getHbinSize()) {
                // create a cell object for this offset
                RegistryCell cell = RegistryCell.makeRegistryCell(offset + ofs, registryBuffer);
                cell.loadHeader(registryBuffer);
                if (cell.size() == 0) {
                    break;
                }
                // call supplied function for this cell
                if (function.process(cell)) {
                    return (true);
                }
                ofs += Math.abs(cell.size());
            }
            if (hbin.getHbinSize() == 0) {
                break;
            }
            offset += hbin.getHbinSize();
        }
        return (false);
    }

    /**
     * Wipes deleted key space in hive
     * Traverses hive looking for deleted keys and wipes each
     * @return number of keys wiped
     */
    public int wipeHiveFreeSpace() {
        AtomicInteger count = new AtomicInteger(0);
        Random random = new Random(System.currentTimeMillis());
        traverse(cell -> {
            if (cell.isDeleted()) {
                registryBuffer.position(cell.getOffset());
                registryBuffer.putInt(cell.size());
                for (int i = 0; i < cell.length(); i++) {
                    registryBuffer.putByte((byte) random.nextInt());
                }
                count.incrementAndGet();
            }
            return (false);

        });
        if (count.get() > 0) {
            hiveChanged(true);
        }
        return (count.get());
    }

    // look for an empty cell of at least the specified size
    // returns the offset of the new cell
    
    /**
     * Finds an empty cell
     * @param size minimum size of empty cell
     * @return offset to empty cell
     */
    public int findEmptyCell(int size) {
        ArrayList<RegistryCell> found = new ArrayList<>();
        traverse(cell -> {
            if ((cell.isDeleted()) && (cell.length() >= size)) {
                found.add(cell);
                return (true);
            }
            return (false);
        });
        if (found.size() > 0) {
            return (found.get(0).getOffset());
        } else {
            return (-1);
        }
    }

}
