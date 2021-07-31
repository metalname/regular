/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import java.util.ArrayList;
import registry.RegistryException;

/**
 *
 * @author 
 */
public class HandlerList extends ArrayList<AbstractHandler> {
    
    private HandlerBuilder builder;
    
    public HandlerList(int count, HandlerBuilder builder) {
        super(count);
        this.builder = builder;
        for (int i = 0; i < count; i++) {
            add(null);
        }
    }
    
    public AbstractHandler get(Enum e) {
        return(get(e.ordinal()));
    }
    
    public void set(Enum e, AbstractHandler handler) {
        set(e.ordinal(), handler);
    }
    
    public boolean close() {
        for (AbstractHandler handler: this) {
            if ((handler != null) && (!handler.confirmClose())) {
                return(false);
            }
        }
        return(true);
    }
    
    public void show(Enum e) throws RegistryException  {
        AbstractHandler handler = builder.makeHandler(e);
        set(e, handler);        
        if (handler != null) {
            handler.show();
        }
    }
    
}
