package com.beans.parser;

import com.beans.lexer.Lexer;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class LL1Parser {

    private LinkedHashMap<String, TreeSet<String>> first = new LinkedHashMap<>();
    private LinkedHashMap<String,TreeSet<String> > follow = new LinkedHashMap<>();
    public LinkedHashMap<Integer,TreeSet<String> > firstS = new LinkedHashMap<>();
    private TreeSet<String> nullable = new TreeSet<>();
    private static ArrayList<Rule> productions = new ArrayList<>();
    public  ArrayList<Rule> getInstance()
    {
        return productions;
    }
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
    public void initProduction(String[] lines)
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
                productions.add(new Rule(l,r));
            }
            else
            {
                String[] rs = r.split("[|]");
                for(int j = rs.length - 1;j >= 0; j --)
                {
                    productions.add(new Rule(l,rs[j]));
                }
            }
        }
    }
    public void initNullable()
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
    public void initFirst()
    {
        for(int i =0;i < productions.size();i ++)
        {
            System.out.println(productions.get(i));
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
                //System.out.println(key +" : "+first.get(key));
                cnt += first.get(key).size();
            }
            if(cnt == size)
                break;
            size = cnt;
        }
    }

    public void initFollow()
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
    public void initFirstS()
    {
        for(int i =0 ;i <productions.size();i ++)
        {
            firstS.put(i,new TreeSet<>());
        }
        for(int i =0 ; i < productions.size();i ++)
        {
            Rule r = productions.get(i);
            TreeSet<String> tmp = new TreeSet();
            for(String key:firstS.get(i))
            {
                tmp.add(key);
            }
            boolean getTail = true;
            for(int j = r.getRest().size() - 1;j >= 0;j --)
            {
                Token M = r.getRest().get(j);
                if(M.getClaz() == 1)
                {
                    if(!M.equals("ε"))
                    {
                        tmp.add(M.toString());
                        firstS.put(i,tmp);
                        getTail = false;
                        break;
                    }
                }else
                {
                    for(String key:first.get(M.toString()))
                    {
                        if(!key.contentEquals("ε"))
                            tmp.add(key);
                    }
                    firstS.put(i,tmp);
                    if(!nullable.contains(M.toString()))
                    {
                        getTail = false;
                        break;
                    }
                }
            }
            if(getTail)
            {
                for(String key:follow.get(r.getHead()))
                    tmp.add(key);
                firstS.put(i,tmp);
            }
        }
    }

    HashMap<Pair<String,String>,Integer> table = new HashMap<>();
    public void initTable()
    {
        for(int i =0 ;i < productions.size();i ++)
        {
            for(String key:firstS.get(i))
            {
                Pair<String,String> tmp = new Pair<>(productions.get(i).getHead(),key);
                table.put(tmp,i);
            }
        }
    }

    public void doParse(ArrayList<String> tokens) throws Exception {
        int i =0 ;
        Stack<Token> stack = new Stack<>();
        stack.push(productions.get(0).getTHead());
        while(!stack.isEmpty() && i < tokens.size())
        {
            Token top = stack.peek();
            if(top.getClaz() == 1)
            {
                if(top.equals("ε"))
                {
                    stack.pop();
                }else if(top.equals(tokens.get(i)) )
                {
                    i ++;
                    stack.pop();
                }else
                {
                    throw new Exception("错误发生！" + "当前只能匹配 "+top.toString() +" , 但是希望匹配"+tokens.get(i));
                }
            }else if((top.getClaz() == 0))
            {
                stack.pop();
                Integer num = table.get(new Pair(top.toString(),tokens.get(i)));
                if(num != null)
                {
                    System.out.println("..." + productions.get(num));
                    Rule r = productions.get(num);
                    for(int j = r.getRest().size() - 1;j >= 0;j --)
                        stack.push(r.getRest().get(j));
                }else
                {
                    throw new Exception("弹栈，弹出非终结符"+top.toString()+ ", \""+ tokens.get(i)+"\" 无法匹配");
                }
            }
        }
        while(!stack.empty())
        {
            Token token = stack.peek();
            if(nullable.contains(token.toString()))
            {
                System.out.println("..." + token.toString()+"->ε");
               stack.pop();
            }else
            {
                System.out.println("Error!存在无法识别的表达式！");
                break;
            }
        }
    }
    public LL1Parser()
    {
        initProduction(getLinesFromFile("input.txt"));
        initNullable();
        initFirst();
        initFollow();
        initFirstS();
        initTable();
    }

    public static void main(String[] args)
    {
        LL1Parser ps = new LL1Parser();
        for(String key : ps.first.keySet())
        {
            System.out.println(key +" FIRST: "+ ps.first.get(key));
        }
        System.out.println();
        for(String key:ps.follow.keySet())
        {
            System.out.println(key + " FOLLOW: " + ps.follow.get(key));
        }
        System.out.println();
        for(Integer key:ps.firstS.keySet())
        {
            System.out.println(key + " " + productions.get(key).toString()+ " FIRSTS: " + ps.firstS.get(key));
        }

    }

}
