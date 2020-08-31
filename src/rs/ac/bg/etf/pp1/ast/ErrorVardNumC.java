// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:3


package rs.ac.bg.etf.pp1.ast;

public class ErrorVardNumC extends Vard {

    private Integer N1;
    private ErrorVardNum ErrorVardNum;

    public ErrorVardNumC (Integer N1, ErrorVardNum ErrorVardNum) {
        this.N1=N1;
        this.ErrorVardNum=ErrorVardNum;
        if(ErrorVardNum!=null) ErrorVardNum.setParent(this);
    }

    public Integer getN1() {
        return N1;
    }

    public void setN1(Integer N1) {
        this.N1=N1;
    }

    public ErrorVardNum getErrorVardNum() {
        return ErrorVardNum;
    }

    public void setErrorVardNum(ErrorVardNum ErrorVardNum) {
        this.ErrorVardNum=ErrorVardNum;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ErrorVardNum!=null) ErrorVardNum.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ErrorVardNum!=null) ErrorVardNum.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ErrorVardNum!=null) ErrorVardNum.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ErrorVardNumC(\n");

        buffer.append(" "+tab+N1);
        buffer.append("\n");

        if(ErrorVardNum!=null)
            buffer.append(ErrorVardNum.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ErrorVardNumC]");
        return buffer.toString();
    }
}
