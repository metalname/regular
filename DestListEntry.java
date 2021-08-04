package jumplist;

import java.nio.ByteBuffer;

/**
 *
 * @author 
 */
public abstract class DestListEntry {

    protected int offset;
    protected int size;
    
    /**
    private long checksum;
    private final byte[] volumeID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] fileID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] birthVolumeID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] birthFileID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String netbios;
    private int entryNum;
    private byte[] unused = {0, 0, 0, 0, 0, 0, 0, 0};
    private long modTime;
    private int pinStatus;
    private short pathSize;
    private String path;
    */
    

    public DestListEntry() {
        
    }
    /**
     * Constructor
     * @param offset 
     */
    public DestListEntry(int offset) {
        this();
        this.offset = offset;
    }

    public abstract void load(ByteBuffer destList);
    
    public abstract void write(ByteBuffer destList);

    public abstract String getNetBios();    
    
    public abstract String getPath();
    
    public abstract String getEntryNum();

    public abstract int getEntryNumI();

    public abstract String getModDate();

    protected abstract String readNetbios(ByteBuffer destList);

    protected abstract void writeNetBios(ByteBuffer destList, String netbios);
    
    protected abstract String readPath(ByteBuffer destList);
    
    protected abstract void writePath(ByteBuffer destList);
    
    public abstract void setOffset(int offset);

    public abstract String dump();

    public abstract int size();
    
}
