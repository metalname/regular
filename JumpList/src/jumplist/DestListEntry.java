package jumplist;

import BinBuffer.BinBuffer;
import utils.NumFormat;
import utils.WindowsDate;

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

    public abstract void load(BinBuffer destList);
    
    public abstract void write(BinBuffer destList);

    public abstract String getNetBios();    
    
    public abstract String getPath();
    
    public abstract String getEntryNum();

    public abstract int getEntryNumI();

    public abstract String getModDate();

    protected abstract String readNetbios(BinBuffer destList);

    protected abstract void writeNetBios(BinBuffer destList, String netbios);
    
    protected abstract String readPath(BinBuffer destList);
    
    protected abstract void writePath(BinBuffer destList);
    
    public abstract void setOffset(int offset);

    public abstract String dump();

    public abstract int size();
    
}
