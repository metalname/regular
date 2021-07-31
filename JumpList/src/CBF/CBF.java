package CBF;

import CBF.directory.*;
import CBF.fat.*;
import CBF.file.*;
import CBF.sector.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.BufferedRandomAccessFile;
import utils.NumFormat;

/**
 *
 * @author  
 * 
 * Compound Binary File, also known as OLE
 */
public class CBF {

    private HeaderSector headerSector;
    private final byte[] headerBuffer = new byte[DataSector.secSize];
    private FileSystem normalFS;
    private FileSystem miniFS;
    private FileBuffer miniStream;

    private Directory directory;

    private final ArrayList<DataSector> sectors = new ArrayList<>();
    private final String filename;
    private boolean changed = false;

    public CBF(String filename) throws IOException {
        this.filename = filename;
        load();
    }

    private void load() throws IOException {
        readDataSectors();
        loadHeader();
        loadFAT();
        loadDirectory();
        loadMiniFAT();
        loadMiniStream();
    }
    
    public void save() throws IOException {
        directory.save();
        saveDataSectors();
    }

    private void readDataSectors() throws IOException {
        BufferedRandomAccessFile raf = new BufferedRandomAccessFile(filename, "r");
        int count = 1;
        raf.read(headerBuffer);
        while (!raf.eof()) {
            DataSector ds = new DataSector(count++);
            ds.read(raf);
            sectors.add(ds);
        }
        raf.close();
    }
    
    private void saveDataSectors() throws IOException {
        BufferedRandomAccessFile raf = new BufferedRandomAccessFile(filename, "rw");
        raf.write(headerBuffer);
        for (DataSector ds: sectors) {
            raf.write(ds.getData());
        }
        raf.close();
    }

    private void loadHeader() {
        headerSector = new HeaderSector(headerBuffer);
        headerSector.load();
        //System.out.println(headerSector.dump());
    }

    // FAT sectors are controlled by header
    // header specifies number of FAT sectors
    // FAT sectors are specified by header DIFAT entries    
    private void loadFAT() {
        DataSector[] ds = new DataSector[headerSector.getCountFATSectors()];
        for (int i = 0; i < headerSector.getCountFATSectors(); i++) {
            int sect = headerSector.getFATSector(i + 1);
            ds[i] = sectors.get(sect);
        }
        normalFS = new FileSystem(new NormalFAT(ds), new NormalSectorArray(sectors));
    }

    // Starting sector for directory specified by header
    // directory sectors are chained in the FAT
    private void loadDirectory() throws IOException {
        final FileBuffer fb = getBufferForSector(headerSector.getDirectoryStartSector(), normalFS, null);
        directory = new Directory(fb);
    }

    // for give sector index, return a buffer containg all sectors chained in FAT
    protected FileBuffer getBufferForSector(int sector, FileSystem fs, DirectoryEntry sid) throws IOException {
        final FileBuffer fb = new FileBuffer(fs, sid);
        int sect = sector;
        do {
            fb.addSector(sect);
            sect = fs.getFATEntry(sect);
        } while ((sect >= 0) && (sect < fs.getNumFATEntries()));        
        return (fb);
    }
    
    protected FileBuffer getBufferForSector(DirectoryEntry sid, FileSystem fs) throws IOException {
        final FileBuffer fb = getBufferForSector(sid.getStartSect(), fs, sid);
        return(fb);
    }

    // Starting sector for mini FAT specified by header
    // miniFAT sectors are chained in FAT
    protected MiniFAT loadMiniFAT() throws IOException {
        return(new MiniFAT(getBufferForSector(headerSector.getMiniFATStartSector(), normalFS, null)));
    }

    // starting sector for ministream is in directory entry 'Root Entry'
    // ministream sectors are chained in FAT
    protected void loadMiniStream() throws IOException {
        // root entry should always be at SID 0
        DirectoryEntry dir = directory.get(0);
        if (dir != null) {
            miniStream = getBufferForSector(dir.getStartSect(), normalFS, dir);
            miniFS = new FileSystem(loadMiniFAT(), new MiniSectorArray(miniStream));
        } else {
            System.out.println("Could not find directory 'Root Entry'");
            System.exit(-1);
        }
    }

    // search directory array for given name
    // returns a byte buffer containign concatenated sectors
    // data will either come from the main sector array
    // or the miniStream depending on the size of the directory entry
    public FileBuffer getStream(String name) throws IOException {
        DirectoryEntry dir = directory.getEntry(name);
        if (dir == null) {
            return (null);
        }
        if (dir.getSize() < headerSector.getMiniSectorCutoff()) {
            // directory size is below cutoff - get data from the miniStream
            return (getBufferForSector(dir, miniFS));
        } else {
            // directory size is above cutoff - get data from the main sector array
            return (getBufferForSector(dir, normalFS));
        }
    }
    
    public FileBuffer getMiniStream() {
        return(miniStream);
    }
    
    public void deleteStream(String name) throws IOException {
        FileBuffer fb = getStream(NumFormat.stripLeadingZeros(name));
        if (fb != null) {
            fb.wipe();
            fb.delete();
            directory.delete(name);
            changed = true;
        }
    }
    
    public Directory getDirectory() {
        return(directory);
    }
    
    // builds a map of all sectpors showing which structures they are assigned to
    public List<String> buildSectorMap() {
        List<String> map = new ArrayList<>();
        for (int i = 0; i < sectors.size(); i++) {
            map.add("Unallocated");
        }
        map.set(0, "Header");
        //set FAT sectors
        int count = 0;
        for (DataSector ds: ((NormalFAT) normalFS.getFAT()).getSectorList()) {
            map.set(ds.getSecNum(), "FAT Sector " + count);
            count++;
        }
        count = 0;
        for (Integer i: directory.getBuffer().getSectorList()) {
            map.set(i + 1, "Directory Sector " + count);
            count++;
        }
        count = 0;
        for (Integer i: ((MiniFAT) miniFS.getFAT()).getBuffer().getSectorList()) {
            map.set(i + 1, "Mini FAT Sector " + count);
            count++;            
        }
        count = 0;
        for (Integer i: miniStream.getSectorList()) {
            map.set(i + 1, "Mini Stream Sector " + count);
            count++;                        
        }
        return(map);
    }
    
    public boolean changed() {
        return(changed);
    }
}
