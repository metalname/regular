/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author cvandenheuvel
 */
public class TokenArray extends ArrayList<Token> {

    // return the token specified number of places ahead
    // without modifying index
    /*
    public Token peek(int ahead) {
        if (((index + ahead) > 0) && ((index + ahead) < size())) {
            return (get(index + ahead));
        } else {
            return (null);
        }
    }
*/
}
