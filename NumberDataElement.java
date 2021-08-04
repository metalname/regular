package registry.element;

/**
 *
 * Interface for number-type elements (INT, LONG, SHORT etc.)
 */
public interface NumberDataElement {
    
    public int getValueI();
    public void setValueI(int value);

    public void setValue(int l);
    
}
