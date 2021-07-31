/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.RecentDocs;

import Forms.TableRow;
import BinBuffer.BinBuffer;
import java.util.ArrayList;
import registry.Nodes.ValueNode;
import registry.RegistryException;

/**
 *
 * @author 
 */
public class RecentRecord {

    private ValueNode valueNode;
    private BinBuffer buffer;
    private String docName;
    private String lnkName;

    public RecentRecord(ValueNode valueNode) {
        this.valueNode = valueNode;
        buffer = new BinBuffer(valueNode.getData());
        unpack();
    }
    
    private void unpack() {
        buffer.position(0);
        docName = buffer.getUnicodeStringZ(buffer.size());
        buffer.skip(14);
        lnkName = buffer.getAsciiStringZ(buffer.remaining());
    }

    public void delete() throws RegistryException {
        valueNode.delete(true);
    }

    public int key() {
        return (Integer.parseInt(valueNode.name()));
    }

    public void decrementKey() throws RegistryException {
        valueNode.name("" + (key() - 1));
    }
    
    /*
    ** Structure of data buffer
    ** 00 - Document Name (UNICODE string, zero-terminated)
    **      14 bytes (unknown)
    **      LNK File name (ASCII string, zero-terminated)
    */
    public String getDocName() {
        return(docName);
    }
    
    public String getLnkFileName() {        
        return(lnkName);
    }
    
    public TableRow makeRow() {
        TableRow row = new TableRow();
        row.add(key());
        row.add(getDocName());
        row.add(getLnkFileName());
        return(row);
    }
    
    public ValueNode valueNode() {
        return(valueNode);
    }
    
    public static String[] columns = {"Key", "Doc Name", "LNK File Name"};

}
