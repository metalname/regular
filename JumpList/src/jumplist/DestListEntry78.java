package jumplist;

import BinBuffer.BinBuffer;
import utils.NumFormat;
import utils.WindowsDate;

/**
 *
 * Structure of DestListEntry in jumplist CBF This is for Windows 7/8
 * 
 * Offset       Size    Usage
 * 0            8       Checksum
 * 8            16      New Volume ID
 * 24           16      New Object ID
 * 40           16      Birth Volume ID
 * 56           16      Birth Object ID
 * 72           16      NetBIOS name (zero-padded)
 * 88           8       Entry ID Number
 * 96           4       Counter (float)
 * 100          8       Last Access Time (MSFILETIME)
 * 108          4       Entry Pin Status
 * 112          2       Length of Unicode string data
 * 114          var     Unicode string data
 * 
 */
public class DestListEntry78 extends DestListEntry {

    private long checksum;
    private final byte[] volumeID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] fileID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] birthVolumeID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] birthFileID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String netbios;
    private int entryNum;
    private byte[] accessCount = {0, 0, 0, 0, 0, 0, 0, 0};
    private long modTime;
    private int pinStatus;
    private short pathSize;
    private String path;

    public DestListEntry78(int offset) {
        super(offset);
    }

    @Override
    public void load(BinBuffer destList) {
        destList.position(offset);
        checksum = destList.getLong();
        destList.get(volumeID);
        destList.get(fileID);
        destList.get(birthVolumeID);
        destList.get(birthFileID);
        netbios = readNetbios(destList);
        entryNum = destList.getInt();
        destList.get(accessCount);  
        modTime = destList.getLong();
        pinStatus = destList.getInt();
        pathSize = destList.getShort();
        path = readPath(destList);
        size = destList.position() - offset;
    }

    @Override
    public void write(BinBuffer destList) {
        destList.position(offset);
        destList.putLong(checksum);
        destList.put(volumeID);
        destList.put(fileID);
        destList.put(birthVolumeID);
        destList.put(birthFileID);
        writeNetBios(destList, netbios);
        destList.putInt(entryNum);
        destList.put(accessCount);  
        destList.putLong(modTime);
        destList.putInt(pinStatus);
        destList.putShort(pathSize);
        writePath(destList);
    }

    @Override
    public String getNetBios() {
        return (netbios);
    }

    @Override
    public String getPath() {
        return (path);
    }

    @Override
    public String getEntryNum() {
        return (NumFormat.numToHex(entryNum));
    }

    @Override
    public int getEntryNumI() {
        return (entryNum);
    }

    @Override
    public String getModDate() {
        return (new WindowsDate(modTime).toString());
    }

    @Override
    protected String readNetbios(BinBuffer destList) {
        int bufpos = destList.position();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            byte b = destList.get();
            if (b == 0) {
                break;
            } else {
                sb.append((char) b);
            }
        }
        destList.position(bufpos + 16);
        return (sb.toString());
    }

    @Override
    protected void writeNetBios(BinBuffer destList, String netbios) {
        int bufpos = destList.position();
        int i;
        for (i = 0; i < netbios.length(); i++) {
            destList.put((byte) netbios.charAt(i));
        }
        if (i < 16) {
            destList.put((byte) 0);
        }
        destList.position(bufpos + 16);
    }

    @Override
    protected String readPath(BinBuffer destList) {
        //int bufpos = destList.position();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pathSize; i++) {
            char c = destList.getChar();
            if (c == 0) {
                break;
            } else {
                sb.append(c);
            }
        }
        return (sb.toString());
    }

    @Override
    protected void writePath(BinBuffer destList) {
        for (int i = 0; i < pathSize; i++) {
            destList.putChar(path.charAt(i));
        }
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("Size: ").append(size).append("\n").
                append("Netbios: ").append(netbios).append("\n").append("Entry Number: ").
                append(entryNum).append("\n").
                append("Modification Time: ").append(getModDate()).append("\n").append("Pin Status: ").
                append(pinStatus).append("\n").
                append("Path: ").append(path).append("\n");
        return (sb.toString());
    }

    @Override
    public int size() {
        return (size);
    }

}
