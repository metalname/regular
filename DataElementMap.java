package registry.element;

/**
 *
 * Implements a map of DataElement using enums
 * This allows elements to be selected by either index or enum
 * 
 */
public class DataElementMap extends DataElementArray {
    
    private Enum[] enums;
    
    /**
     * Constructor
     * @param values enum class
     */
    public DataElementMap(Enum[] values) {
        prepareEnum(values);
    }
            
    /**
     * Builds map from enums
     * 
     * @param values 
     */
    private void prepareEnum(Enum[] values) {
        // store enum constants
        enums = values;
        // initialize arraylist
        for (int i = 0; i < values.length; i++) {
            elements.add(null);
        }
    }            
    
    /**
     * Checks if element at specified index is mapped
     * 
     * @param i
     * @return 
     */
    public boolean isMapped(int i) {
        return (elements.get(i) != null);
    }

    /**
     * Checks if element with specified enum is mapped
     * 
     * @param e
     * @return 
     */
    public boolean isMapped(Enum e) {
        return (isMapped(e.ordinal()));
    }    
    
    /**
     * Returns element with specified enum
     * 
     * @param e
     * @return 
     */
    public DataElement getElement(Enum e) {
        if (isMapped(e)) {
            return (getElement(e.ordinal()));
        } else {
            throw new RuntimeException("Label " + e.toString() + " is not mapped");
        }
    }   
    
    /**
     * Get element at specified index
     * 
     * @param i
     * @return 
     */
    public DataElement getElement(int i) {
        return(elements.get(i));
    }
    
    /**
     * Set element with specified enum
     * 
     * @param e
     * @param d
     * @return 
     */
    public DataElement setElement(Enum e, DataElement d) {
        elements.set(e.ordinal(), d);
        return(d);
    }
    
    /**
     * Get element for specified enum
     * 
     * @param e
     * @return 
     */
    public DataElement get(Enum e) {
        return(getElement(e.ordinal()));
    }
    
}
