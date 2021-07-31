package registry.element;

import java.util.stream.Stream;

/**
 *
 */
public abstract class DataElementCollection implements Iterable<DataElement> {
    
    public abstract DataElement addElement(DataElement d);
    public abstract Stream<DataElement> stream();
    
}
