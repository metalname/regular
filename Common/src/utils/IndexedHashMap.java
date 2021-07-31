/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * @author cvandenheuvel
 * @param <K>
 * @param <V>
 */
public class IndexedHashMap<K,V> implements Serializable, Iterable<V> {

    protected ArrayList<V> index;
    protected HashMap<K, Integer> map;

    public IndexedHashMap() {
        index = new ArrayList<>();
        map = new HashMap<>();
    }
    
    public IndexedHashMap(int size) {
        index = new ArrayList<>(size);
        map = new HashMap<>(size);
    }    

    public V get(Integer i) {
        return (index.get(i));
    }

    public Integer getIndex(K key) {
        return (map.get(key));
    }
    
    public Iterator<K> keyIterator() {
        return map.keySet().iterator();
    }

    public V get(K key) {
        Integer i = getIndex(key);
        return((i == null) ? null : index.get(i));
    }

    public void set(Integer i, V value) {
        index.set(i, value);
    }

    public void put(K key, V value) {
        Integer i = getIndex(key);
        if (i == null) {
            index.add(value);            
            i = index.size()-1;
        }            
        map.put(key, i);
    }
    
    public void remove(K key) {
        Integer i = getIndex(key);
        if (i != null) {
            index.remove(i.intValue());            
            map.remove(key);
        }            
    }
    
    public void replace(K key, V value) {
        Integer i = getIndex(key);
        if (i != null) {
            index.set(i, value);
            map.remove(key);
            map.put(key, i);
        }                    
    }

    public boolean containsKey(K key) {
        return (map.containsKey(key));
    }

    @Override
    public Iterator<V> iterator() {
        return (index.iterator());
    }

    public int size() {
        return (index.size());
    }
    
    public void clear() {
        index.clear();
        map.clear();
    }
    
    public Stream<V> stream() {
        return(index.stream());
    }
    
    public Set<K> getKeys() {
        return(map.keySet());
    }
}
