class Exp_Add extends Exp
{
    Exp left;
    Exp right;
    public Exp_Add(Exp l,Exp r)
    {
        this.kind = Kind.E_ADD;
        this.left = l;
        this.right = r;
    }
}
