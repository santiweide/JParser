package com.beans.lexer;

import java.util.*;

public class Lexer {
    public String input;
    public List<Token> tokens = new ArrayList();
    public ArrayList<String> strTokens = new ArrayList<>();
    public Lexer(String input)
    {
        this.input = input;
    }
    Character nextChar(int i)
    {
        if(input != null && i < input.length())
            return new Character(input.charAt(i));
        return null;
    }
    Token nextToken(int i)
    {
        Character c = nextChar(++ i);
        if(c == null)
            return null;
        if(c.equals('<'))
        {
            c = nextChar(++ i);
            if(c == null)
                return new Token(Kind.LPAREN,"<");
            if(c.equals('='))
                return new Token(Kind.LEPAREN,"<=");
            if(c.equals('>'))
                return new Token(Kind.NEPAREN,"<>");
            else
            {
                i --;
                return new Token(Kind.LPAREN,"<");
            }
        }else if(c.equals('='))
        {
            return new Token(Kind.EQUAL,"=");
        }
        else if(c.equals('>'))
        {
            c = nextChar(++ i);
            if(c == null)
                return new Token(Kind.RPAREN,">");
            if(c.equals('='))
                return new Token(Kind.REPAREN,">=");
           else
            {
                i --;
                return new Token(Kind.RPAREN,">");
            }
        }
        else if (isLetter(c))
        {
            String charList = new String(c.toString());
            c = nextChar(++ i);
            while(c != null && (isLetter(c) || isDigit(c)) )
            {
                charList += c.toString();
                c = nextChar(++i);
            }
            i --;
            if(charList.equals("for"))
            {
                return new Token(Kind.FOR,charList);
            }else if(charList.equals("if"))
            {
                return new Token(Kind.IF,charList);
            }else if(charList.equals("then"))
            {
                return new Token(Kind.THEN,charList);
            }else if(charList.equals("else"))
            {
                return new Token(Kind.ELSE,charList);
            }else if(charList.equals("while"))
            {
                return new Token(Kind.WHILE,charList);
            }else if(charList.equals("do"))
            {
                return new Token(Kind.DO,charList);
            }else if(charList.equals("until"))
            {
                return new Token(Kind.UNTIL,charList);
            }else if(charList.equals("int"))
            {
                return new Token(Kind.INT,charList);
            }else if(charList.equals("input"))
            {
                return new Token(Kind.INPUT,charList);
            }else if(charList.equals("output"))
            {
                return new Token(Kind.OUTPUT,charList);
            }else
            {
                return new Token(Kind.ID,charList);
            }
        }
        else if(isDigit(c))
        {
            String digitList = c.toString();
            c = nextChar(++i);
            while(c != null && isDigit(c))
            {
                digitList += c.toString();
                c = nextChar(++i);
            }
            i --;
            return new Token(Kind.NUM,digitList);
        }else if(c.equals('+'))
        {
            return new Token(Kind.PLUS,"+");
        }else if(c.equals('-'))
        {
            return new Token(Kind.MINUS,"-");
        }else if(c.equals('*'))
        {
            return new Token(Kind.TIMES,"*");
        }else if(c.equals('/'))
        {
            return new Token(Kind.SLASH,"/");
        }else if(c.equals(':'))
        {
            c = nextChar(++ i);
            if(c != null && c.equals('='))
                return new Token(Kind.COLEQUAL,":=");
            i --;
            return new Token(Kind.COLON,":");
        }else if(c.equals(';'))
        {
            return new Token(Kind.SEMICOLON,";");
        }else if(c.equals('('))
        {
            return new Token(Kind.LBRACKET,"(");
        }else if(c.equals(')'))
        {
            return new Token(Kind.RBRACKET,")");
        }else if(c.equals('#'))
        {
            return new Token(Kind.WELL,"#");
        }
        return null;
    }
    private boolean isLetter(Character ch)
    {
        char c = ch.charValue();
        return ('a' <= c && c <= 'z');
    }
    private boolean isDigit(Character ch)
    {
        char c = ch.charValue();
        return ('0' <= c && c <= '9');
    }
    public boolean doLex()
    {
        int i = -1;
        do {
            Token nowToken = nextToken(i);
            if(nowToken != null)
            {
                //System.out.print(nowToken);
                tokens.add(nowToken);
                strTokens.add(nowToken.lexeme);
                i += nowToken.lexeme.length();
                if(nowToken.isEnd())
                {
                    //System.out.println();
                    return true;
                }
            }
            else i ++;
        }while(i < input.length());
        //System.out.println();
        return false;
    }

}
