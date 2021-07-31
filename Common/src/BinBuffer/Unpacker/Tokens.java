/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer.Unpacker;

import lexer.Lexer;

/**
 *
 * @author 
 */
public enum Tokens {
    //CHAR,

    VAR,
    INT,
    HEX,
    COMMA,
    LBRACKET,
    RBRACKET,
    LPARAN,
    RPARAN,
    QUESTION,
    ASTERISK,
    EQUALS,
    PLUS,
    MINUS,
    DIVIDE,
    SEMICOLON,
    AMPERSAND,
    BAR,
    LCURLY,
    RCURLY,
    PERIOD,
    GT,
    LT,
    GE,
    LE,
    NE,
    HASH,
    WHITESPACE,
    EOF;

    public String getRegex() {
        switch (this) {
            case INT:
                return ("[0-9]+");
            case HEX:
                return("0x[0-9A-Fa-f]+");
            case VAR:
                return ("[A-Za-z_][A-Za-z_0-9]+");
            case COMMA:
                return ("\\,");
            case LBRACKET:
                return ("\\[");
            case RBRACKET:
                return ("\\]");
            case LPARAN:
                return ("\\(");
            case RPARAN:
                return("\\)");
            case ASTERISK:
                return ("\\*");
            case QUESTION:
                return("\\?");
            case EQUALS:
                return("=");
            case PLUS:
                return("\\+");
            case MINUS:
                return("\\-");
            case DIVIDE:
                return("\\/");
            case SEMICOLON:
                return(";");
            case AMPERSAND:
                return("&");
            case BAR:
                return("|");
            case LCURLY:
                return("\\{");
            case RCURLY:
                return("\\}");
            case PERIOD:
                return("\\.");
            case GT:
                return(">");
            case LT:
                return("<");
            case GE:
                return(">=");
            case LE:
                return("<=");
            case NE:
                return("!=");
            case HASH:
                return("\\#");
            case WHITESPACE:
                return ("[ \t\n]+");
            case EOF:
                return ("");
            default:
                System.err.println("Token " + this.toString() + " not realized");
                System.exit(-1);
                return (null);
        }
    }

    public static void setupLex(Lexer lex) {
        for (Tokens token : Tokens.values()) {
            if (token == EOF) {
                lex.setEOFToken(token);
            } else if (token == WHITESPACE) {
                lex.addRule(token.getRegex(), token, true);
            } else {
                lex.addRule(token.getRegex(), token);
            }
        }
    }
}

/*
variable declaration:
[offset expression] name type [flag modifier]

types:
byte
buffer
char
int
short
long
asciiz(max length)
asciil(length)
unicodez(max length)
unicodel(length)

array:
type[length]

structure types:
rec
union
switch

flag definition:
flag name value

type flag modifier:
type flag[,flag]*

examples:

struct:
    var1 int;
    var1
*/