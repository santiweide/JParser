package com.beans.parser;

import com.beans.lexer.Kind;
import com.beans.lexer.Lexer;
import com.beans.lexer.Token;

public class RecursiveDesParser {
    private int i = 0;
    private boolean ok = false;
    Lexer lexer;
    public RecursiveDesParser(Lexer lexer)
    {
        this.lexer = lexer;
        this.ok = false;
        i = 0;
    }

    public boolean doParse()
    {
        try
        {
            parse_E();
            ok = true;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        if(nextToken() != null)
        {
            System.out.println("输入错误！未处理完的字符串");
        }
        return ok;
    }
    private Token nextToken()
    {
        if(i < lexer.tokens.size())
            return (Token)lexer.tokens.get(i);
        return null;
    }

    public boolean parse_E()
    {
        Token token = nextToken();
        //System.out.println("parse E " + token);

        if(token == null){
            return false;
        }
        if(parse_T())
        {
            if(parse_E2())
            {
                System.out.println( "E->TE'");
                return true;
            }
        }
        System.out.println("弹栈，弹出非终结符E");
        return  false;
    }
    public boolean parse_E2()
    {
        Token token = nextToken();
       // System.out.println("parse E' " + token);
        if (token != null && token.k == Kind.PLUS)
        {
            i ++;
            if(parse_T())
            {
                if(parse_E2())
                {
                    System.out.println( "E'->+TE'" );
                    return true;
                }
            }
        }
        System.out.println( "E'->ε");
        return true;
    }
    public boolean parse_T()
    {
        Token token = nextToken();
        //System.out.println("parse T " + token);

        if(token == null)
        {
            return false;
        }
        if (parse_F())
        {
            if(parse_T2())
            {
                System.out.println( "T->FT'");
                return true;
            }
        }
        System.out.println( "弹栈，弹出非终结符T");
        return false;
    }
    public boolean parse_T2()
    {
        Token token = nextToken();
        //System.out.println("parse T' " + token);
        if (token != null && token.k == Kind.TIMES)
        {
            i ++;
            if(parse_F() )
            {
                if(parse_T2()){
                    System.out.println( "T'->*FT'");
                    return true;
                }
            }
        }
        System.out.println( "T'->ε");
        return true;
    }
    public boolean parse_F()
    {
        Token token = nextToken();
       // System.out.println("parse F " + token);

        if(token == null)
        {
            return false;
        }

        if (token.k == Kind.LBRACKET)
        {
            i ++;
            if(parse_E())
            {
                token = nextToken();
                //System.out.println("parse F" + token);
                if(token != null && token.k == Kind.RBRACKET)
                {
                    System.out.println( "F->(E)" );
                    i ++;
                    return true;
                }
                else
                {
                    System.out.println( "弹栈，弹出非终结符F，用户少输入一个右括号)");
                    return false;
                }
            }
        }
        else if (token.k == Kind.ID)
        {
            System.out.println( "F->id");
            i ++;
            return true;
        }
        System.out.println( "弹栈，弹出非终结符F，用户少输入一个id");
        return false;

    }
}
