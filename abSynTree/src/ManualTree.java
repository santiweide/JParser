import sun.reflect.annotation.ExceptionProxy;

public class ManualTree {
    public void test()
    {
        //   E->n|E+E|E*E
        //手工构造抽象语法树~~可以分析整型数的第一个域判断Exp的类型ww
        Exp e1 = new Exp_Int(2);
        Exp e2 = new Exp_Int(3);
        Exp e3 = new Exp_Int(4);
        Exp e4 = new Exp_Times(e2,e3);
        Exp e5 = new Exp_Add(e1,e4);
        pretty_print(e5);
    }
    public void pretty_print(Exp e)
    {
        if(e.kind == Kind.E_INT)
        {
            System.out.print(((Exp_Int)e).n);
        }
        else if(e.kind == Kind.E_ADD)
        {
            System.out.print("(");
            pretty_print(((Exp_Add)e).left);
            System.out.print(") + (");
            pretty_print(((Exp_Add)e).right);
            System.out.print(")");
            return;
        }
        else if(e.kind == Kind.E_TIMES)
        {
            System.out.print("(");
            pretty_print(((Exp_Times)e).left);
            System.out.print(") + (");
            pretty_print(((Exp_Times)e).right);
            System.out.print(")");
            return;
        }

    }
    public Type checkExp(Exp e)throws Exception
    {
        if(e.kind == Kind.E_INT)
        {
            return Type.INT;
        }else if(e.kind == Kind.E_ADD)
        {
            Type t1 = checkExp(((Exp_Add)e).left);
            Type t2 = checkExp(((Exp_Add)e).right);
            if(t1 != Type.INT && t2 != Type.INT)
            {
                throw new Exception("type mismatch!!");
            }else
            {
                return Type.INT;
            }
        }else if(e.kind == Kind.E_TIMES)
        {
            Type t1 = checkExp(((Exp_Times)e).left);
            Type t2 = checkExp(((Exp_Times)e).right);
            if(t1 != Type.INT && t2 != Type.INT)
            {
                throw new Exception("type mismatch!!");
            }else
            {
                return Type.INT;
            }
        }
        return null;
    }
}
