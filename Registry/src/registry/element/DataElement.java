package registry.element;

import registry.RegistryBuffer;

/**
 *
 * Defines an element of specified type at specified offset into RegistryBuffer
 * This is the base (abstract) class
 * Concrete element types will descend from here
 */
public abstract class DataElement {
    
    // the type of the element
    protected ElementType type;
    // data length
    protected int length;
    // offset of this element 
    protected int offset;
    // label of this element
    protected String label;
    // indicates that this element holds an index that points to another cell
    protected boolean follow = false;
    //protected CellIndex cellIndex;
    
    //public DataElement() {
        
    //}
    
    /**
     * Constructor
     * @param type element type
     * @param length element length (for strings and byte arrays)
     * @param offset offset into RegistryBuffer
     * @param label element label
     * @param follow flag that specifies this element's value points to another element
     */
    public DataElement(final ElementType type, 
                       final int length, 
                       final int offset, 
                       final String label, 
                       final boolean follow) {
        //this();
        this.type = type;
        this.length = length;
        this.offset = offset;
        this.label = label;
        this.follow = follow;
    }
    
    /**
     * getter for type
     * @return 
     */
    public ElementType getType() {
        return(type);
    }
    
    /**
     * Reads this elements value from the buffer
     * Returns the offset of where the next element will be - allows chaining
     * @param origin offset to apply to read
     * @param buffer
     * @return 
     */
    public abstract int read(int origin, final RegistryBuffer buffer);
    
    /**
     * Writes element value back to buffer
     * @param origin offset to apply to buffer
     * @param buffer
    **/
    public abstract void write(int origin, final RegistryBuffer buffer);
    
    /**
     * Getter for label
     * @return 
     */
    public String getLabel() {
        return(label);
    }
    
    @Override
    public abstract String toString();
    
    /**
     * Factory method to create a new data element using the supplied values
     * @param type
     * @param length
     * @param offset
     * @param label
     * @param follow
     * @return 
     */
    public static DataElement makeDataElement(final ElementType type, 
                                              final int length, 
                                              final int offset, 
                                              final String label, 
                                              final boolean follow) {
        switch (type) {
            case E_INT:
                return(new IntDataElement(type, length, offset, label, follow));
            case E_SHORT:
                return(new ShortDataElement(type, length, offset, label, follow));
            case E_LONG:
                return(new LongDataElement(type, length, offset, label, follow));
            case E_ASCIISZ: 
                return(new AStrZDataElement(type, length, offset, label, follow));
            case E_UNICODESZ:
                return(new UStrZDataElement(type, length, offset, label, follow));
            case E_ASCIISL: 
                return(new AStrLDataElement(type, length, offset, label, follow));
            case E_UNICODESL:
                return(new UStrLDataElement(type, length, offset, label, follow));                
            case E_FILETIME:
                return(new FiletimeDataElement(type, length, offset, label, follow));
            case E_BINARY:
                return(new BinDataElement(type, length, offset, label, follow));
            case E_NKFLAGS:
                return(new NkFlagsDataElement(type, length, offset, label, follow));
            default:
                throw new RuntimeException("Element type " + type + " not handled in DataElement.makeDataElement");
        }
    }
    
    /** 
     * Getter for follow flag
     * 
     * @return 
     */
    public boolean isFollow() {
        return(follow);
    }
    
    /**
     * Setter for follow flag
     * 
     * @param follow 
     */
    public void setFollow(final boolean follow) {
        this.follow = follow;
    }
    
    /**
     * Setter for label
     * @param label 
     */
    public void setLabel(final String label) {
        this.label = label;
    }    
    
    /**
     * Sets data from supplied string
     * Returns false is string is invalid
     * 
     * @param data
     * @return 
     */
    public abstract boolean setData(final String data);
    
    /**
     * Getter for offset
     * @return 
     */
    public int getOffset() {
        return(offset);
    }
    
    /**
     * Setter for offset
     * @param offset 
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    /**
     * Get size
     * @return 
     */
    public abstract int getSize();
}
