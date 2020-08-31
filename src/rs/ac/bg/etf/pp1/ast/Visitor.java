// generated with ast extension for cup
// version 0.8
// 2/1/2020 21:2:4


package rs.ac.bg.etf.pp1.ast;

public interface Visitor { 

    public void visit(ActParsOpt ActParsOpt);
    public void visit(FormPars FormPars);
    public void visit(DesignatorSufixList DesignatorSufixList);
    public void visit(IfStmtEnd IfStmtEnd);
    public void visit(DesignatorOrError DesignatorOrError);
    public void visit(Factor Factor);
    public void visit(Statement Statement);
    public void visit(MethodDecl MethodDecl);
    public void visit(ClassMethods ClassMethods);
    public void visit(ConstDeclList ConstDeclList);
    public void visit(DesignatorStatementOpt DesignatorStatementOpt);
    public void visit(ExprList ExprList);
    public void visit(VardC VardC);
    public void visit(Vard Vard);
    public void visit(VarDecls VarDecls);
    public void visit(Relop Relop);
    public void visit(FormalParamList FormalParamList);
    public void visit(DeclList DeclList);
    public void visit(CondFactList CondFactList);
    public void visit(VarListC VarListC);
    public void visit(Expr Expr);
    public void visit(ConditionOpt ConditionOpt);
    public void visit(MulopFactorList MulopFactorList);
    public void visit(AbsClassMethodDecls AbsClassMethodDecls);
    public void visit(AddopTermList AddopTermList);
    public void visit(MethodTypeName MethodTypeName);
    public void visit(NumConstOpt NumConstOpt);
    public void visit(DesignatorSufix DesignatorSufix);
    public void visit(ForDeclEnd ForDeclEnd);
    public void visit(TypeList TypeList);
    public void visit(DeclLists DeclLists);
    public void visit(Condition Condition);
    public void visit(Mulop Mulop);
    public void visit(AbsClassName AbsClassName);
    public void visit(DesignatorStatement DesignatorStatement);
    public void visit(ClassName ClassName);
    public void visit(ElseStmtEnd ElseStmtEnd);
    public void visit(Addop Addop);
    public void visit(StatementList StatementList);
    public void visit(ConstDecl ConstDecl);
    public void visit(CondTermList CondTermList);
    public void visit(AbsClassMethodDecl AbsClassMethodDecl);
    public void visit(CondTerm CondTerm);
    public void visit(FormalParamDecl FormalParamDecl);
    public void visit(AbsClassDecl AbsClassDecl);
    public void visit(VarList VarList);
    public void visit(IfStatement IfStatement);
    public void visit(AbsMethodDecl AbsMethodDecl);
    public void visit(MethodDeclList MethodDeclList);
    public void visit(Extends Extends);
    public void visit(VarDeclList VarDeclList);
    public void visit(CondFact CondFact);
    public void visit(ErrorVardNum ErrorVardNum);
    public void visit(ConstVar ConstVar);
    public void visit(ConstDecls ConstDecls);
    public void visit(VarDeclsC VarDeclsC);
    public void visit(ConditionIf ConditionIf);
    public void visit(VarDeclListC VarDeclListC);
    public void visit(Mod Mod);
    public void visit(Div Div);
    public void visit(Mul Mul);
    public void visit(Minus Minus);
    public void visit(Plus Plus);
    public void visit(Lte Lte);
    public void visit(Lt Lt);
    public void visit(Gte Gte);
    public void visit(Gt Gt);
    public void visit(Neq Neq);
    public void visit(Eq Eq);
    public void visit(DesignatorSufixSquare DesignatorSufixSquare);
    public void visit(DesignatorSufixDot DesignatorSufixDot);
    public void visit(NoDesignatorSufixList NoDesignatorSufixList);
    public void visit(DesignatorSufixListClass DesignatorSufixListClass);
    public void visit(DesignatorName DesignatorName);
    public void visit(Designator Designator);
    public void visit(NewType NewType);
    public void visit(NullFact NullFact);
    public void visit(ExprFact ExprFact);
    public void visit(NewFactSquare NewFactSquare);
    public void visit(NewFact NewFact);
    public void visit(BoolFact BoolFact);
    public void visit(CharFact CharFact);
    public void visit(NumFact NumFact);
    public void visit(FactorFuncCall FactorFuncCall);
    public void visit(DesignatorFact DesignatorFact);
    public void visit(NoMulopFactorList NoMulopFactorList);
    public void visit(MulopFactorListClass MulopFactorListClass);
    public void visit(Term Term);
    public void visit(NoAddopTermList NoAddopTermList);
    public void visit(AddopTermListClass AddopTermListClass);
    public void visit(MinusExpr MinusExpr);
    public void visit(ExprMinus ExprMinus);
    public void visit(ExprNoMinus ExprNoMinus);
    public void visit(CondFactExprRelopExpr CondFactExprRelopExpr);
    public void visit(CondFactExpr CondFactExpr);
    public void visit(LogicalAnd LogicalAnd);
    public void visit(CondTermNode CondTermNode);
    public void visit(CondTermC CondTermC);
    public void visit(LogicalOr LogicalOr);
    public void visit(ConditionNode ConditionNode);
    public void visit(ConditionOrNode ConditionOrNode);
    public void visit(SingleExpr SingleExpr);
    public void visit(ExprListClass ExprListClass);
    public void visit(ActPars ActPars);
    public void visit(NoActParsOpt NoActParsOpt);
    public void visit(ActParsOptClass ActParsOptClass);
    public void visit(ErrorAssign ErrorAssign);
    public void visit(Assign Assign);
    public void visit(FuncName FuncName);
    public void visit(DesignatorStmtModif DesignatorStmtModif);
    public void visit(DesignatorStmtSqrt DesignatorStmtSqrt);
    public void visit(DesignatorStmtAssign DesignatorStmtAssign);
    public void visit(DesignatorStmtMinus2 DesignatorStmtMinus2);
    public void visit(DesignatorStmtPlus2 DesignatorStmtPlus2);
    public void visit(DesignatorStmtFunc DesignatorStmtFunc);
    public void visit(NoNumConstOpt NoNumConstOpt);
    public void visit(NumConstOptClass NumConstOptClass);
    public void visit(NoDesignatorStmtOpt NoDesignatorStmtOpt);
    public void visit(DesignatorStmtOpt DesignatorStmtOpt);
    public void visit(ErrorConditionFOR ErrorConditionFOR);
    public void visit(NoConditionOpt NoConditionOpt);
    public void visit(ConditionOptClass ConditionOptClass);
    public void visit(ForDeclarationEnd ForDeclarationEnd);
    public void visit(ForHeader ForHeader);
    public void visit(ForStatement ForStatement);
    public void visit(ConditionIfClass ConditionIfClass);
    public void visit(ElseStatementEnd ElseStatementEnd);
    public void visit(ElseStart ElseStart);
    public void visit(ElseStmt ElseStmt);
    public void visit(IfStatementEnd IfStatementEnd);
    public void visit(IfStmtJmp IfStmtJmp);
    public void visit(IfStart IfStart);
    public void visit(IfElse IfElse);
    public void visit(If If);
    public void visit(ListStmt ListStmt);
    public void visit(PrintStmt PrintStmt);
    public void visit(ReadStmt ReadStmt);
    public void visit(ReturnVoid ReturnVoid);
    public void visit(ReturnSmt ReturnSmt);
    public void visit(ContinueStmt ContinueStmt);
    public void visit(BreakStmt BreakStmt);
    public void visit(ForStmt ForStmt);
    public void visit(IfStmt IfStmt);
    public void visit(DesignatorStmt DesignatorStmt);
    public void visit(NoStmt NoStmt);
    public void visit(Statements Statements);
    public void visit(ErrorFormalParam ErrorFormalParam);
    public void visit(FormalParamSquares FormalParamSquares);
    public void visit(FormalParam FormalParam);
    public void visit(SingleFormalParamDecl SingleFormalParamDecl);
    public void visit(FormalParamDecls FormalParamDecls);
    public void visit(NoFormParam NoFormParam);
    public void visit(FormParams FormParams);
    public void visit(MethodTypeNameWithVoid MethodTypeNameWithVoid);
    public void visit(MethodTypeNameWithType MethodTypeNameWithType);
    public void visit(MethodDeclClass MethodDeclClass);
    public void visit(NoMethodDecl NoMethodDecl);
    public void visit(MethodDeclarations MethodDeclarations);
    public void visit(Type Type);
    public void visit(NoVarDeclListC NoVarDeclListC);
    public void visit(VarDeclListCClass VarDeclListCClass);
    public void visit(ErrorAbsMethodDecl ErrorAbsMethodDecl);
    public void visit(AbsMethodDeclClass AbsMethodDeclClass);
    public void visit(NoAbsClassAbsMethodDeclList NoAbsClassAbsMethodDeclList);
    public void visit(AbsClassAbsMethodDeclList AbsClassAbsMethodDeclList);
    public void visit(AbsClassNoAbsMethodDeclList AbsClassNoAbsMethodDeclList);
    public void visit(NoAbsClassMethods NoAbsClassMethods);
    public void visit(AbsClassMethodDeclClass AbsClassMethodDeclClass);
    public void visit(AbsClassNameC AbsClassNameC);
    public void visit(ErrorExtends ErrorExtends);
    public void visit(NoExtends NoExtends);
    public void visit(ExtendsClass ExtendsClass);
    public void visit(AbsClassDeclC AbsClassDeclC);
    public void visit(NoClassMethods NoClassMethods);
    public void visit(ClassMethodsClass ClassMethodsClass);
    public void visit(SingleTypeList SingleTypeList);
    public void visit(TypeListClass TypeListClass);
    public void visit(VardCSquares VardCSquares);
    public void visit(VardCClass VardCClass);
    public void visit(SingleVardListC SingleVardListC);
    public void visit(VardListC VardListC);
    public void visit(VardDeclsC VardDeclsC);
    public void visit(ErrorClassName ErrorClassName);
    public void visit(ClassNameC ClassNameC);
    public void visit(ClassDecl ClassDecl);
    public void visit(ErrorVardNumClass ErrorVardNumClass);
    public void visit(ErrorVardNumC ErrorVardNumC);
    public void visit(VardClassSquares VardClassSquares);
    public void visit(VardClass VardClass);
    public void visit(SingleVarClass SingleVarClass);
    public void visit(VarListClass VarListClass);
    public void visit(TypeVar TypeVar);
    public void visit(SingleVarDecls SingleVarDecls);
    public void visit(NoVarDeclList NoVarDeclList);
    public void visit(VarDeclListClass VarDeclListClass);
    public void visit(BoolConstClass BoolConstClass);
    public void visit(CharConstClass CharConstClass);
    public void visit(NumConstClass NumConstClass);
    public void visit(SingleConstDecl SingleConstDecl);
    public void visit(SingleConstDeclListClass SingleConstDeclListClass);
    public void visit(ConstDeclListClass ConstDeclListClass);
    public void visit(SingleConstDecls SingleConstDecls);
    public void visit(AbsClassDeclClass AbsClassDeclClass);
    public void visit(ClassDeclClass ClassDeclClass);
    public void visit(VarDeclsClass VarDeclsClass);
    public void visit(ConstDeclsClass ConstDeclsClass);
    public void visit(NoDeclListClass NoDeclListClass);
    public void visit(DeclListsClass DeclListsClass);
    public void visit(ProgName ProgName);
    public void visit(Program Program);

}
