// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:3


package rs.ac.bg.etf.pp1.ast;

public class ErrorAbsMethodDecl extends AbsMethodDecl {

    public ErrorAbsMethodDecl () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ErrorAbsMethodDecl(\n");

        buffer.append(tab);
        buffer.append(") [ErrorAbsMethodDecl]");
        return buffer.toString();
    }
}
