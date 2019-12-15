
import com.beans.lexer.Lexer;
import com.beans.parser.LL1Parser;
import com.beans.parser.RecursiveDesParser;
import com.beans.parser.SLRParser;

import java.util.Scanner;

public class Main {
    public static void testRDP(Lexer lexer)
    {
        RecursiveDesParser parser = new RecursiveDesParser(lexer);
        parser.doParse();
    }
    //id id+id
    //(id
    //id++
    public static void testDP(Lexer lexer) throws Exception {
        LL1Parser ps = new LL1Parser();
        for(Integer key:ps.firstS.keySet())
        {
            System.out.println(key + " " + ps.getInstance().get(key).toString()+ " FIRSTS: " + ps.firstS.get(key));
        }

        ps.doParse(lexer.strTokens);
    }
    public static void testSLRParser(Lexer lexer)
    {
        SLRParser ps = new SLRParser();
        ps.doParse(lexer.strTokens);
    }
    //i*(i+i-i)
    //i*i+i
    //(i
    //i++i
    public static void  main(String args[]) throws Exception {
        String str;
        Scanner sc = new Scanner(System.in);
        str = sc.nextLine();
        Lexer lexer = new Lexer(str);
        lexer.doLex();
//        testDP(lexer);
//        testRDP(lexer);
        testSLRParser(lexer);
    }

}
