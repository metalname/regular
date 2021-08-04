/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.RecentDocs;

import BinBuffer.BinBuffer;
import Misc.StringEx;
import java.util.ArrayList;
import registry.Nodes.KeyNode;
import registry.Nodes.ValueNode;
import registry.RegistryException;

/**
 *
 * @author 
 */
public class GroupRecord {

    private final KeyNode keyNode;
    private ValueNode valueMRUListEx;
    private ArrayList<RecentRecord> records;
    private boolean changed = false;

    public GroupRecord(KeyNode keyNode) {
        this.keyNode = keyNode;
    }

    @Override
    public String toString() {
        return (keyNode.toString());
    }

    public KeyNode keyNode() {
        return (keyNode);
    }

    public ArrayList<RecentRecord> records() throws RegistryException {
        if (records == null) {
            buildRecords();
        }
        return (records);
    }

    private void buildRecords() throws RegistryException {
        records = new ArrayList<>();
        for (ValueNode vn : keyNode.values()) {
            if (StringEx.isInt(vn.name())) {
                RecentRecord rr = new RecentRecord(vn);
                records.add(rr);
            } else if ("MRUListEx".equals(vn.name())) {
                valueMRUListEx = vn;
            }
        }
    }

    public void deleteKey(int key) throws RegistryException {
        int row = getIndexForKey(key);
        if (row >= 0) {
            // delete current value
            records.get(row).delete();
            records.remove(row);
            // loop through remaining records
            // if the key is larger than this key, decrement it
            for (RecentRecord rr : records) {
                if (rr.key() > key) {
                    rr.decrementKey();
                }
            }
            // recalculate MRU list
            recalculateMRU();
            changed = true;
        }
    }

    private int getIndexForKey(int key) {
        for (int i = 0; i < records.size(); i++) {
            if (key == records.get(i).key()) {
                return (i);
            }
        }
        return (-1);
    }

    public boolean changed() {
        return (changed);
    }

    // recalculate MRU list 
    public void recalculateMRU() throws RegistryException {
        BinBuffer buffer = new BinBuffer(new byte[(records.size() + 1) * 4]);
        buffer.position(0);
        // write MRU entries as ints in reverse order
        for (int i = records.size() - 1; i >= 0; i--) {
            buffer.putInt(records.get(i).key());
        }
        // last entry is -1
        buffer.putInt(-1);
        valueMRUListEx.setData(buffer.data());
    }

    public RecentRecord record(String key) {
        return(record(Integer.parseInt(key)));
    }

    public RecentRecord record(int key) {
        int i = getIndexForKey(key);
        if (i >= 0) {
            return (records.get(i));
        } else {
            return (null);
        }
    }

}
