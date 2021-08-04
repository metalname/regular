package registry.Nodes;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * Wraps a list of cell values
 */
public class ValueList implements Iterable<ValueNode> {

    //private Iterator<ValueNode> itr;
    private final ArrayList<ValueNode> values;    

    /**
     * Constructor
     */
    public ValueList() {
        values = new ArrayList<>();
    }

    /**
     * Constructor - specified size
     *
     * @param capacity
     */
    public ValueList(int capacity) {
        values = new ArrayList<>(capacity);
    }

    /**
     * Adds a value node
     *
     * @param value
     */
    public void add(ValueNode value) {
        values.add(value);
    }

    /**
     * Get element at specified index
     *
     * @param i
     * @return
     */
    public ValueNode get(int i) {
        return (values.get(i));
    }

    /**
     * Iterator
     * @return 
     */
    @Override
    public Iterator<ValueNode> iterator() {
        return new Iterator<ValueNode>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return (index < values.size());
            }

            @Override
            public ValueNode next() {
                return (values.get(index++));
            }
        };
    }

    /**
     * Get size
     * @return 
     */
    public int size() {
        return (values.size());
    }

}
