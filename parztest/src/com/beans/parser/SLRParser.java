package com.beans.parser;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class SLRParser {
    private String filename = "inputlr_2.txt";
    private static ArrayList<LRRule> productions = new ArrayList<>();
    private LinkedHashMap<String, TreeSet<String>> first = new LinkedHashMap<>();
    private LinkedHashMap<String,TreeSet<String> > follow = new LinkedHashMap<>();
    private TreeSet<String> nullable = new TreeSet<>();
    private HashMap<Closure, Integer> ClosureSet = new HashMap<>();
    private HashMap<Integer,Closure> ClosureFind = new HashMap<>();
    private HashMap<Pair<String,String>,String> GOTO = new HashMap<>();
    private HashMap<Pair<String,String>,String> ACTION = new HashMap<>();
    private Set<Token> NonTerminals = new TreeSet<>();
    private Set<Token> Terminals = new TreeSet<>();
    public String[][] table = new String[233][233];
    private Closure C0;
    int num = 0;
    private String [] getLinesFromFile(String filename)
    {
        String [] ret = null;
        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(filename));
            byte [] buffer = new byte[1024];
            int length = 0;
            String str = "";
            while((length = fis.read(buffer)) != -1)
            {
                str = str + new String(buffer,0,length);
            }
            ret = str.split("\n");
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    private void initProduction(String[] lines)
    {
        for(int i =0 ;i < lines.length;i ++)
        {
            int sep = lines[i].indexOf("→");
            int sep2 = lines[i].indexOf("->");
            String l = null;
            String r = null;
            if (sep2 < 0) {
                l = lines[i].substring(0, sep - 1).trim();
                r = lines[i].substring(sep + 1).trim();
            } else {
                l = lines[i].substring(0, sep2 - 1);
                r = lines[i].substring(sep2 + 2);
            }
            if(!r.contains("|"))
            {
                productions.add(new LRRule(l,r));
            }
            else
            {
                String[] rs = r.split("[|]");
                for(int j = rs.length - 1;j >= 0; j --)
                {
                    productions.add(new LRRule(l,rs[j]));
                }
            }
        }
    }
    private void initFirst()
    {
        for(int i =0;i < productions.size();i ++)
        {
//            System.out.println(productions.get(i));
            first.put(productions.get(i).getHead(), new TreeSet());
        }
        int size = 0;
        while(true)
        {
            for(int i =0;i < productions.size();i ++)
            {
                Rule r = productions.get(i);
                if(r.getRest().get(0).getClaz() == 1)
                {
                    TreeSet<String> set = first.get(r.getHead());
                    set.add(r.getRest().get(0).toString());
                    first.put(r.getHead(),set);
                }
                else
                {
                    for(int j = 0;j < r.getRest().size();j ++)
                    {
                        String M = r.getRest().get(j).toString();
                        TreeSet<String> list = first.get(r.getHead());
                        TreeSet<String> addlist = first.get(M);
                        for(String key:addlist)
                            list.add(key);
                        first.put(r.getHead(),list);
                        if(!nullable.contains(M))
                        {
                            break;
                        }
                    }
                }
            }
            int cnt = 0;
            for(String key : first.keySet())
            {
                cnt += first.get(key).size();
            }
            if(cnt == size)
                break;
            size = cnt;
        }
    }
    private void initNullable()
    {
        int size = 0;
        while(true)
        {
            for(int i =0;i < productions.size();i ++)
            {
                Rule r = productions.get(i);
                if(r.getRestStr(0).equals("ε"))
                {
                    nullable.add(r.getHead());
                }
                else
                {
                    boolean flg = true;
                    for(int j =0 ;j < r.getRest().size();j ++)
                    {
                        if(r.getRest().get(j).getClaz() == 1 )
                        {
                            flg = false;
                            break;
                        }else if(!nullable.contains(r.getRest().get(j).toString()))
                        {
                            flg = false;
                            break;
                        }
                    }
                    if(flg)
                    {
                        nullable.add(r.getHead());
                    }
                }
            }
            if(nullable.size() == size)
                break;
            size = nullable.size();
        }
    }
    private void initFollow()
    {
        for(int i =1;i < productions.size();i ++)
        {
            follow.put(productions.get(i).getHead(), new TreeSet());
        }
        TreeSet<String> t = new TreeSet<>();
        t.add("$");
        follow.put(productions.get(0).getHead(),t);

        int size = 0;
        while(true)
        {
            for(int i =0 ;i < productions.size();i ++)
            {
                Rule r = productions.get(i);
                //tmp = follow(N) = follow(qn)
                TreeSet<String> tmp = new TreeSet<>();
                for(String key:follow.get(r.getHead()))
                {
                    tmp.add(key);
                }

                for(int j = r.getRest().size() - 1;j >= 0;j --)
                {
                    //M=qi, calculate: tmp = follow(qi)
                    Token M = r.getRest().get(j);
                    if(M.getClaz() == 1)
                    {
                        if(!M.equals("ε"))
                        {
                            tmp.clear();
                            tmp.add(M.toString());
                        }
                    }
                    else
                    {
                        TreeSet<String> tmpM = new TreeSet<>();
                        for(String key: follow.get(M.toString()))
                        {
                            tmpM.add(key);
                        }
                        for(String key:tmp)
                        {
                            tmpM.add(key);
                        }
                        follow.put(M.toString(),tmpM);
                        //go ahead
                        if(!nullable.contains(M.toString()))
                            tmp.clear();
                        for(String key:first.get(M.toString()))
                        {
                            if(!key.contentEquals("ε"))
                                tmp.add(key);
                        }
                    }
//                    System.out.print("tmp : ");
//                    for(String key:tmp)
//                        System.out.print(key + " ");
//                    for(String key:follow.keySet())
//                    {
//                        System.out.println(key + " FOLLOW: " + follow.get(key));
//                    }
//                    System.out.println();
                }
            }
            int cnt = 0;
            for(String key : follow.keySet())
            {
                cnt += follow.get(key).size();
            }
            if(cnt == size)
                break;
            size = cnt;
        }
    }
    private Pair<Boolean,String> Goto(Closure C, Token x)
    {
        Set<LRRule> ret = new TreeSet<>();
        for(LRRule rule: C.items)
        {
            Token token = rule.getHoped();
            if(token == null)
            {
                continue;
            }
            if(token.equals(x))
            {
                LRRule newRule = new LRRule(rule.getHead(),rule.getJustStr(),rule.dot+1);
                ret.add(newRule);
            }
        }
        if(ret.size() != 0)
        {
            Closure D = new Closure(ret,productions);
            int numD;
            if(ClosureSet.containsKey(D))
            {
                numD = ClosureSet.get(D);
                return new Pair<>(false,"s" + numD);
            }
            else {
                numD = num ++;
                ClosureSet.put(D,numD);
                ClosureFind.put(numD,D);
            }
            return new Pair<>(true,"s" + numD);
        }
        return null;
    }
    Stack<Integer> numstk = new Stack();
    public void doParse(ArrayList<String> tokens)
    {
        tokens.add("$");
        Stack<State> stack = new Stack();
        stack.push(new State(new Token("$")));
        stack.push(new State(C0));
        int i = 0;
        while(true)
        {
            State state = stack.peek();
            Token token = new Token(tokens.get(i));
            Integer numC = ClosureSet.get(state.closure);
            String actionStr = table[numC][token.toString().charAt(0)];//ACTION.get(new Pair<>(numC.toString(),token.toString()));
            if(actionStr == null)
            {
                System.out.println("!! Error at '" + token.toString()+"'");
                while(i < tokens.size() && table[numC][tokens.get(i).charAt(0)] == null)
                    i ++;
                if(i < tokens.size())
                {
                    token = new Token(tokens.get(i));
                    actionStr = table[numC][token.toString().charAt(0)];
                }
                else
                    break;
            }
            if(actionStr.contains("r"))//reduce!
            {
                Rule ru = productions.get(Integer.parseInt(actionStr.substring(1)));
                System.out.println("按照" + ru + "规约");
                for(int cnt = 0; cnt < ru.getRest().size()*2;cnt ++)
                    stack.pop();//2|beta|次，因为栈里还有状态

                if(ru.equals("E->E+T"))
                {
                    int tmp = numstk.peek();
                    numstk.pop();
                    numstk.pop();
                    int tmp2 = numstk.peek();
                    numstk.pop();
                    numstk.push(tmp2+tmp);
                }
                if(ru.equals("E->E-T"))
                {
                    int tmp = numstk.peek();
                    numstk.pop();
                    numstk.pop();
                    int tmp2 = numstk.peek();
                    numstk.pop();
                    numstk.push(tmp2-tmp);
                }
                if(ru.equals("T->T/F"))
                {
                    int tmp = numstk.peek();
                    numstk.pop();
                    numstk.pop();
                    int tmp2 = numstk.peek();
                    numstk.pop();
                    numstk.push(tmp2/tmp);
                }
                if(ru.equals("T->T*F"))
                {
                    int tmp = numstk.peek();
                    numstk.pop();
                    numstk.pop();
                    int tmp2 = numstk.peek();
                    numstk.pop();
                    numstk.push(tmp2*tmp);
                }
                State s = stack.peek();
                Token t = new Token(ru.getHead());
                stack.push(new State(t));
                Integer numD = ClosureSet.get(s.closure);
                String gotoStr = table[numD][t.toString().charAt(0)];
                stack.push(new State(ClosureFind.get(Integer.parseInt(gotoStr.substring(1)))));
            }
            else if(actionStr.contains("s"))
            {
                if(i < tokens.size() )
                    i ++;
                System.out.println("移进");
                numstk.push(token.val);
                stack.push(new State(token));
                stack.push(new State(ClosureFind.get(Integer.parseInt(actionStr.substring(1)))));
            }
            else if(actionStr.equals("acc"))
            {
                while(!numstk.isEmpty())
                {
                    System.out.println(".."+numstk.peek());
                    numstk.pop();
                }
                System.out.println("Accept~");
                break;
            }
        }
    }
    private void initTable()
    {
        LRRule begin = new LRRule(productions.get(0).getHead()+"'",productions.get(0).getHead(),0);
        C0 = new Closure(begin,productions);
        Queue<Closure> queue = new PriorityQueue<>();
        queue.offer(C0);
        ClosureSet.put(C0,num);
        ClosureFind.put(num,C0);
        num ++;
        while(!queue.isEmpty())
        {
            Closure C = queue.poll();
            Integer numC = ClosureSet.get(C);
            //System.out.print("C"+ numC +":\n" + C);
            for(Token x : NonTerminals)
            {
                Pair<Boolean,String> p = Goto(C,x);
                if(p != null)
                {
                    String tbi = p.getValue();
                    if(p.getKey())
                    {
                        queue.offer(ClosureFind.get(Integer.parseInt(tbi.substring(1))));
                    }
                    GOTO.put(new Pair<>(numC.toString(),x.toString()),tbi);
                }
            }
            for(Token x: Terminals)
            {
                Pair<Boolean,String> p = Goto(C,x);
                if(p != null)
                {
                    String tbi = p.getValue();
                    if(p.getKey())
                    {
                        queue.offer(ClosureFind.get(Integer.parseInt(tbi.substring(1))));
                    }
                    ACTION.put(new Pair<>(numC.toString(),x.toString()),tbi);
                }
            }
        }
        for(Closure c:ClosureSet.keySet())
        {
            Integer I = ClosureSet.get(c);
            for(LRRule r: c.items)
            {
                int N = getProductionNumber(r);
                if(r.getHoped() == null && !r.getHead().equals(productions.get(0).getHead() + "'"))
                {
                    //System.out.println("==="+r);
                    for(String x:follow.get(r.getHead()))
                    {
                        //System.out.println(I.toString() + " " + x);
                        ACTION.put(new Pair<>(I.toString(),x),"r"+N);
                    }
                }
                else if(r.getHoped() == null && r.getHead().equals(productions.get(0).getHead() +"'"))
                {
                    ACTION.put(new Pair<>(I.toString(),"$"),"acc");
                }
            }
        }
        for(Token x:NonTerminals)
        {
            for(Closure C: ClosureSet.keySet())
            {
                Integer numC = ClosureSet.get(C);
                String tbi = GOTO.get(new Pair<>(numC.toString(),x.toString()));
                table[numC][x.toString().charAt(0)] = tbi;
            }
        }
        for(Token x:Terminals)
        {
            for(Closure C: ClosureSet.keySet())
            {
                Integer numC = ClosureSet.get(C);
                String tbi = ACTION.get(new Pair<>(numC.toString(),x.toString()));
                table[numC][x.toString().charAt(0)] = tbi;
            }
        }
    }
    private int getProductionNumber(LRRule rule)
    {
        for(int i = 0;i < productions.size();i ++)
        {
            if(productions.get(i).getJustStr().equals(rule.getJustStr()))
                return i;
        }
        return -1;
    }
    private void showTable()
    {
        for(int i =0 ;i < 10;i ++)
            System.out.print("-");
        System.out.println();
        System.out.print(" \t");
        for(Token t:Terminals)
            System.out.print(t+"\t");
        for(Token t:NonTerminals)
            System.out.print(t+"\t");
        System.out.println();
        for(int i =0 ;i < ClosureSet.size();i ++) {
            System.out.print(i + "\t");
            for (Token x : Terminals) {
                if (table[i][x.toString().charAt(0)] == null)
                    System.out.print(" \t");
                else System.out.print(table[i][x.toString().charAt(0)] + "\t");
            }
            for (Token x : NonTerminals) {
                if (table[i][x.toString().charAt(0)] == null)
                    System.out.print(" \t");
                else System.out.print(table[i][x.toString().charAt(0)] + "\t");
            }
            System.out.println();
        }
        for(int i =0 ;i < 10;i ++)
            System.out.print("-");
        System.out.println();
    }
    private void showFollow()
    {
        for(String str:follow.keySet())
        {
            System.out.println(str + " "+follow.get(str));
        }
    }
    public SLRParser()
    {
        initProduction(getLinesFromFile(filename));
        for(LRRule production:productions)
        {
            NonTerminals.add(production.getTHead());
            for(Token t : production.getRest())
            {
                if(t.getClaz() == 1)
                {
                    Terminals.add(t);
                }
            }
        }
        Terminals.add(new Token("$"));
        initFirst();
        initNullable();
        initFollow();
        showFollow();
        initTable();
        showTable();
    }

    public static void main(String[] args)
    {
        SLRParser ps = new SLRParser();

    }

}

//get dot 的下一个元素
//dot记录的是数组下标，返回rule.getRest().get(dot)
