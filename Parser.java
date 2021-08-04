/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

import java.util.List;

/**
 *
 * @author 
 * @param <E>
 */
public class Parser<E extends Enum<E>> {

    //private final Class<E> enumClass;
    private final Lexer lexer;
    private TokenArray tokens;
    private Token currToken, nextToken;
    private int tokenptr;

    public Parser(LexerSetup ps) {
        //this.enumClass = enumClass;
        lexer = new Lexer();
        ps.setupLex(lexer);
    }

    public void start(String inputStream) throws LexerException {
        tokens = lexer.parse(inputStream);
        tokenptr = 0;
        nextToken();
    }

    public boolean hasNext() {
        return (nextToken != null);
    }

    public Token nextToken() {
        currToken = nextToken;
        if (tokenptr < tokens.size()) {
            nextToken = tokens.get(tokenptr++);
        } else {
            nextToken = null;
        }
        return (currToken);
    }

    private void report(E tokenID, Token token) throws LexerException {
        throw new LexerException("expected token "
                + tokenID.toString()
                + " at (" + token.getLine()
                + "," + token.getColumn()
                + ") but found '"
                + token.getText()
                + "'");
    }
    
    private void report(E tokenID, Token token, String match) throws LexerException {
        throw new LexerException("expected string '"
                + match
                + "' (" +
                token.getTokenID() + 
                ") at (" + token.getLine()
                + "," + token.getColumn()
                + ") but found '"
                + token.getText()
                + "'");
    }
    

    /**
     * try to match specified token type advance token pointer if found
     *
     * @param tokenID
     * @return
     * @throws LexerException
     */
    public boolean matchToken(E tokenID) throws LexerException {
        return (matchToken(tokenID, false));
    }

    /**
     * try to match specified token type and specified string advance token
     * pointer if found
     *
     * @param tokenID
     * @param match
     * @return
     * @throws LexerException
     */
    public boolean matchToken(E tokenID, String match) throws LexerException {
        return (matchToken(tokenID, match, false));
    }

    /**
     * try to match specified token type advance token pointer if found
     *
     * @param tokenID
     * @param required - generate exception if token is not matched
     * @return
     * @throws LexerException
     */
    public boolean matchToken(E tokenID, boolean required) throws LexerException {
        if (hasNext()) {
            if (nextToken.getTokenID() == tokenID) {
                nextToken();
                return (true);
            } else {
                if (required) {
                    report(tokenID, nextToken);
                }
                return (false);
            }
        } else {
            throw new LexerException("unexpected end of input");
        }
    }

    /**
     * try to match specified token type and specified string advance token
     * pointer if found
     *
     * @param tokenID
     * @param match
     * @param required - generate exception if no match
     * @return
     * @throws LexerException
     */
    public boolean matchToken(E tokenID, String match, boolean required) throws LexerException {
        if (hasNext()) {
            if (nextToken.getTokenID() == tokenID) {
                if (nextToken.getText().equals(match)) {
                    nextToken();
                    return (true);
                } else {
                    if (required) {
                        report(tokenID, nextToken, match);
                    }
                    return (false);
                }
            } else {
                if(required) {
                    report(tokenID, nextToken);
                }
            }
        } else {
            throw new LexerException("unexpected end of input");
        }
        return (false);
    }

    public Token currToken() {
        return (currToken);
    }

    public Lexer getlexer() {
        return (lexer);
    }

    public TokenArray getTokenArray() {
        return (tokens);
    }

    public TokenArray matchTokenStream(List<E> tokenList, boolean required) throws LexerException {

        TokenArray ta = new TokenArray();

        int mark = tokenptr;
        for (E e : tokenList) {
            if (matchToken(e, required)) {
                ta.add(currToken());
            } else {
                tokenptr = mark;
                return (null);
            }
        }
        return (ta);
    }
}
