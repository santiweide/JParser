package com.beans.lexer;



public class Token {
    public Kind k;
    public String lexeme;
    public Token(Kind k,String lexeme)
    {
        this.k = k;
        this.lexeme = lexeme;
    }
    public boolean isEnd()
    {
        return k == Kind.WELL || k == Kind.END;
    }
    public String toString()
    {
        return "("+this.k + ","+this.lexeme+")";
    }
    public boolean equals(Token t)
    {
        return this.k == t.k;
    }
}
