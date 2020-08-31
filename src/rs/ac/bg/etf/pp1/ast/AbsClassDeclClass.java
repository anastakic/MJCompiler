// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:3


package rs.ac.bg.etf.pp1.ast;

public class AbsClassDeclClass extends DeclList {

    private AbsClassDecl AbsClassDecl;

    public AbsClassDeclClass (AbsClassDecl AbsClassDecl) {
        this.AbsClassDecl=AbsClassDecl;
        if(AbsClassDecl!=null) AbsClassDecl.setParent(this);
    }

    public AbsClassDecl getAbsClassDecl() {
        return AbsClassDecl;
    }

    public void setAbsClassDecl(AbsClassDecl AbsClassDecl) {
        this.AbsClassDecl=AbsClassDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbsClassDecl!=null) AbsClassDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbsClassDecl!=null) AbsClassDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbsClassDecl!=null) AbsClassDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbsClassDeclClass(\n");

        if(AbsClassDecl!=null)
            buffer.append(AbsClassDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbsClassDeclClass]");
        return buffer.toString();
    }
}
