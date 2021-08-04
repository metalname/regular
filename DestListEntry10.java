package jumplist;

import java.nio.ByteBuffer;
import utils.NumFormat;
import utils.WindowsDate;

/**
 *
 * Structure of DestListEntry in jumplist CBF This is for Windows 10+
 * 
 * Offset       Size    Usage
 * 0            8       Checksum
 * 8            16      New Volume ID
 * 24           16      New Object ID
 * 40           16      Birth Volume ID
 * 56           16      Birth Object ID
 * 72           16      NetBIOS name (zero-padded)
 * 88           8       Entry ID Number
 * 96           4       Appears to be unused
 * 100          8       Last Access Time (MSFILETIME)
 * 108          4       Entry Pin Status
 * 112          4       Appaers to be unused
 * 116          4       Access count
 * 120          8       Appears to be unused
 * 128          2       Length of Unicode string data
 * 130          var     Unicode string data followed by four zero bytes
 */
public class DestListEntry10 extends DestListEntry {

    private long checksum;
    private final byte[] volumeID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] fileID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] birthVolumeID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] birthFileID = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private String netbios;
    private int entryNum;
    private final byte[] unused1 = {0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] unused2 = {0, 0, 0, 0};
    private final byte[] unused3 = {0, 0, 0, 0, 0, 0, 0, 0};
    private final byte[] trailer = {0, 0, 0, 0};
    private long modTime;
    private int pinStatus;
    private int accessCount;
    private short pathSize;
    private String path;

    public DestListEntry10(int offset) {
        super(offset);
    }

    @Override
    public void load(ByteBuffer destList) {
        destList.position(offset);
        checksum = destList.getLong();
        destList.get(volumeID);
        destList.get(fileID);
        destList.get(birthVolumeID);
        destList.get(birthFileID);
        netbios = readNetbios(destList);
        entryNum = destList.getInt();
        destList.get(unused1);  
        modTime = destList.getLong();
        pinStatus = destList.getInt();
        destList.get(unused2);
        accessCount = destList.getInt();
        destList.get(unused3);
        pathSize = destList.getShort();
        path = readPath(destList);
        destList.get(trailer);
        size = destList.position() - offset;
    }

    @Override
    public void write(ByteBuffer destList) {
        destList.position(offset);
        destList.putLong(checksum);
        destList.put(volumeID);
        destList.put(fileID);
        destList.put(birthVolumeID);
        destList.put(birthFileID);
        writeNetBios(destList, netbios);
        destList.putInt(entryNum);
        destList.put(unused1);  
        destList.putLong(modTime);
        destList.putInt(pinStatus);
        destList.put(unused2);
        destList.putInt(accessCount);
        destList.putShort(pathSize);
        writePath(destList);
        destList.put(trailer);
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
    protected String readNetbios(ByteBuffer destList) {
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
    protected void writeNetBios(ByteBuffer destList, String netbios) {
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
    protected String readPath(ByteBuffer destList) {
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
    protected void writePath(ByteBuffer destList) {
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
