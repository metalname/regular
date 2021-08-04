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
public class Token {

    protected java.lang.Enum tokenID;
    protected int line;
    protected int column;
    protected Matcher matcher;
    protected boolean isWS = false;

    public Token(Matcher matcher, java.lang.Enum tokenID, int line, int column, boolean isWS) {
        this(line, column);
        this.tokenID = tokenID;
        this.matcher = matcher;
        this.isWS = isWS;
    }
    
    public Token(Matcher matcher, java.lang.Enum tokenID, int line, int column) {
        this(matcher, tokenID, line, column, false);
    }    

    public Token(int line, int column) {
        this.line = line;
        this.column = column;
        tokenID = null;
    }
    
    public Token(java.lang.Enum tokenID) {
        this.tokenID = tokenID;
    }

    public String getText() {
        if ((matcher != null) && (matcher.group() != null)) {
            return (matcher.group());
        } else {
            return ("");
        }
    }

    public int getLine() {
        return (line);
    }

    public int getColumn() {
        return (column);
    }

    
    public java.lang.Enum getTokenID() {
        return (tokenID);
    }    
    
    public void setTokenID(java.lang.Enum tokenID) {
        this.tokenID = tokenID;
    }

    public void setWS(boolean isWS) {
        this.isWS = isWS;
    }

    public boolean isWS() {
        return (isWS);
    }
    
    public Matcher matcher() {
        return(matcher);
    }
}
