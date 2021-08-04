/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lexer;

import java.util.ArrayList;

import java.util.regex.*;

/**
 * Simple lexer
 * Splits input stream into an array of tokens based on matches with a set of
 * regular expressions
 * @author cvandenheuvel
 */
public class Lexer {

    // tokenArray will be built by the lexer and returned to the calling method
    protected TokenArray tokenArray = new TokenArray();
    // rules - specifies a list of rules to be matched from the input stream
    protected ArrayList<Rule> rules = new ArrayList<>();
    // charStream contains the string to be tokenized
    protected StringBuilder charStream;
    // lastPos contains the last character positionmatched
    protected int lastPos = 0;
    protected int currentLine = 1;
    protected int currentColumn = 1;
    // token to be returned on EOF
    protected Token EOFToken;
    protected Token token;
    // flag determines wether short or long match is to be used
    protected boolean longMatch = true;
    TokenArray matchTokens = new TokenArray();
    
    public Lexer() {
        charStream = null;
    }

    public Lexer(String charStream) {
        this(new StringBuilder(charStream));
    }

    public Lexer(StringBuilder charStream) {
        this.charStream = charStream;
    }

    public void reset(String charStream) {
        reset(new StringBuilder(charStream));
    }
    
    public void reset(StringBuilder charStream) {
        this.charStream = charStream;
        tokenArray = new TokenArray();
        lastPos = 0;
        currentLine = 1;
        currentColumn = 1;        
    }
    
    public void setShortMatch() {
        longMatch = false;
    }
    
    public Lexer addRule(Rule r) {
        rules.add(r);
        return(this);
    }

    public Lexer addRule(String regex, java.lang.Enum tokenID) {
        addRule(new Rule(regex, tokenID));
        return(this);
    }

    // add a new rule based onthe supplied regex, token ID and whitespace indicator
    public Lexer addRule(String regex, java.lang.Enum tokenID, boolean isWS) {
        addRule(new Rule(regex, tokenID, isWS));
        return(this);
    }
    
    public void setEOFToken(java.lang.Enum tokenID) {
        EOFToken = new Token(tokenID);
    }

    public TokenArray parse(String code) throws LexerException {
        reset(code);
        return(parse());
    }
    
    // build the token array
    public TokenArray parse() throws LexerException {

        do {
            token = nextToken();
            if (token != null) {
                // skip whitespace
                if (!token.isWS()) {
                    tokenArray.add(token);
                }
            } else {
                break;
            }
        } while (lastPos < charStream.length());
        if (EOFToken != null) tokenArray.add(EOFToken);
        return (tokenArray);
    }

    // attempts to match next token against list of rules
    // returns null on EOF
    protected Token nextToken() throws LexerException {
        
        // loop through rules - scan for matches in string
        matchTokens.clear();
        rules.forEach((rule) -> {
            // only want tokens that start at the beginning of the input
            Matcher matcher = rule.pattern().matcher(charStream.substring(lastPos));
            if (matcher.find(0)) {
                if ((matcher.start() == 0) && (matcher.end() > 0)) {
                    matchTokens.add(new Token(matcher, rule.getTokenID(), currentLine, currentColumn, rule.isWS()));
                }
            }
        });
        // check if any matches
        if (matchTokens.isEmpty()) {
            String s = charStream.length() > 10 ? charStream.substring(lastPos, lastPos+9) : charStream.substring(lastPos);
            throw new LexerException("Could not match input at line " + currentLine + ", column " + currentColumn + "\n"
                    + "Unmatched string begins with " + s);
        }
        // extract longest or shortest string matched
        Token extractedToken = extractToken(matchTokens);
        updatePosition(extractedToken.matcher().end());
        return (extractedToken);
    }
    
    // search token array for eitehr shortest or longest string based on longMatch flag
    protected Token extractToken(TokenArray tokens) {
        if (longMatch) {
            return(extractLongestToken(tokens));
        } else {
            return(extractShortestToken(tokens));
        }
    }
    
    protected Token extractLongestToken(TokenArray tokens) {
        int maxEnd = 0;
        Token longestToken = null;
        
        for (Token t : tokens) {
            if (t.matcher().end() > maxEnd) {
                maxEnd = t.matcher().end();
                longestToken = t;
            }
        }        
        return(longestToken);
    }
    
    protected Token extractShortestToken(TokenArray tokens) {        
        Token shortestToken = tokens.get(0);
        int minEnd = shortestToken.matcher().end();
        
        for (Token t : tokens) {
            if (t.matcher().end() < minEnd) {
                minEnd = t.matcher().end();
                shortestToken = t;
            }
        }        
        return(shortestToken);
    }    

    protected String nextCharStream() {
        return (charStream.substring(lastPos));
    }

    StringBuilder sb = new StringBuilder();
    
    protected void updatePosition(int length) {
        // count number of newlines 
        sb.setLength(0);
        sb.append(charStream.subSequence(lastPos, lastPos + length));
        int count = 0;
        int last = 0;
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\n') {
                count++;
                last = i;
            }
        }
        if (count > 0) {
            currentLine += count;
            currentColumn = sb.length() - last;
        } else {
            currentColumn += length;
        }
        lastPos = lastPos + length;
    }
}
