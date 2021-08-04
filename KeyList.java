package registry.Nodes;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * Wraps an array of KeyNode
 */
public class KeyList implements Iterable<KeyNode> {
        
    private final ArrayList<KeyNode> keys;
    
    public KeyList() {
        keys = new ArrayList<>();
    }
    
    public KeyList(int capacity) {
         keys = new ArrayList<>(capacity);
    }
    
    public void add(KeyNode key) {
        keys.add(key);
    }
    
    public KeyNode get(int i) {
        return(keys.get(i));
    }
    
    @Override
    public Iterator<KeyNode> iterator() {
        return(new Iterator<KeyNode>() {
            
            int index = 0;
            
            @Override
            public KeyNode next() {
                return keys.get(index++);
            }
            
            @Override
            public boolean hasNext() {
                return index < keys.size();
            }
        });
    }
    
    public int size() {
        return(keys.size());
    }
    
}
