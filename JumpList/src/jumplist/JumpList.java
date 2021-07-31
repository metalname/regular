package jumplist;

import CBF.CBF;
import CBF.file.FileBuffer;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.BufferedRandomAccessFile;
import utils.NumFormat;

/**
 *
 * Container object for a jump list file
 * 
 * https://cyberforensicator.com/wp-content/uploads/2017/01/1-s2.0-S1742287616300202-main.2-14.pdf
 */
public class JumpList {

    private final CBF cbf;      // jump list file are Compund Binary Files
    private DestList destList;  // DestList stream of emebedded CBF contains MRU and MFU lists and access times
    //private final ArrayList<ShlLink> shlLinks = new ArrayList<>();

    /**
     * Constructor
     * 
     * @param filename
     * @throws IOException 
     */
    public JumpList(String filename) throws IOException {
        // create a CBF object for this jumplist
        cbf = new CBF(filename);
        // load data from CBF
        load();
    }

    /**
     * Getter for CBF
     * 
     * @return 
     */
    public CBF getCBF() {
        return (cbf);
    }

    private void load() throws IOException {
        // a CBF contains multiple streams
        // we want the stream named DestList
        FileBuffer dl = cbf.getStream("DestList");
        //saveBuffer(dl, "c:/temp/destlist.dat");
        //saveBuffer(cbf.getDirectory().getBuffer(), "c:/temp/directory.dat");
        //saveBuffer(cbf.getMiniStream(), "c:/temp/ministream.dat");
        //cbf.dumpFAT("c:/temp/FAT.txt", cbf.getFAT());
        //cbf.dumpFAT("c:/temp/miniFAT.txt", cbf.getMiniFAT());
        //System.out.println(cbf.getSID("DestList").dump());
        //System.exit(0);
        if (dl != null) {
            // create and populate DestList
            destList = new DestList(dl);
            destList.load();
            //for (DestListEntry e: destList) {
            //    System.out.println(e.dump());
            //}
            //loadShlLinks();
        }
    }

    /**
     * Saves the DestList to disk for debugging
     * 
     * @throws IOException 
     */
    public void save() throws IOException {
        destList.save();
        cbf.save();
    }

    /**
     * get entries from DestList
     * 
     * @return 
     */
    public List<DestListEntry> getEntries() {
        return (destList.getEntries());
    }

    /**
     * Load SHLLNK entries from DestList
     * @throws IOException 
     */
    protected void loadShlLinks() throws IOException {
        for (DestListEntry entry : destList) {
            String name = NumFormat.stripLeadingZeros(entry.getEntryNum());
            FileBuffer stream = cbf.getStream(name);
            if (stream != null) {
                //ShlLink shlLink = new ShlLink(name, stream);
                //shlLink.load(stream);
                //shlLinks.add(shlLink);
            } else {
                System.out.println("Entry " + name + " was not found");
            }
        }
    }

    protected String formatName(int i) {
        String s = NumFormat.numToHex(i);
        return (stripLeadingZeros(s));
    }

    protected String stripLeadingZeros(String s) {
        boolean start = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!start) {
                start = (s.charAt(i) != '0');
            }
            if (start) {
                sb.append(s.charAt(i));
            }
        }
        return (sb.toString());
    }

    public void saveBuffer(FileBuffer buffer, String filename) throws IOException {
        BufferedRandomAccessFile raf = new BufferedRandomAccessFile(filename, "rw");
        for (byte[] b : buffer) {
            raf.write(b);
        }
        raf.close();
    }

    public static void saveFile(String data, String filename) {
        try {
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void deleteEntry(String entryNum) throws IOException {
        // delete sequence:
        // delete entry from destList
        destList.deleteEntry(entryNum);
        // delete shllink entry
        cbf.deleteStream(entryNum);
    }

    public boolean changed() {
        return(cbf.changed() | destList.changed());
    }
    
    /*
     protected int findShlLink(String entryNum) {
     for (int i = 0; i < shlLinks.size(); i++) {
     if (shlLinks.get(i).getName().equals(entryNum)) {
     return (i);
     }
     }
     return (-1);
     }
     */
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            JumpList jumpList = new JumpList("c:/temp/2b7046ae8c430d91.automaticDestinations-ms");
            //jumpList.deleteEntry("1");
            //jumpList.save();
            int sector = 0;
            for (String s : jumpList.getCBF().buildSectorMap()) {
                System.out.println("Sector: " + NumFormat.numToHex(sector) + " " + s);
                sector++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(cbf.getSector(0).dump());
    }

}
