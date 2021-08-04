package registry.Cell;

import registry.RegistryBuffer;
import registry.element.DataElementCollection;
import registry.element.DataElement;


/**
 *
 * Base (abstract) class for all registry cell types
 */
public abstract class RegistryCell {
    
    protected int offset;           // offset of this cell (relative to start of hive bins)
    protected int size;             // cell size
    protected String signature;     // cell signature
    
    public RegistryCell (int offset) {
        this.offset = offset;
    }
    
    public abstract RegistryCellType getCellType();
    public abstract boolean load(RegistryBuffer buffer);    
    public abstract int[] getChildIndexes();
    public abstract RegistryCell[] getChildCells(RegistryBuffer buffer);    
    public abstract DataElementCollection elements();
    public abstract void elements(DataElementCollection elements);
        
    public int getOffset() {
        return(offset);
    }
    
    public String dump() {
        final StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        elements().stream().forEach(e -> {
            sb.append(e.getLabel()).append(" = ").append(e.toString()).append("\n");
        });
        return(sb.toString());
    }
    
    /**
     * Get size of cell from buffer
     * 
     * @param buffer 
     */
    public void loadSize(RegistryBuffer buffer) {
        buffer.position(offset);
        size = buffer.getInt();
    }
    
    /** 
     * Get cell signature from buffer
     * 
     * @param buffer
     */
    public void loadSignature(RegistryBuffer buffer) {
        buffer.position(0x04);
        signature = "" + (char) buffer.getByte() + (char) buffer.getByte();
    }
    
    /**
     * Load cell header
     * 
     * @param buffer 
     */
    public void loadHeader(RegistryBuffer buffer) {
        loadSize(buffer);
        loadSignature(buffer);
    }

    /**
     * Factory method
     * Creates a cell by examining data at specified offset into buffer
     * 
     * @param offset
     * @param buffer
     * @return 
     */
    public static RegistryCell makeRegistryCell(int offset, RegistryBuffer buffer) {
        
        if (offset < 0) {
            return(null);
        }
        // read length and signature
        buffer.position(offset);
        int size = buffer.getInt();
        String signature = "" + (char) buffer.getByte() + (char) buffer.getByte();
        
        // create cell based on signature
        switch(signature) {
            case "nk":
                NkCell nkCell = new NkCell(offset);
                nkCell.load(buffer);
                return(nkCell);
            case "li":
                LiCell liCell = new LiCell(offset);
                liCell.load(buffer);
                return(liCell);
            case "lf":
                LfCell lfCell = new LfCell(offset);
                lfCell.load(buffer);
                return(lfCell);
            case "lh":
                LhCell lhCell = new LhCell(offset);
                lhCell.load(buffer);
                return(lhCell);
            case "ri":
                RiCell riCell = new RiCell(offset);
                riCell.load(buffer);
                return(riCell);
            case "vk":
                VkCell vkCell = new VkCell(offset);
                vkCell.load(buffer);
                return(vkCell);       
            case "sk":
                SkCell skCell = new SkCell(offset);
                skCell.load(buffer);
                return(skCell);                   
            default: 
                // data cell doesn't have a signature
                DataCell dataCell = new DataCell(offset);
                dataCell.load(buffer);
                return(dataCell);
        }
    }
    
    /**
     * Creates a new registry cell of specified type at specified offset in buffer
     * 
     * @param offset
     * @param type
     * @param buffer
     * @return 
     */
    public static RegistryCell newRegistryCell(int offset, RegistryCellType type, RegistryBuffer buffer) {
        zero(offset, buffer);        
        buffer.position(offset);
        int sz = buffer.getInt();
        // rewrite size as negative
        sz = -Math.abs(sz);
        buffer.position(offset);
        buffer.putInt(sz);
        // write signature
        switch (type) {
            case T_VK:
                buffer.putByte((byte) 'v');
                buffer.putByte((byte) 'k');
                VkCell vkCell = new VkCell(offset);
                vkCell.loadHeader(buffer);
                return(vkCell);
            case T_NK:
                buffer.putByte((byte) 'n');
                buffer.putByte((byte) 'k');
                NkCell nkCell = new NkCell(offset);
                nkCell.loadHeader(buffer);
                return(nkCell);                
            default:
                System.out.println("Unhandled type " + type + " in newregistryCell");
                System.exit(-1);
        }
        return(null);
    }
    
    /**
     * Create a data cell from registry buffer at specified offset
     * 
     * @param offset
     * @param buffer
     * @return 
     */
    public static DataCell makeDataCell(int offset, RegistryBuffer buffer) {
        if (offset < 0) {
            return(null);
        }
        DataCell dataCell = new DataCell(offset);
        dataCell.load(buffer);
        return(dataCell);
    }
    
    /**
     * Create a value list cell from registry buffer at specified offset
     * 
     * @param offset
     * @param numValues
     * @param buffer
     * @return 
     */
    public static ValueListCell makeValueListCell(int offset, int numValues, RegistryBuffer buffer) {
        if (offset < 0) {
            return(null);
        }
        ValueListCell valueListCell = new ValueListCell(offset, numValues);
        valueListCell.load(buffer);
        return(valueListCell);
    }
    
    /**
     * Create a class cell from registry buffer at specified offset
     * 
     * @param offset
     * @param buffer
     * @return 
     */
    public static ClassCell makeClassCell(int offset, RegistryBuffer buffer) {
        if (offset < 0) {
            return(null);
        }
        ClassCell classCell = new ClassCell(offset);
        classCell.load(buffer);
        return(classCell);
    }    
        
    /**
     * Getter for size
     * @return 
     */
    public int size() {
        return(size);
    }
    
    /**
     * Get absolute size
     * Active cells will have a negative number for the size
     * Deleted cells will be positive
     * 
     * @return 
     */
    public int absSize() {
        return(size < 0 ? -size : size);
    }
    
    /**
     * Set cell size
     * 
     * @param size 
     */
    public void size(int size) {
        this.size = size;
    }
    
    /**
     * Get length of cell less header
     * 
     * @return 
     */
    public int length() {
        return(Math.abs(size) - 4);
    }
    
    /**
     * Write attribute elements back to cell
     * 
     * @param buffer 
     */
    public void update(RegistryBuffer buffer) {
        for (DataElement element: elements()) {
            element.write(offset, buffer);
        }
    }

    /**
     * Delete cell
     * This stub entry just rewrites the cell size - descendant classes will delete the rest of the cell
     * @param buffer 
     */
    public void delete(RegistryBuffer buffer) {
        buffer.position(offset);
        size = Math.abs(size);
        buffer.putInt(size);
    }
    
    /**
     * Wipe cell data
     * 
     * @param buffer
     * @return 
     */
    public RegistryCell wipe(RegistryBuffer buffer) {
        // zero out cell data
        zero(offset, buffer);
        
        // change size to positive value
        size = Math.abs(size);
        buffer.position(offset);
        buffer.putInt(size);
        
        //recreate as data cell (without signature)
        return(makeDataCell(offset, buffer));
    }
    
    /**
     * Overwrites cell data with zeroes
     * 
     * @param offset
     * @param buffer 
     */
    public static void zero(int offset, RegistryBuffer buffer) {
        buffer.position(offset);
        int size = Math.abs(buffer.getInt());
        for (int i = 0; i < size - 4; i++) {
            buffer.putByte((byte) 0);
        }
    }
    
    /**
     * Get deleted flag
     * 
     * @return 
     */
    public boolean isDeleted() {
        return(size > 0);
    }    
}
