/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BinBuffer.Unpacker;

import BinBuffer.BinHelper;
import BinBuffer.Var.Var;
import BinBuffer.Var.VarArray;
import BinBuffer.Var.VarType;
import BinBuffer.VarSet.LabelVarSet;
import BinBuffer.VarSet.VarSet;
import lexer.Lexer;
import lexer.LexerException;
import lexer.Token;
import lexer.TokenArray;

/**
 *
 * @author 
 */
public class Unpacker {

    private static Lexer lexer = null;
    private static TokenArray tokens = null;
    private static Token currToken = null;
    private static int currTokenIndex = 0;
    private static BinHelper buffer = null;

    private static Token nextToken() {
        if (currTokenIndex < tokens.size()) {
            currToken = tokens.get(currTokenIndex++);
            return (currToken);
        } else {
            return (null);
        }
    }

    private static Token peekToken() {
        if (currTokenIndex < tokens.size()) {
            return (tokens.get(currTokenIndex));
        } else {
            return (null);
        }
    }

    private static boolean hasNextToken() {
        return (currTokenIndex < tokens.size() && (peekToken().getTokenID() != Tokens.EOF));
    }

    //BNF for VarSet
    // SPEC: IDENTIFIER 
    //     | IDENTIFIER [ integer ]
    //create varlist from string spec
    // spec = list of type identifiers
    // types (case sensitive) -
    //  i | int = Int
    //  o | short = Short
    //  b | byte = Byte
    //  l | long = Long
    //  Bn | bytesn = Byte Array of size n
    //  Zn | stringuzn = Unicode String null-terminated (max lengh n)
    //  zn | stringazn = ASCII String null-terminated (max length n)
    //  Sn | stringuln = Unicode string of length n
    //  sn | stringaln = ASCII String of length n
    //  array specified by appending [ n ]
    public static VarSet UnpackVarSet(BinHelper b, String spec, int offset) {
        buffer = b;
        if (lexer == null) {
            setupLexer();
        }
        VarSet vs = new VarSet(buffer);
        try {
            tokens = lexer.parse(spec);
            currTokenIndex = 0;
        } catch (LexerException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        while (hasNextToken()) {
            if (!vsSpec(vs)) {
                throwParseError("specification expected");
            }
        }
        return (vs);
    }

    public static VarSet UnpackVarSet(BinHelper b, String spec) {
        return (UnpackVarSet(b, spec, 0));
    }
        
    private static void throwParseError(String message) {
        throw new RuntimeException("Parse error: " + message + ", last token was '"
                + ((currToken == null) ? "(UNDEFINED)" : currToken.getText()) + "' (" + currToken.getTokenID() + ")");
    }

    private static boolean vsSpec(VarSet vs) {
        if (nextToken().getTokenID() == Tokens.VAR) {
            String spec = currToken.getText();
            String num;
            Var var;
            // check if next token is an int
            if (hasNextToken() && (peekToken().getTokenID() == Tokens.INT)) {
                num = nextToken().getText();
                var = makeVar(spec, num);
            } else {
                var = makeVar(spec);
            }
            // check if array definition follows
            if (hasNextToken() && (peekToken().getTokenID() == Tokens.LBRACKET)) {
                nextToken();
                if (nextToken().getTokenID() == Tokens.INT) {
                    num = currToken.getText();
                    if (nextToken().getTokenID() == Tokens.RBRACKET) {
                        var = makeArrayVar(var, num);
                        vs.addVar(var);
                        return (true);
                    }
                }
            } else {
                vs.addVar(var);
                return (true);
            }
        }
        return (false);
    }

    private static Var makeVar(String spec) {
        switch (spec) {
            case "i":
            case "int":
                return (Var.makeVar(VarType.T_INT, 0, buffer));
            case "o":
            case "short":
                return (Var.makeVar(VarType.T_SHORT, 0, buffer));
            case "byte":
            case "b":
                return (Var.makeVar(VarType.T_BYTE, 0, buffer));
            case "long":
            case "l":
                return (Var.makeVar(VarType.T_LONG, 0, buffer));
            case "stringuz":
            case "Z":
                return (Var.makeVar(VarType.T_UNICODESZ, 0, buffer));
            case "stringaz":
            case "z":
                return (Var.makeVar(VarType.T_ASCIISZ, 0, buffer));
            default:
                throwParseError("unknown type specifier '" + spec + "'");
                return (null);
        }
    }

    private static Var makeVar(String spec, String num) {
        int i = Integer.parseInt(num);
        switch (spec) {
            case "bytes":
            case "B":
                return (Var.makeVar(VarType.T_BARRAY, i, buffer));
            case "stringuz":
            case "Z":
                return (Var.makeVar(VarType.T_UNICODESZ, i, buffer));
            case "stringaz":
            case "z":
                return (Var.makeVar(VarType.T_ASCIISZ, i, buffer));
            case "stringul":
            case "S":
                return (Var.makeVar(VarType.T_UNICODESL, i, buffer));
            case "stringal":
            case "s":
                return (Var.makeVar(VarType.T_ASCIISL, i, buffer));
            default:
                throwParseError("unknown type specifier '" + spec + "'");
                return (null);
        }
    }

    private static Var makeArrayVar(Var var, String num) {
        int elements = Integer.parseInt(num);
        return (VarArray.makeVarArray(var, buffer, elements));
    }

    private static void setupLexer() {
        lexer = new Lexer();
        Tokens.setupLex(lexer);
    }

    //BNF for VarSet
    // SPEC: IDENTIFIER 
    //     | IDENTIFIER [ integer ]
    // IDENTIFIER | label '=' TYPESPEC
    //create varlist from string spec
    // spec = list of type identifiers
    // types (case sensitive) -
    //  i | int = Int
    //  o | short = Short
    //  b | byte = Byte
    //  l | long = Long
    //  Bn | bytesn = Byte Array of size n
    //  Zn | stringuzn = Unicode String null-terminated (max lengh n)
    //  zn | stringazn = ASCII String null-terminated (max length n)
    //  Sn | stringuln = Unicode string of length n
    //  sn | stringaln = ASCII String of length n
    //  array specified by appending [ n ]
    public static LabelVarSet UnpackLabelVarSet(BinHelper b, String spec, int offset) {
        buffer = b;
        if (lexer == null) {
            setupLexer();
        }
        LabelVarSet vs = new LabelVarSet(buffer);
        try {
            tokens = lexer.parse(spec);
            currTokenIndex = 0;
        } catch (LexerException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        while (hasNextToken()) {
            if (!lvsSpec(vs)) {
                throwParseError("specification expected");
            }
        }
        return (vs);
    }

    public static LabelVarSet UnpackLabelVarSet(BinHelper b, String spec) {
        return (UnpackLabelVarSet(b, spec, 0));
    }

    private static boolean lvsSpec(LabelVarSet vs) {
        if (nextToken().getTokenID() == Tokens.VAR) {
            String label = currToken.getText();
            // next token must be '='
            if (nextToken().getTokenID() == Tokens.EQUALS) {
                if (nextToken().getTokenID() == Tokens.VAR) {
                    String spec = currToken.getText();
                    String num;
                    Var var;
                    // check if next token is an int
                    if (hasNextToken() && (peekToken().getTokenID() == Tokens.INT)) {
                        num = nextToken().getText();
                        var = makeVar(spec, num);
                    } else {
                        var = makeVar(spec);
                    }
                    // check if array definition follows
                    if (hasNextToken() && (peekToken().getTokenID() == Tokens.LBRACKET)) {
                        nextToken();
                        if (nextToken().getTokenID() == Tokens.INT) {
                            num = currToken.getText();
                            if (nextToken().getTokenID() == Tokens.RBRACKET) {
                                var = makeArrayVar(var, num);
                                vs.addVar(var, label);
                                return (true);
                            }
                        }
                    } else {
                        vs.addVar(var, label);
                        return (true);
                    }

                }
            }
        }
        return (false);
    }

}
