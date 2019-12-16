package com.beans.parser;


public class Token implements Comparable{
    private int claz = 0;//0:NonTerm; 1:Term
    private String value;//What it is exactly
    public int val = 0;
    public Token(String s)
    {
        com.beans.lexer.Lexer lexer = new com.beans.lexer.Lexer(s);
        lexer.doLex();
        if('0' <= s.charAt(0) && s.charAt(0) <= '9')
        {
            claz = 1;
            value = "i";
            val = Integer.parseInt(lexer.tokens.get(0).lexeme);
        }else
        if(('a' <= s.charAt(0) && s.charAt(0) <= 'z')
                || s.charAt(0) == '+'
                || s.charAt(0) == '*'
                || s.charAt(0) == '-'
                || s.charAt(0) == '/'
                || s.charAt(0) == 'ε'
                || s.charAt(0) == '('
                || s.charAt(0) == ')'
                || s.charAt(0) == '$')
        {
            claz = 1;
            value = "" + s.charAt(0);
        }
        else
        if(s.length() > 1 && s.charAt(1) == '\'' && 'A' <= s.charAt(0) && s.charAt(0) <= 'Z')
        {
            claz = 0;
            value = s.substring(0,2);
        }
        else
        if ('A' <= s.charAt(0) && s.charAt(0) <= 'Z')
        {
            claz = 0;
            value = "" + s.charAt(0);
        }
    }
    public boolean equals(String str)
    {
        return value.equals(str);
    }
    public boolean equals(Object o){
        Token t = (Token)o;
        return this.value.equals(t.toString());
    }
    public int getLen()
    {
        return value.length();
    }
    public String toString()
    {
        return value;
    }
    public int getClaz()
    {
        return claz;
    }

    @Override
    public int compareTo(Object o) {
        Token t = (Token)o;
        if(t.equals(this.toString()))
            return 0;
        return -1;
    }
}
