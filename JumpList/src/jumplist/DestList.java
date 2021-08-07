package jumplist;

import CBF.CBF;
import CBF.CBFException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static utils.NumFormat.numToHex;

/**
 *
 * Destlist structure contains a list of entries ordered by last modification
 * timestamp
 *
 * Structure:
 *
 * 00 - 20 Header 21 - End Of Stream MRU Entries (variable length - see
 * DestListEntry class)
 *
 * Header Structure 00 - 03 INT4 Version 04 - 07 INT4 Number of Entries 08 - 0b
 * INT4 Number of Pinned Entries
 */
public class DestList implements Iterator<DestListEntry>, Iterable<DestListEntry> {

    private final ByteBuffer buffer;
    private final List<DestListEntry> entries = new ArrayList<>();
    private int lastIndex = 0;
    private static final int headerSize = 0x20;

    private int version;
    private int numEntries;
    private int numPinned;
    private int size;
    private boolean changed = false;

    public DestList(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void load() {
        loadHeader();
        readEntries();
    }

    private void loadHeader() {
        buffer.position(0);
        version = buffer.getInt();
        numEntries = buffer.getInt();
        numPinned = buffer.getInt();
    }

    private void writeHeader() {
        buffer.position(0);
        buffer.putInt(version);
        buffer.putInt(entries.size());
        buffer.putInt(numPinned);
    }

    //private void saveBuffer() {
    //    file.truncate();
    //    file.put(buffer.buffer(), size);
    //}

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("Version: " + version).append("\n");
        sb.append("Number of Entries: " + numEntries).append("\n");
        sb.append("Pinned Entries: " + numPinned).append("\n");
        return (sb.toString());
    }

    protected void readEntries() {
        int offset = headerSize;
        int i;
        for (i = 0; i < numEntries && buffer.hasRemaining(); i++) {
            // check version number in header to determine which type of DestListEntry to create
            DestListEntry entry;
            switch (version) {
                case 1:
                    entry = new DestListEntry78(offset);
                    break;
                case 3:
                case 4:
                    entry = new DestListEntry10(offset);
                    break;
                default:
                    System.out.println("Warning: skipping DestList @" + numToHex(offset) + " - invalid version number");
                    entry = null;
            }
            if (entry != null) {
                entry.load(buffer);
                entries.add(entry);
                offset += entry.size();
            }            
        }
        size = offset;
    }

    protected void writeEntries() {
        int offset = headerSize;
        for (DestListEntry entry : entries) {
            entry.setOffset(offset);
            entry.write(buffer);
            offset += entry.size();
        }
        size = offset;
        // wipe remainder of buffer
        //buffer.position(offset);
        //for (int i = offset; i < buffer.size(); i++) {
        //    buffer.put((byte) 0);
        //}
    }

    public int getNumEntries() {
        return (numEntries);
    }

    public List<DestListEntry> getEntries() {
        return (entries);
    }

    // delete entry from the DestList and move all others up
    public void deleteEntry(String entryNum) {
        int num;
        try {
            num = Integer.parseInt(entryNum, 16);
        } catch (NumberFormatException e) {
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getEntryNumI() == num) {
                entries.remove(i);
                changed = true;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return (lastIndex < entries.size());
    }

    @Override
    public DestListEntry next() {
        return (entries.get(lastIndex++));
    }

    @Override
    public Iterator<DestListEntry> iterator() {
        lastIndex = 0;
        return (this);
    }

    public void save() {
        //writeHeader();
        //writeEntries();
        //saveBuffer();
        //file.close();
    }

    public boolean changed() {
        return (changed);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //var cbf = new CBF("/home/metataro/7e4dca80246863e3.automaticDestinations-ms");
            //var cbf = new CBF("/home/metataro/5f7b5f1e01b83767.automaticDestinations-ms");
            //var cbf = new CBF("/home/metataro/f01b4d95cf55d32a.automaticDestinations-ms");
            var cbf = new CBF("/home/metataro/a52b0784bd667468.automaticDestinations-ms");
            var buffer = cbf.getStream("DestList");
            if (buffer != null) {
                var destList = new DestList(buffer);
                destList.load();
            }
        } catch (CBFException e) {
            System.out.println(e);
        }
    }    
}
