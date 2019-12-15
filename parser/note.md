# 二、语法分析
语法分析是词法分析的下一阶段。有递归下降分析和非递归下降分析。

## 2.1非递归预测分析

LL(1)文法：
* 第一个L是从左向右读程序的意思；

* 第二个L是最左推导的意思：推到的过程中，每次总是选择最左面的最替换；

* (1)表示采用一个前看符号。前看符号是用来辅助判断的。

* 线性时间，分析高效，没有回溯

* 基本思想：表驱动分析

回顾：自顶向下分析算法

    tokens={}
    i=0
    stack = [s]
    while(stack != [])
        if(stack[top] is a terminal t)
            if(t == tokens[i++])
                pop()
            else backtrace();
        else if (stack[top] is a nonterminal T)
            pop();
            push(the next right hand side of T)
            
剪枝策略：
  
       push(the next right hand side of T)  
        == push(the correct right hand side of T)
        == push(what in table[N,T])
   
   这样就不用回溯了！！
   考虑一个LL(1)分析表，他横着是所有的终结符，竖着是所有的非终结符。
   构造的时候，逐个扫描每一个derive表达式k:N->q1,...,qn, 辅助集合FIRST={x1,x2,...,xn}，那么就把k写到 table[N,xi]上
   
* LL(1) 分析表中的冲突
如果按照上述方法生成，并且表中每个表项中最多只有一个元素的话，就是一个LL(1)文法。如果表项不止一个就是LL(1)冲突，这种就不是LL(1)文法。
就是说，如果N->q,N->p,并且FIRST(q)∩FIRST(p)!=空集，就会有表项冲突。


### 2.1.1 First集合的不动点算法：
 近似定义：
        First(N)集合  从非终结符N出发开始推导得出的句子开头所有可能终结符的集合

    foreach (nonterminal N)
            FIRST(N) = {}

    while(some set is changing)
        foreach (production p: N-> q1,q2,...,qn
            if(q1 == a...)
                FIRST(N) ∪ ={a}
            if(q1 == M...)
                FIRST(N) ∪ = FIRST(M)
完整定义：
    基本情况
        X->a,FIRST(X)∪={a}
    归纳情况
        X->Y1Y2...Yn,FIRST(X)∪=FIRST(Y1),
        如果FIRST(Y1)属于NULLABLE，FIRST(X)∪=FIRST(Y2)
        如果Y1Y属于NULLABLE，FIRST(X)∪=FIRST(Y3)
        ......

    foreach (nonterminal N)
        FIRST(N) = {}
    while(some set is changing)
        foreach (production p: N-> q1,q2,...,qn
            if(q1 == a...)
                FIRST(N) ∪ ={a}
            if(q1 == M...)
                FIRST(N) ∪ = FIRST(M)
                if(M is not in NULLABLE)
                    break;
 ### 2.1.2 NULLABLE集合算法
 当我们想看那些集合可以推出一个空串，就看他是不是NULLABLE集合里面的人。
 定义：
        非终结符X属于NULLABLE集合，当且仅当：
        基本情况X->空串或者归纳情况X->Y1...Yn,Yi为非终结符并且都属于NULLABLE集合
 
     NULLABLE = {}    
     while(nullable is still changing)
        foreach (production p: X-> q)
            if(q == 空)
                NULLABLE ∪={X}
            if(q == Y1,...,Yn)
                if(Y1属于NULLABLE && ... && Yn 属于NULLABLE)
                    NULLABLE ∪={X}
                    
 ### FOLLOW集合计算算法
 看每一个非终结符后面可能跟着啥符号
 
    foreach(nonterminal N)
        FOLLOW(N) = {}
    while(some set is changing)
        tmp = FOLLOW(N)
        foreach(qi from qn down to q1)//逆序！！
            if(qi == a...)
                tmp = {a}
            if(qi == M...)
                FOLLOW(M) ∪=tmp
                if(M is not NULLABLE)
                    tmp = FIRST(M)
                    else tmp ∪=FIRST(M)
                    
### 计算FIRST_s集合
这个FIRST_S是FIRST推广到语句(串），是针对每一个产生式的右部而言的

    foreach(production p)
        FIRST_S(p) = {}
    calculate_FIEST_S(production p:N->q1,...,qn)
        foreach(qi from q1 to qn)
            if(qi == a...)
                FIRST_S(P) ∪={a}
                return
            if(qi == M...)
                FIRST_S(P) ∪=FIRST(M)
                    if(M is not NULLABLE)
                        return
        FIRST_S(P) ∪= FOLLOW(N)
 
 
 啊啊