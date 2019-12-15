package com.beans.parser;
/*
    包装类，因为LR0算法要把Closure和Token放到一个Stack里面555
 */
public class State {
    public Token token = null;
    public Closure closure = null;
    public State(Token t)
    {
        token = new Token(t.toString());
    }
    public State(Closure c)
    {
        closure = new Closure(c);
    }
    public String toString()
    {
        if(token != null)
        {
            return token.toString();
        }else
        {
            return closure.toString();
        }
    }
}
