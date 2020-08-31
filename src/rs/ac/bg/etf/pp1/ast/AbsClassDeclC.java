// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:3


package rs.ac.bg.etf.pp1.ast;

public class AbsClassDeclC extends AbsClassDecl {

    private AbsClassName AbsClassName;
    private Extends Extends;
    private VarDeclListC VarDeclListC;
    private AbsClassMethodDecl AbsClassMethodDecl;

    public AbsClassDeclC (AbsClassName AbsClassName, Extends Extends, VarDeclListC VarDeclListC, AbsClassMethodDecl AbsClassMethodDecl) {
        this.AbsClassName=AbsClassName;
        if(AbsClassName!=null) AbsClassName.setParent(this);
        this.Extends=Extends;
        if(Extends!=null) Extends.setParent(this);
        this.VarDeclListC=VarDeclListC;
        if(VarDeclListC!=null) VarDeclListC.setParent(this);
        this.AbsClassMethodDecl=AbsClassMethodDecl;
        if(AbsClassMethodDecl!=null) AbsClassMethodDecl.setParent(this);
    }

    public AbsClassName getAbsClassName() {
        return AbsClassName;
    }

    public void setAbsClassName(AbsClassName AbsClassName) {
        this.AbsClassName=AbsClassName;
    }

    public Extends getExtends() {
        return Extends;
    }

    public void setExtends(Extends Extends) {
        this.Extends=Extends;
    }

    public VarDeclListC getVarDeclListC() {
        return VarDeclListC;
    }

    public void setVarDeclListC(VarDeclListC VarDeclListC) {
        this.VarDeclListC=VarDeclListC;
    }

    public AbsClassMethodDecl getAbsClassMethodDecl() {
        return AbsClassMethodDecl;
    }

    public void setAbsClassMethodDecl(AbsClassMethodDecl AbsClassMethodDecl) {
        this.AbsClassMethodDecl=AbsClassMethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbsClassName!=null) AbsClassName.accept(visitor);
        if(Extends!=null) Extends.accept(visitor);
        if(VarDeclListC!=null) VarDeclListC.accept(visitor);
        if(AbsClassMethodDecl!=null) AbsClassMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbsClassName!=null) AbsClassName.traverseTopDown(visitor);
        if(Extends!=null) Extends.traverseTopDown(visitor);
        if(VarDeclListC!=null) VarDeclListC.traverseTopDown(visitor);
        if(AbsClassMethodDecl!=null) AbsClassMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbsClassName!=null) AbsClassName.traverseBottomUp(visitor);
        if(Extends!=null) Extends.traverseBottomUp(visitor);
        if(VarDeclListC!=null) VarDeclListC.traverseBottomUp(visitor);
        if(AbsClassMethodDecl!=null) AbsClassMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AbsClassDeclC(\n");

        if(AbsClassName!=null)
            buffer.append(AbsClassName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Extends!=null)
            buffer.append(Extends.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclListC!=null)
            buffer.append(VarDeclListC.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AbsClassMethodDecl!=null)
            buffer.append(AbsClassMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AbsClassDeclC]");
        return buffer.toString();
    }
}
