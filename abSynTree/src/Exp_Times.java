public class Exp_Times extends Exp
{
    Exp left;
    Exp right;
    public Exp_Times(Exp l,Exp r)
    {
        this.kind = Kind.E_TIMES;
        this.left = l;
        this.right = r;
    }

}
