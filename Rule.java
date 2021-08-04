/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

import java.util.regex.*;

/**
 *
 * @author cvandenheuvel
 */
public class Rule {
    protected String regex;
    protected java.lang.Enum tokenID;
    protected boolean isWS;
    protected Pattern pattern;
    
    public Rule(String regex, java.lang.Enum tokenID, boolean isWS) {
        this.regex = regex;
        this.tokenID = tokenID;
        this.isWS = isWS;
        this.pattern = Pattern.compile(this.regex, Pattern.DOTALL);
    }
    
    public Rule(String regex, java.lang.Enum tokenID) {
        this(regex, tokenID, false);
    }    
    
    public String getRegex() {
        return (regex);
    }
    
    public java.lang.Enum getTokenID() {
        return (tokenID);
    }
    
    public boolean isWS() {
        return (isWS);
    }
    
    public Pattern pattern() {
        return(pattern);
    }
    
}
