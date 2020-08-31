// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:3


package rs.ac.bg.etf.pp1.ast;

public class AbsClassAbsMethodDeclList extends AbsClassMethodDecls {

    private AbsClassMethodDecls AbsClassMethodDecls;
    private AbsMethodDecl AbsMethodDecl;

    public AbsClassAbsMethodDeclList (AbsClassMethodDecls AbsClassMethodDecls, AbsMethodDecl AbsMethodDecl) {
        this.AbsClassMethodDecls=AbsClassMethodDecls;
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.setParent(this);
        this.AbsMethodDecl=AbsMethodDecl;
        if(AbsMethodDecl!=null) AbsMethodDecl.setParent(this);
    }

    public AbsClassMethodDecls getAbsClassMethodDecls() {
        return AbsClassMethodDecls;
    }

    public void setAbsClassMethodDecls(AbsClassMethodDecls AbsClassMethodDecls) {
        this.AbsClassMethodDecls=AbsClassMethodDecls;
    }

    public AbsMethodDecl getAbsMethodDecl() {
        return AbsMethodDecl;
    }

    public void setAbsMethodDecl(AbsMethodDecl AbsMethodDecl) {
        this.AbsMethodDecl=AbsMethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.accept(visitor);
        if(AbsMethodDecl!=null) AbsMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.traverseTopDown(visitor);
        if(AbsMethodDecl!=null) AbsMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.traverseBottomUp(visitor);
        if(AbsMethodDecl!=null) AbsMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbsClassAbsMethodDeclList(\n");

        if(AbsClassMethodDecls!=null)
            buffer.append(AbsClassMethodDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AbsMethodDecl!=null)
            buffer.append(AbsMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbsClassAbsMethodDeclList]");
        return buffer.toString();
    }
}
