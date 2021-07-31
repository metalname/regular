package registry;

import utils.BufferedRandomAccessFile;
import registry.element.DataElement;
import registry.element.DataElementArray;
import registry.element.ElementType;
import registry.element.IntDataElement;

/**
 *
 * A registry hive is composed of one or more chunks known as hive bins
 * Each bin has a header containing information about the chunk
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#hive-bin
 */
public class Hbin {

    protected int fileOffset;       // files offset of this chunk in registry file
    protected int hbinSize;         // size of this chunk (always a multiple of 0x1000
    protected int ix_hbinSize;      // index of chunk size data element

    protected BufferedRandomAccessFile reader;
    protected int startIndex, endIndex;
    protected DataElementArray elements;

    public final static int headerSize = 0x20;
    
    /**
     * Constructor
     * @param fileOffset offset of this bin in registry file
     */
    public Hbin(final int fileOffset) {
        this.fileOffset = fileOffset;
    }
    
    /**
     *
     * @return
     */
    public DataElementArray getDataElementArray() {
        return(elements);
    }
        
    public int getStartIndex() {
        return(startIndex);
    }
    
    public int getEndIndex() {
        return(endIndex);
    }
    
    /**
     * Loads bin header from registry byte buffer
     * @param buffer 
     */
    public void loadHeader(RegistryBuffer buffer)  {
        buffer.position(fileOffset);
        int ofs = 0;
        elements = new DataElementArray();
        
        // first 4 bytes is signature = 'hbin'
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_ASCIISZ, 4, ofs, "Signature", false)).read(fileOffset, buffer);
        
        // next 4 bytes is offset of this chunk relative to first chunk
        // first chunk will have offset 0x00000000 (skips registry header)
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "HBIN Offset", false)).read(fileOffset, buffer);
        
        // next 4 bytes is chunk size (always a multiple of 4096)
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "HBIN Size", false)).read(fileOffset, buffer);
        ix_hbinSize = elements.lastElement();
        
        // there is a timestamp at offset 0x14
        // only first bin has a value for timestamp
        elements.addElement(DataElement.makeDataElement(ElementType.E_FILETIME, 0, 0x14, "HBIN Timestamp", false)).read(fileOffset, buffer);
        hbinSize = getHbinSize();        
    }

    /**
     * get bin size
     * @return 
     */
    protected int getHbinSize() {
        return(((IntDataElement) elements.get(ix_hbinSize)).getValueI());
    }
    
    /**
     * Getter for fileOffset
     * @return 
     */
    public int getOffset() {
        return(fileOffset);
    }
    
    @Override
    public String toString() {
        return("Hive @" + fileOffset);
    }
    
    /**
     * Construct a hive bin path
     * @return 
     */
    public String getPath() {
        return("Header\\" + toString());
    }
    
    /**
     * Reload header from new registry hive
     * @param buffer 
     */
    public void update(RegistryBuffer buffer) {
        buffer.position(fileOffset);
        for (DataElement e : elements) {
            e.write(fileOffset, buffer);
        }
    }     

}
