package registry;

import java.io.*;
import registry.element.*;

/**
 *
 * Defines registry header object
 * 
 * https://github.com/msuhanov/regf/blob/master/Windows%20registry%20file%20format%20specification.md#base-block
 */
public class RegistryHeader {

    public final static int headerSize = 0x1000;    // header size is always 4096 bytes
    protected int ix_offsetToRootKey = -1;          // index of data element that holds offset to root key

    protected DataElementArray elements;

    /**
     * Constructor
     */
    public RegistryHeader() {

    }
    
    /**
     * getter for elements
     * @return 
     */
    public DataElementArray getDataElementArray() {
        return(elements);
    }

    /**
     * Loads registry header fields from specified buffer
     * @param buffer
     * @return
     * @throws IOException 
     */
    public boolean loadHeader(RegistryBuffer buffer) throws IOException {
        elements = new DataElementArray();
        int ofs = 0;
        
        // negative origin because element offset is relative to start of hive space,
        // while registry header is 0x1000 bytes before that
        int origin = -RegistryHeader.headerSize;
        
        // signature is 'regf'
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_ASCIISZ, 4, ofs, "Signature", false)).read(origin, buffer);
        
        // primary and secondary sequence numbers
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Seq1", false)).read(origin, buffer);
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Seq2", false)).read(origin, buffer);
        
        // last write timestamp
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_FILETIME, 0, ofs, "Timestamp", false)).read(origin, buffer);
        
        // major and minor version numbers
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Major", false)).read(origin, buffer);
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Minor", false)).read(origin, buffer);
        
        // registry type
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Type", false)).read(origin, buffer);
        
        // registry format
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Format", false)).read(origin, buffer);
        
        // offset to root key
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Offset to Root Key", true)).read(origin, buffer);
        ix_offsetToRootKey = elements.lastElement();        // save index of data element
        
        // length of hive bins
        ofs = elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Hive Length", false)).read(origin, buffer);
        
        // cluster factor
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, ofs, "Cluster", false)).read(origin, buffer);
        
        // filename
        elements.addElement(DataElement.makeDataElement(ElementType.E_UNICODESZ, 64, 0x30, "Filename", false)).read(origin, buffer);
        
        // checksum
        elements.addElement(DataElement.makeDataElement(ElementType.E_INT, 0, 0x1fc, "Checksum", false)).read(origin, buffer);
        
        // validate registry signature
        return ((elements.getElementByName("Signature").toString().equals("regf")));
    }

    public void dump() {
        System.out.println(toString());
    }
    
    /**
     * Get root key offset
     * @return 
     */
    public int getRootKeyOffset() {
        return(((IntDataElement) elements.get(ix_offsetToRootKey)).getValueI());
    }
    
    @Override
    public String toString() {
        return("Header");
    }
    
    public String getPath() {
        return(toString());
    }
    
    /**
     * Write contents of element array back to registry buffer
     * @param buffer
     * @throws IOException 
     */
    public void update(RegistryBuffer buffer) throws IOException {
        buffer.position(0);
        for (DataElement e : elements) {
            e.write(0, buffer);
        }
    }    
}
