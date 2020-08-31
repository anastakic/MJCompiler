// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:3


package rs.ac.bg.etf.pp1.ast;

public class VardClassSquares extends Vard {

    private String VarName;

    public VardClassSquares (String VarName) {
        this.VarName=VarName;
    }

    public String getVarName() {
        return VarName;
    }

    public void setVarName(String VarName) {
        this.VarName=VarName;
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
        buffer.append("VardClassSquares(\n");

        buffer.append(" "+tab+VarName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VardClassSquares]");
        return buffer.toString();
    }
}
