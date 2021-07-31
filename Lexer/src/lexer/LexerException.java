/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

/**
 *
 * @author cvandenheuvel
 */
public class LexerException extends Exception {
    protected String message;
    protected int line;
    protected int column;
    
    public LexerException(String message) {
        this.message = message;
        this.line = 0;
        this.column = 0;
    }
    
    public LexerException(String message, int line) {
        this(message);
        this.line = line;
        this.column = 0;
    }
    
    public LexerException(String message, int line, int column) {
        this(message, line);
        this.column = column;
    }    
    
    @Override
    public String getMessage() {
        return(message + (line > 0 ? " at line " + line + (column > 0 ? "column " + column : "") : ""));
    }
    
}
