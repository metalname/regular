/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author 
 */
public class ChunkList<E> implements Iterable<E>, List<E> {

    private static final int defaultChunkSize = 1000;

    private final int chunkSize;
    private ArrayList<Chunk<E>> chunks;
    private int length = 0;
    private Chunk currentChunk;

    /*
     ** Inner class - chunk
     ** Wraps an object array
     */
    private class Chunk<E> {

        private final Object[] data;
        private int lastElement = 0;

        public Chunk(int size) {
            data = new Object[size];
        }

        public E get(int index) {
            return ((E) data[index]);
        }

        public void set(int index, E element) {
            data[index] = element;
        }

        public void add(E element) {
            set(lastElement++, element);
        }

        public int remaining() {
            return (data.length - lastElement);
        }

    }

    /*
     ** Constructor
     */
    public ChunkList(int size) {
        chunkSize = size;
        chunks = new ArrayList<>();
        addChunk();
    }

    public ChunkList() {
        this(defaultChunkSize);
    }

    @Override
    public boolean add(E element) {
        if (currentChunk.remaining() == 0) {
            addChunk();
        }
        currentChunk.add(element);
        length++;
        return (true);
    }

    private void addChunk() {
        Chunk<E> chunk = new Chunk<>(chunkSize);
        chunks.add(chunk);
        currentChunk = chunk;
    }

    @Override
    public E get(int index) {
        if ((index >= 0) && (index < length)) {
            return ((chunks.get(chunkNoFromIndex(index))).get(elementFromIndex(index)));
        } else {
            throw new IndexOutOfBoundsException("Array index oout of bounds: " + index);
        }
    }

    @Override
    public E set(int index, E element) {
        if ((index >= 0) && (index < length)) {
            chunks.get(chunkNoFromIndex(index)).set(elementFromIndex(index), element);
            return (element);
        } else {
            throw new IndexOutOfBoundsException("Array index oout of bounds: " + index);
        }
    }

    private int chunkNoFromIndex(int index) {
        return (index / chunkSize);
    }

    private int elementFromIndex(int index) {
        return (index % chunkSize);
    }

    @Override
    public int size() {
        return (length);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return (index < size());
            }

            @Override
            public E next() {
                return (get(index++));
            }

        };
    }

    @Override
    public ChunkList<E> subList(int start, int end) {
        ChunkList<E> newList = new ChunkList<>();
        for (int i = start; i < end; i++) {
            newList.add(get(i));
        }
        return (newList);
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < size(); i++) {
            if ((E) o == get(i)) {
                return (i);
            }
        }
        return (-1);
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = size() - 1; i >= 0; i--) {
            if ((E) o == get(i)) {
                return (i);
            }
        }
        return (-1);
    }

    @Override
    public E remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int i, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        chunks = new ArrayList<>();
        length = 0;
        addChunk();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection c) {
        for (Object o : c) {
            add((E) o);
        }
        return (true);
    }

    @Override
    public boolean addAll(int i, Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        for (E element : this) {
            if ((E) o == element) {
                return (true);
            }
        }
        return (false);
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object o : c) {
            if (!contains(o)) {
                return (false);
            }
        }
        return (true);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size()];
        for (int i = 0; i < size(); i++) {
            array[i] = get(i);
        }
        return(array);
    }
    
    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isEmpty() {
        return(size() == 0);
    }
}
