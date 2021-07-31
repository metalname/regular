package registry.element;

import java.text.ParseException;
import java.util.Date;
import utils.WindowsDate;

/**
 *
 * Windows FILETIME data element
 */
public class FiletimeDataElement extends LongDataElement {
    
    public FiletimeDataElement(final ElementType type, 
                       final int length, 
                       final int offset, 
                       final String label, 
                       final boolean follow) {    
        super(type, length, offset, label, follow);
    }    
        

    /**
     * Convert value to string a date/time
     * 
     * @return 
     */
    @Override
    public String toString() {
        return((new WindowsDate(value)).toString());
    }
    
    /**
     * Set data from string
     * Assumes string is in format yyyy-MM-dd hh:mm:ss.SSS
     * 
     * @param data
     * @return 
     */
    @Override
    public boolean setData(final String data) {
        try {
            long newValue = (new WindowsDate(data)).timestamp();
            value = newValue;
            return(true);
        } catch (ParseException e) {
            return(false);
        }
    }
    
    /**
     * Get date as WindowsDate object
     * @return 
     */
    public WindowsDate getWindowsDate() {
       return(new WindowsDate(value)) ;
    }
    
    /**
     * Get data as Java date object
     * @return 
     */
    public Date getDate() {
        return(new WindowsDate(value).toDate());
    }
    
    /**
     * Set value from WindowsDate object
     * @param dt 
     */
    public void setDate(WindowsDate dt) {
        value = dt.timestamp();
    }
    
    /**
     * Get value as epoch (long integer)
     * 
     * @return 
     */
    public long getTimestamp() {
        return(super.getValueL());
    }
        
}
