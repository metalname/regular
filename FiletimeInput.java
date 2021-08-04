package InputHelpers;

import java.util.Calendar;
import java.util.Date;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

/**
 *
 * Swing component for editing timestamp
 */
public class FiletimeInput extends AbstractInput {

    JSpinner dateSpinner;
    SpinnerDateModel model;

    /**
     * Constructor
     * 
     */
    public FiletimeInput() {
        super();

        dateSpinner = new JSpinner();
        model = new SpinnerDateModel();
        model.setCalendarField(Calendar.MILLISECOND);
        dateSpinner.setModel(model);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm:ss.SSS"));
        dateSpinner.addAncestorListener(new RequestFocusListener());
        add(dateSpinner);
    }

    /**
     * Set value as epoch (long int)
     * 
     * @param timestamp 
     */
    public void setValue(long timestamp) {
        dateSpinner.setValue(timestamp);
    }
      
    /**
     * Set value as Date object
     * 
     * @param dt 
     */
    public void setValue(Date dt) {
        dateSpinner.setValue(dt);
    }

    /**
     * Get edited value as Date object
     * 
     * @return 
     */
    public Date getValue() {
        Date dt = (Date) dateSpinner.getValue();
        return (dt);
    }
}
