package Forms.System.Accounts;

import Misc.SID;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import registry.Nodes.KeyNode;
import registry.RegistryException;
import utils.NumFormat;

/**
 *
 * Record for a user account
 */
public class UserRecord {

    private final int rid;
    private final String name;
    private byte[] F;
    private byte[] V;
    private ByteBuffer VBuf;
    private SID sid;
    private String profilePath;
    private boolean hasSAMRecord;       // indicates that this user has a record in SAM
    private boolean hasSOFTWARERecord;  // indicates that this user has a record in SOFTWARE
    private KeyNode userNode;           // SAM User key for this user
    private KeyNode nameNode;           // SAM Name key for this user
    private KeyNode profileNode;        // SOFTWARE key for this user

    public UserRecord(int RID, String name) {
        this.rid = RID;
        this.name = name;
    }

    public String name() {
        return (name);
    }

    public int rid() {
        return (rid);
    }
    
    public SID sid() {
        return(sid);
    }
    
    public void sid(SID sid) {
        this.sid = sid;
    }
    
    public void setF(byte[] F) {
        this.F = F;
    }
    
    public void profilePath(String path) {
        profilePath = path;
    }
    
    public String profilePath() {
        return(profilePath);
    }
    
    public void setV(byte[] V) {
        this.V = V;
        VBuf = ByteBuffer.wrap(V);
        VBuf.order(ByteOrder.LITTLE_ENDIAN);
        buildVHeader();
        buildSID();
    }
        
    public void hasSAMRecord(boolean flag) {
        hasSAMRecord = flag;
    }
    
    public boolean hasSAMRecord() {
        return(hasSAMRecord);
    }
    
    public void hasSOFTWARERecord(boolean flag) {
        hasSOFTWARERecord = flag;
    }
    
    public boolean hasSOFTWARERecord() {
        return(hasSOFTWARERecord);
    }
    
    public void userNode(KeyNode node) {
        userNode = node;
        hasSAMRecord(true);
    }
    
    public void nameNode(KeyNode node) {
        nameNode = node;
        hasSAMRecord(true);
    }
    
    public void profileNode(KeyNode node) {
        profileNode = node;
        hasSOFTWARERecord(true);
    }
    
    public void deletekeys() throws RegistryException {
        if (nameNode != null) {
            nameNode.delete(true);
        }
        if (userNode != null) {
            userNode.delete(true);
        }
        if (profileNode != null) {
            profileNode.delete(true);
        }
    }
    
    /* 
    ** Structure of V record:
    ** First 12 bytes are unknown - ignored
    ** Next 44 bytes are pointers to strings located further in the record
    ** There are 16 string pointers - each pointer has three integer elements:
    ** element {
    **   int offset;
    **   int length;
    **   int unknown;
    ** }
    **
    ** Offsets are relative to the end of the header, i.e. offset 0xCC
    */
    private class VHeaderElement {
        public int offset;
        public int length;
        public VHeaderElement(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
    }
        
    private static final int headerStart = 0xC;
    private static final int headerElements = 16;
    private static final int dataStart = 0xCC;
    private static final int permissionStart = 0x114;
    
    private final VHeaderElement[] headerElementArray = new VHeaderElement[headerElements];
    
    public enum ElementUsage  
        {E_UserName, E_FullName, E_Comment, E_UserComment, E_Unknown1,
        E_Homedir, E_HomedirConnect, E_Scriptpath, E_ProfilePath,
        E_Workstations, E_HoursAllowed, E_Unknown2, E_LMPasswordHash,
        E_NTPasswordHash, E_Unknown3, E_Unknown4; }
        
    private void buildVHeader() {
        VBuf.position(headerStart);
        for (int i = 0; i < headerElements; i++) {
            int offset = VBuf.getInt();
            int length = VBuf.getInt();
            VBuf.getInt();
            headerElementArray[i] = new VHeaderElement(offset, length);
        }
    }
    
    public String fullName() {
        if (VBuf != null) {
            return(getVElementAsString(ElementUsage.E_FullName));
        } else {
            return("");
        }
    }
    
    private byte[] getVElementAsByte(ElementUsage usage) {
        int index = usage.ordinal();
        int length = headerElementArray[index].length;
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; i++) {
            buffer[i] = VBuf.get(i + headerElementArray[index].offset + dataStart);
        }
        return(buffer);
    }
    
    private String getVElementAsString(ElementUsage usage) {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = getVElementAsByte(usage);
        // Read UNICODE characters from buffer and build string
        byte lsb = 0, msb = 0;
        for (int i = 0; i < buffer.length; i += 2) {
            lsb = buffer[i];
            if (i < buffer.length - 1) {
                msb = buffer[i+1];
            }
            char c = (char) (lsb + (msb * 0x100));
            sb.append(c);
        }
        return(sb.toString());
    }    
    
    public void dumpV() {
        int i = 0;
        for (VHeaderElement e: headerElementArray) {
            System.out.print(ElementUsage.values()[i] + ": offset=" + 
                               NumFormat.numToHex(e.offset) + ", length = " + e.length);
            System.out.println(" " + getVElementAsString(ElementUsage.values()[i]));
            i++;
        }
    }
    
    // read the permission block of the V key to get SID
    // block starts at 0ffset 0x114 (for Win 7 - may be different for other versions of Windows?)
    // structure:
    // offset   length value
    // 00       04     number of permission blocks
    //
    // permission block structure:
    // offset   length value
    // 00       02     unknown (always 0?)
    // 02       02     block length (includes first 4 bytes)
    // 04       04     permission list (5b030200 = Everyone, 44000200 = User, ff070f = Admin
    // 08       end    permission data
    //
    // SID will be in the user block (44000200)
    public void buildSID() {
        byte[] permList = new byte[4];
        VBuf.position(permissionStart);
        int numBlocks = VBuf.getInt(); // read number of blocks
        int marker = 0;
        for (int i = 0; i < numBlocks; i++) {
            marker = VBuf.position();
            // read block length
            VBuf.getShort();
            int blockLen = VBuf.getShort();
            // read permission list
            VBuf.get(permList);
            // is this the user pemission block?
            if (isUserPermission(permList)) {
                // SID is going to be the remainder of the block                
                sid = SID.makeSID(VBuf);
            }
            VBuf.position(marker + blockLen);
        }
    }
    
    private boolean isUserPermission(byte[] buffer) {
        return ((buffer[0] == 0x44) && (buffer[1] == 0x00) && (buffer[2] == 0x02));
    }
        
}
