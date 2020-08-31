// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:3


package rs.ac.bg.etf.pp1.ast;

public class AbsClassMethodDeclClass extends AbsClassMethodDecl {

    private AbsClassMethodDecls AbsClassMethodDecls;

    public AbsClassMethodDeclClass (AbsClassMethodDecls AbsClassMethodDecls) {
        this.AbsClassMethodDecls=AbsClassMethodDecls;
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.setParent(this);
    }

    public AbsClassMethodDecls getAbsClassMethodDecls() {
        return AbsClassMethodDecls;
    }

    public void setAbsClassMethodDecls(AbsClassMethodDecls AbsClassMethodDecls) {
        this.AbsClassMethodDecls=AbsClassMethodDecls;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbsClassMethodDecls!=null) AbsClassMethodDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbsClassMethodDeclClass(\n");

        if(AbsClassMethodDecls!=null)
            buffer.append(AbsClassMethodDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbsClassMethodDeclClass]");
        return buffer.toString();
    }
}
