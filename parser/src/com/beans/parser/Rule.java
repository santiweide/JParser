package com.beans.parser;

import java.util.LinkedList;

public class Rule {
    protected String head;
    protected LinkedList<Token> rest = new LinkedList<>();

    public String getHead()
    {
        return head;
    }
    public Token getTHead() {return new Token(head);}
    public LinkedList<Token> getRest()
    {
        return rest;
    }

    public Rule(String l,String r)
    {
        head = l.trim();
        r = r.trim();
        for(int i = 0;i < r.length();i ++)
        {
            Token next = getNextToken(r.substring(i));
            rest.add(next);
            if(next.getLen() == 2)
                i ++;
        }

    }
    public Token getNextToken(String str)
    {
        return new Token(str);
    }
    public String getRestStr(int begin)
    {
        String ret = new String();
        for(int i =begin ;i < rest.size();i ++)
        {
            ret = ret + rest.get(i);
        }
        return ret;
    }
    public String getJustStr()
    {
        String ret = new String();
        for(int i = 0;i < rest.size();i ++)
        {
            ret = ret + rest.get(i);
        }
        return ret;
    }

    public boolean equals(Rule rule)
    {
        if(!rule.getHead().equals(this.head))
            return false;
        if(rule.getRest().size() != this.getRest().size())
            return false;
        for(int i =0 ;i < this.rest.size();i ++)
        {
            if(!rule.getRest().get(i).equals(this.rest.get(i)))
                return false;
        }
        return true;
    }
    public String toString()
    {
        return head + "->" + getRestStr(0);
    }


}
