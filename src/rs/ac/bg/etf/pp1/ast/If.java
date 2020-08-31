// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:4


package rs.ac.bg.etf.pp1.ast;

public class If extends IfStatement {

    private IfStart IfStart;
    private ConditionIf ConditionIf;
    private IfStmtJmp IfStmtJmp;
    private IfStmtEnd IfStmtEnd;

    public If (IfStart IfStart, ConditionIf ConditionIf, IfStmtJmp IfStmtJmp, IfStmtEnd IfStmtEnd) {
        this.IfStart=IfStart;
        if(IfStart!=null) IfStart.setParent(this);
        this.ConditionIf=ConditionIf;
        if(ConditionIf!=null) ConditionIf.setParent(this);
        this.IfStmtJmp=IfStmtJmp;
        if(IfStmtJmp!=null) IfStmtJmp.setParent(this);
        this.IfStmtEnd=IfStmtEnd;
        if(IfStmtEnd!=null) IfStmtEnd.setParent(this);
    }

    public IfStart getIfStart() {
        return IfStart;
    }

    public void setIfStart(IfStart IfStart) {
        this.IfStart=IfStart;
    }

    public ConditionIf getConditionIf() {
        return ConditionIf;
    }

    public void setConditionIf(ConditionIf ConditionIf) {
        this.ConditionIf=ConditionIf;
    }

    public IfStmtJmp getIfStmtJmp() {
        return IfStmtJmp;
    }

    public void setIfStmtJmp(IfStmtJmp IfStmtJmp) {
        this.IfStmtJmp=IfStmtJmp;
    }

    public IfStmtEnd getIfStmtEnd() {
        return IfStmtEnd;
    }

    public void setIfStmtEnd(IfStmtEnd IfStmtEnd) {
        this.IfStmtEnd=IfStmtEnd;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(IfStart!=null) IfStart.accept(visitor);
        if(ConditionIf!=null) ConditionIf.accept(visitor);
        if(IfStmtJmp!=null) IfStmtJmp.accept(visitor);
        if(IfStmtEnd!=null) IfStmtEnd.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IfStart!=null) IfStart.traverseTopDown(visitor);
        if(ConditionIf!=null) ConditionIf.traverseTopDown(visitor);
        if(IfStmtJmp!=null) IfStmtJmp.traverseTopDown(visitor);
        if(IfStmtEnd!=null) IfStmtEnd.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IfStart!=null) IfStart.traverseBottomUp(visitor);
        if(ConditionIf!=null) ConditionIf.traverseBottomUp(visitor);
        if(IfStmtJmp!=null) IfStmtJmp.traverseBottomUp(visitor);
        if(IfStmtEnd!=null) IfStmtEnd.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("If(\n");

        if(IfStart!=null)
            buffer.append(IfStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConditionIf!=null)
            buffer.append(ConditionIf.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(IfStmtJmp!=null)
            buffer.append(IfStmtJmp.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(IfStmtEnd!=null)
            buffer.append(IfStmtEnd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [If]");
        return buffer.toString();
    }
}
