/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

import java.util.ArrayList;

/**
 *
 * @author cvandenheuvel
 */
public class RuleArray extends ArrayList<Rule>{
    
    public boolean includes(RuleArray rules) {
        for (Rule rule: rules) {
            if (!contains(rule)) return (false);
        }
        return(true);
    }
    
}
