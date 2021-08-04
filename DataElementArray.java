package registry.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 *
 * Wraps an array of DataElement
 */
public class DataElementArray extends DataElementCollection implements Iterable<DataElement> {
    
    protected ArrayList<DataElement> elements;
    
    public DataElementArray() {
        elements = new ArrayList<>();
    }
    
    public DataElementArray(int init) {
        elements = new ArrayList<>(init);
    }    
        
    @Override
    public DataElement addElement(DataElement element) {
        elements.add(element);
        return (element);
    }
    
    public DataElement setElement(int index, DataElement element) {
        elements.set(index, element);
        return(element);
    }
    
    public DataElement get(int index) {
        return(elements.get(index));
    }

    public int lastElement() {
        return(elements.size() - 1);
    }
    
    public void add(DataElement d) {
        elements.add(d);
    }
    
    public int size() {
        return(elements.size());
    }
    
    public DataElement getElementByName(String name) {
        for (DataElement e : this) {
            if (e.getLabel().equals(name)) {
                return (e);
            }
        }
        return(null);
    }
    
    @Override
    public Iterator<DataElement> iterator() {
        return(new Iterator<DataElement> () {
            int index = 0;
                    
            @Override
            public boolean hasNext() {
                return(index < elements.size());
            }
            
            @Override
            public DataElement next() {
                return(elements.get(index++));
            }
        });
    }
    
    @Override
    public Stream<DataElement> stream() {
        return(elements.stream());
    }

}
