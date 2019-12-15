package com.beans.parser;

public class LRRule extends Rule implements Comparable {
    protected int dot = 0;
    protected String forward;

    public Token getHoped()
    {
        if(dot < this.getRest().size())
            return this.getRest().get(dot);
        return null;
    }
    public LRRule(String l, String r) {
        super(l, r);
        dot = 0;
    }
    public LRRule(String l, String r,int dot) {
        super(l, r);
        this.dot = dot;
    }
    public String toString()
    {
        return head +"->"+ getRestStr(0);
    }

    @Override
    public String getRestStr(int begin) {
        String ret = new String();
        for(int i =begin ;i < rest.size();i ++)
        {
            if(i == dot)
                ret = ret + "●";
            ret = ret + rest.get(i);
        }
        return ret;
    }

    @Override
    public int compareTo(Object o) {
        LRRule r = (LRRule)o;
        if(this.toString().equals(r))
            return 0;
        return -1;
    }
}
