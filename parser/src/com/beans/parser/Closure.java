package com.beans.parser;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Closure implements Comparable{
    public Set<LRRule> items = new LinkedHashSet<>();
    public static int toHash(String key) {
        int arraySize = 11113;
        int hashCode = 0;
        for (int i = 0; i < key.length(); i++) {
            int letterValue = key.charAt(i) - 96;
            hashCode = ((hashCode << 5) + letterValue) % arraySize;
        }
        return hashCode;
    }
    public int hashCode()
    {
        return toHash(toString());
    }
    public String toString()
    {
        String ret = new String();
        for(LRRule r:items)
        {
            ret = ret + r.toString()+'\n';
        }
        return ret;
    }
    public Closure(Closure c)
    {
        for(LRRule rule:c.items)
        {
            items.add(rule);
        }
    }

    public Closure(LRRule r, List<LRRule> productions)
    {
        items.add(r);
        while(true)
        {
            Set<LRRule> last = new LinkedHashSet<>();
            for(LRRule rule:items)
                last.add(rule);
            for(LRRule rule:last)
            {
                Token token = rule.getHoped();
                if(token.getClaz() == 0)
                {
                    for(LRRule production:productions)
                    {
                        if(production.getHead().equals(token.toString()))
                        {
                            items.add(production);
                        }
                    }
                }
            }
            if(items.size() == last.size())
                break;
        }
    }
    public Closure(Set<LRRule> last, List<LRRule> productions)
    {
        for(LRRule rule:last)
        {
            items.add(rule);
        }
        while(true)
        {
            for(LRRule rule:last)
            {
                Token token = rule.getHoped();
                if(token == null)
                    break;
                if(token.getClaz() == 0)
                {
                    for(LRRule production:productions)
                    {
                        if(production.getHead().equals(token.toString()))
                        {
                            items.add(production);
                        }
                    }
                }
            }
            if(items.size() == last.size())
                break;
            last.clear();
            for(LRRule rule:items)
            {
                last.add(rule);
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        Closure c = (Closure)o;
        if(c.toString().equals(this.toString()))
            return 0;
        return -1;
    }
    public boolean equals(Object o)
    {
        Closure c = (Closure)o;
        return this.toString().equals(c.toString());
    }
}
