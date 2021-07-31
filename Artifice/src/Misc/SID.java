/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Misc;

import java.nio.ByteBuffer;

/**
 *
 * @author 
 */
public class SID {

    private final byte[] sid;
    private int blocks;
    private int SID_REVISION;
    private int SID_NT_AUTHORITY;
    private int SID_NT_NON_UNIQUE;
    long[] groups;

    public SID(byte[] buffer) {
        sid = buffer;
        parseSID();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // get version number
        sb.append("S-").append(SID_REVISION);
        sb.append("-").append(SID_NT_AUTHORITY);
        sb.append("-").append(SID_NT_NON_UNIQUE);
        for (int i = 0; i < groups.length; i++) {
            sb.append("-").append(groups[i]);
        }
        return (sb.toString());
    }

    private void parseSID() {
        SID_REVISION = sid[0];
        blocks = sid[1];
        for (int i = 2; i < 8; i++) {
            SID_NT_AUTHORITY = (SID_NT_AUTHORITY * 0x100) + sid[i];
        }
        SID_NT_NON_UNIQUE = (int) getIntLE(8);
        int j = 12;
        groups = new long[blocks - 1];
        for (int i = 0; i < blocks - 1; i++) {
            groups[i] = getIntLE(j);
            j += 4;
        }
    }

    private long getIntLE(int index) {
        long result = 0;
        for (int i = index + 3; i >= index; i--) {
            result = (result << 8) + (sid[i] & 0xff);
        }
        return (result);
    }
    
    public int blocks() {
        return(blocks);
    }
    
    public byte byteAt(int index) {
        if ((index > 0) && (index < sid.length)) {
            return(sid[index]);
        } else {
            return(-1);
        }
    }
    
    public int rid() {
        return((int) groups[groups.length - 1]);
    }
    
    public boolean equals(SID csid) {
        // check length
        if (blocks == csid.blocks()) {
            for (int i = 0; i < sid.length; i++) {
                if (byteAt(i) != csid.byteAt(i)) {
                    return(false);
                }
            }
            return(true);
        }
        return(false);
    }
    
    public static SID makeSID(byte[] buffer) {
        return(makeSID(ByteBuffer.wrap(buffer)));
    }

    public static SID makeSID(ByteBuffer buffer) {
        // get revision byte
        byte revision = buffer.get();
        // get block count
        byte blocks = buffer.get();
        byte[] sid = new byte[8 + (blocks * 4)];
        sid[0] = revision;
        sid[1] = blocks;
        if ((sid[1] > 0) && (sid[1] < 10)) {
            // next six bytes are NT authority (1st block)
            for (int i = 0; i < 6; i++) {
                sid[i + 2] = buffer.get();
            }
            // get remainder of blocks
            for (int i = 0; i < sid[1]; i++) {
                for (int j = 0; j < 4; j++) {
                    sid[8 + (i * 4) + j] = buffer.get();
                }
            }
            return (new SID(sid));
        } else {
            return (null);
        }
    }

}
