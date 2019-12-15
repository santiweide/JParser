*  goto  和 closure

```
goto(C,x)
    temp = {}
    foreach(C's item i: A->beta dot x gamma)
        temp ∪={ A->beta dot x gamma}
    return closure(temp)

closure(C)
    while(C is still changing)
        foreach(C's item i:  A->beta dot B gamma)
            C ∪={B->...}

LR(0)
    C0 = closure(S' -> dot S $)
    SET = {C0}
    Q = enQueue(C0)
    while(Q is not empty)
        C = deQueue(Q)
        foreach(X∈(N∪T))
            D = goto(C,x)
        if(x∈T)
            ACTION[C,x] = D
        else GOTO[C,x] = D
        if(D not ∈ SET)
            SET ∪= {D}
            enQueue(D)
```
