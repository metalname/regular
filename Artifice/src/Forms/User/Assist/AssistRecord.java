/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.Assist;

import BinBuffer.BinBuffer;
import registry.Nodes.ValueNode;
import utils.WindowsDate;

/**
 *
 * @author 
 */
public class AssistRecord {

    private String name;
    private String guid;
    private BinBuffer data;
    private boolean isUEMESession;
    private ValueNode valueNode;

    public AssistRecord(String name, String guid, ValueNode valueNode) {
        this.name = decryptName(name);
        isUEMESession = this.name.equals("UEME_CTLSESSION");
        this.guid = guid;
        this.valueNode = valueNode;
        this.data = new BinBuffer(valueNode.getData());
    }

    private String decryptName(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            sb.append(ROT13(s.charAt(i)));
        }
        return (sb.toString());
    }

    public String name() {
        return (name);
    }

    public boolean isUEMESession() {
        return (isUEMESession);
    }

    private char ROT13(char c) {
        if ((c >= 'a') && (c <= 'z')) {
            int i = c - 'a' + 13;
            if (i >= 26) {
                i -= 26;
            }
            return ((char) (i + 'a'));
        } else if ((c >= 'A') && (c <= 'Z')) {
            int i = c - 'A' + 13;
            if (i >= 26) {
                i -= 26;
            }
            return ((char) (i + 'A'));
        } else {
            return (c);
        }
    }
    
    public ValueNode valueNode() {
        return(valueNode);
    }

    private static final int offsetCount = 0x04;
    private static final int offsetTimestamp = 0x3c;

    public int count() {
        data.position(offsetCount);
        return (data.getInt());
    }

    public long timestamp() {
        if (isUEMESession()) {
            return (0);
        } else {
            data.position(offsetTimestamp);
            return (data.getLong());
        }
    }
    
    public void timestamp(long timestamp) {
        if (!isUEMESession) {
            data.position(offsetTimestamp);
            data.putLong(timestamp);
            valueNode.setData(data.data());
        }
    }
    
    public void timestamp(WindowsDate dt) {
        timestamp(dt.timestamp());
    }

}
