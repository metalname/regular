/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms.User.ShellBags;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author 
 */
public class MruList implements Iterable<MruNode>, Iterator<MruNode> {
    
    private final ArrayList<MruNode> nodes;
    private MruNode parent;
    private Iterator<MruNode> iterator;
    
    public MruList() {
        nodes = new ArrayList<>();
    }
    
    public MruList(MruNode parent) {
        this();
        this.parent = parent;
    }
    
    public void add(MruNode node) {
        nodes.add(node);
    }
    
    @Override
    public Iterator<MruNode> iterator() {
        iterator = nodes.iterator();
        return(iterator);
    }
    
    @Override
    public MruNode next() {
        return(iterator.next());
    }
    
    @Override
    public boolean hasNext() {
        return(iterator.hasNext());
    }
    
    public int size() {
        return(nodes.size());
    }
    
    public MruNode get(int index) {
        return(nodes.get(index));
    }
    
    public void remove(MruNode node) {
        nodes.remove(node);
    }
}
