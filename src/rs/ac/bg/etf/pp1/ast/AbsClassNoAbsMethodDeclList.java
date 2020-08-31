// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:3


package rs.ac.bg.etf.pp1.ast;

public class AbsClassNoAbsMethodDeclList extends AbsClassMethodDecls {

    private AbsClassMethodDecls AbsClassMethodDecls;
    private MethodDecl MethodDecl;

    public AbsClassNoAbsMethodDeclList (AbsClassMethodDecls AbsClassMethodDecls, MethodDecl MethodDecl) {
        this.AbsClassMethodDecls=AbsClassMethodDecls;
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.setParent(this);
        this.MethodDecl=MethodDecl;
        if(MethodDecl!=null) MethodDecl.setParent(this);
    }

    public AbsClassMethodDecls getAbsClassMethodDecls() {
        return AbsClassMethodDecls;
    }

    public void setAbsClassMethodDecls(AbsClassMethodDecls AbsClassMethodDecls) {
        this.AbsClassMethodDecls=AbsClassMethodDecls;
    }

    public MethodDecl getMethodDecl() {
        return MethodDecl;
    }

    public void setMethodDecl(MethodDecl MethodDecl) {
        this.MethodDecl=MethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.accept(visitor);
        if(MethodDecl!=null) MethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.traverseTopDown(visitor);
        if(MethodDecl!=null) MethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.traverseBottomUp(visitor);
        if(MethodDecl!=null) MethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbsClassNoAbsMethodDeclList(\n");

        if(AbsClassMethodDecls!=null)
            buffer.append(AbsClassMethodDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodDecl!=null)
            buffer.append(MethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbsClassNoAbsMethodDeclList]");
        return buffer.toString();
    }
}
