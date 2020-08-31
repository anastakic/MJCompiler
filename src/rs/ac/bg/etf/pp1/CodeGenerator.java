package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {
	
	
	/////////////////// PRIVATE VARIABLES ///////////////////
	private int mainPc;
	private int dataSize;
	private boolean returnFound = false;
	private int forDesOpt = 0;
	
	private ArrayList<Obj> currentDesignator = new ArrayList<>();
	private ArrayList<Integer> adr = new ArrayList<>();
	private ArrayList<Integer> forAdrUpCond = new ArrayList<>();
	private ArrayList<Integer> forAdrUpInkr = new ArrayList<>();
	private ArrayList<Integer> forAdrFixupTrue = new ArrayList<>();
	private ArrayList<Integer> forAdrFixupFalse = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> breakList = new ArrayList<>();
	
	private ArrayList<ArrayList<Integer>> condAdr = new ArrayList<>();
	
	/////////////////// HELPER FUNCTIONS ///////////////////
	
	private void designatorCond(DesignatorStatementOpt node) {
		if (forDesOpt == 0) {	
			forAdrUpCond.add(Code.pc);
		}
		else {	
			Code.putJump(forAdrUpCond.remove(forAdrUpCond.size()-1));
			Code.fixup(forAdrFixupTrue.remove(forAdrFixupTrue.size()-1));
		}
		
		forDesOpt++;
		
		forDesOpt %= 2;
	}
	
	public int getMainPc() {
		return this.mainPc;
	}

	public int getDataSize() {
		return this.dataSize;
	}

	public void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}
	
	private Obj getCurrDes() {
		return currentDesignator.get(currentDesignator.size()-1);
	}
	
	private void resetCurrDes(Obj obj) {
		currentDesignator.remove(currentDesignator.size()-1);
		currentDesignator.add(obj);
	}
	
	private boolean ParentRead(SyntaxNode node) {
		SyntaxNode parent = node.getParent();
		while (parent != null) {
			if (parent instanceof ReadStmt)
				return true;
			if (parent instanceof DesignatorSufixSquare)
				return false;
			
			node = node.getParent();
			parent = node.getParent();
		}
		return false;

	}
	
	private boolean ParentAssign(SyntaxNode node) {
		SyntaxNode parent = node.getParent();
		while (parent != null) {
			if (parent instanceof DesignatorSufixSquare)
				return false;
			if (parent instanceof Assign && ((Assign)parent).getDesignator() == node)
				return true;
			node = node.getParent();
			parent = node.getParent();
		}
		return false;
	}
	
	private boolean Plus2Minus2(SyntaxNode node) {
		SyntaxNode parent = node.getParent();
		while (parent != null) {
			if (parent instanceof DesignatorSufixSquare)
				return false;
			if (parent instanceof DesignatorStmtMinus2 || parent instanceof DesignatorStmtPlus2)
				return true;
			node = node.getParent();
			parent = node.getParent();
		}
		return false;
	}
	
	
	
	/////////////////////////////////////////////////
	/////////////////// OVERRIDE ////////////////////
	/////////////////////////////////////////////////
	
	
	/////////////////// CONSTANTS ///////////////////
	public void visit(NullFact nullFactor) {
		Obj constant = new Obj(Obj.Con, "constant", nullFactor.struct, 0, 0);
		Code.load(constant);
	
	}

	public void visit(NumFact numFactor) {
		Obj constant = new Obj(Obj.Con, "constant", numFactor.struct);
		constant.setAdr(numFactor.getN1());
		Code.load(constant);
	}
	
	public void visit(CharFact charFactor) {
		Obj constant = new Obj(Obj.Con, "constant", charFactor.struct);
		constant.setAdr(charFactor.getC1());
		Code.load(constant);
	}
	
	public void visit(BoolFact boolFactor) {
		Obj constant = new Obj(Obj.Con, "constant", boolFactor.struct);
		constant.setAdr(boolFactor.getB1().equalsIgnoreCase("true") ? 1 : 0);
		Code.load(constant);
	}

	
	/////////////////// DESIGNATORS ///////////////////	
	public void visit(Assign assign) {
		Code.store(getCurrDes());						//na steku je vrednost, dodeli se trenutnom designatort
		currentDesignator.remove(getCurrDes());	
	}
	
	public void visit(Designator designator) {
		SyntaxNode parent = designator.getParent();
		
		if (designator.obj.getKind()==Obj.Meth) {
			currentDesignator.remove(currentDesignator.size()-1);
			return;
		}
		if (parent instanceof DesignatorStmtMinus2 && designator.obj.getKind()==Obj.Elem) {
			Code.put(Code.dup2);
			Code.load(designator.obj);
		}
		else if (parent instanceof DesignatorStmtPlus2 && designator.obj.getKind()==Obj.Elem) {
			Code.put(Code.dup2);
			Code.load(designator.obj);
		}
		else if (parent instanceof DesignatorStmtPlus2 && designator.obj.getKind()==Obj.Elem) {
			Code.put(Code.dup2);
			Code.load(designator.obj);
		}
		else if (!(parent instanceof FactorFuncCall) && !(parent instanceof Assign)
				&& designator.getDesignatorName().obj.getType().getKind()!=Struct.Array 
				&& !ParentRead(designator) && !(parent instanceof DesignatorStmtSqrt)) {
			Code.load(designator.obj);
		}
			
		if (!(Assign.class == parent.getClass() && ((Assign)parent).getDesignator() == designator)) {
			currentDesignator.remove(currentDesignator.size()-1);
		}
	}
	
	public void visit(DesignatorName node) {
		SyntaxNode parent = node.getParent();
		
		if (node.obj.getType().getKind()==Struct.Array && !(parent instanceof Assign)) {
			Code.load(node.obj);
		}
		else if (((Designator)parent).getDesignatorSufixList() instanceof DesignatorSufixListClass) {
			Code.load(node.obj);
		}
		currentDesignator.add(node.obj);
	}
	
	public void visit(DesignatorSufixSquare node) {
		
		boolean assignleft = false;
		assignleft = ParentAssign(node);	
		
		resetCurrDes(new Obj(Obj.Elem,"%", getCurrDes().getType().getElemType(), getCurrDes().getAdr(), getCurrDes().getLevel()));
		
		if (!assignleft && !Plus2Minus2(node) && node.getParent().getParent().getParent().getClass() != ReadStmt.class) {	
			//ako nije niz[i]++ ili niz[i]-- ili ako nije read.... ili kao ima sufix onda treba load
			Code.load(getCurrDes());
		
		}
		else if (((DesignatorSufixListClass)node.getParent()).getDesignatorSufixList().getClass() != NoDesignatorSufixList.class) {
			//mora read provera)
			Code.load(getCurrDes());
		}
		
	}
	
	
	/////////////////// METHODS ///////////////////
	public void visit(FuncName node) {
		if (node.getDesignator().getDesignatorSufixList().getClass() == DesignatorSufixListClass.class) {
			return;
		}
	}
	
	public void visit(FactorFuncCall FuncCall) {
		if ("chr".equalsIgnoreCase(FuncCall.getFuncName().getDesignator().obj.getName())) {
			return;
		}
		if ("ord".equalsIgnoreCase(FuncCall.getFuncName().getDesignator().obj.getName())) {
			return;
		}
		if ("len".equalsIgnoreCase(FuncCall.getFuncName().getDesignator().obj.getName())) {
			Code.put(Code.arraylength);
			return;
		}
		
		Obj functionObj = FuncCall.getFuncName().getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc; 
		Code.put(Code.call);
		Code.put2(offset);
	}

	public void visit(DesignatorStmtFunc node) {
		
		Obj function = node.getFuncName().getDesignator().obj;
		
		if (function.getName().equals("chr") || function.getName().equals("ord")) {
			// No need for conversion, char and integer values are the same
		} else if (function.getName().equals("len")) {
			// No need for executing non-void function
		} else {
			Code.put(Code.call);
			Code.put2(function.getAdr() - Code.pc + 1);
			if (function.getType() != Tab.noType)
				Code.put(Code.pop);
		}
	}
	
	public void visit(PrintStmt PrintStmt) {
		if (PrintStmt.getNumConstOpt().getClass() == NoNumConstOpt.class) {
			Code.put(Code.const_5);		
		}
		else {
			Code.loadConst(((NumConstOptClass)PrintStmt.getNumConstOpt()).getN1());	
		}
			
		if (PrintStmt.getExpr().struct==Tab.charType) {
			Code.put(Code.bprint);
		}
		else {
			Code.put(Code.print);
		}	
	}
	
	public void visit(ReadStmt readStmt) {
		if (readStmt.getDesignator().obj.getType()==Tab.intType || readStmt.getDesignator().obj.getType()==SemanticAnalyzer.boolType) {
			Code.put(Code.read);
		}
		else if (readStmt.getDesignator().obj.getType()==Tab.charType) {
			Code.put(Code.bread);
		}
		Code.store(readStmt.getDesignator().obj);
	}
		
	public void visit(MethodTypeNameWithType methodName) {
		methodName.obj.setAdr(Code.pc);									//adr metode je trenutni pc
		Code.put(Code.enter);
		Code.put(methodName.obj.getLevel());							//u level je broj formalnih parametara
		Code.put(methodName.obj.getLocalSymbols().size());				//u localSymbols su formalni parametri i lokalne promenljive
	}
	
	public void visit(MethodTypeNameWithVoid methodName) {
		methodName.obj.setAdr(Code.pc);	
		
		if (methodName.getMethName().equals("main"))
			this.mainPc = Code.pc;
		
		Code.put(Code.enter);
		Code.put(methodName.obj.getLevel());							//u level je broj formalnih parametara
		Code.put(methodName.obj.getLocalSymbols().size());				//u localSymbols su formalni parametri i lokalne promenljive
	}
	
	public void visit(MethodDeclClass MethodDecl) {
		if (!returnFound) {
			//runtime error
			//Code.put(Code.trap);
			//Code.put(-1);
			Code.put(Code.exit);
			Code.put(Code.return_);
		}
		returnFound = false;
	}
	
	public void visit(ReturnSmt ReturnExpr) {
		returnFound=true;
		if (ReturnExpr.getParent() instanceof Statements) {
			Statements statements = (Statements) ReturnExpr.getParent();
			
			if (statements.getParent() instanceof MethodDeclarations
				|| statements.getParent() instanceof ClassMethodsClass) { return;}
		}
		
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(ReturnVoid ReturnNoExpr) {
		returnFound=true;
		if (ReturnNoExpr.getParent() instanceof Statements) {
			Statements statements = (Statements) ReturnNoExpr.getParent();
			
			if (statements.getParent() instanceof MethodDeclarations
				|| statements.getParent() instanceof ClassMethodsClass) { return;}
		}
		
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	/////////////////// OPERATORS ///////////////////
	public void visit(AddopTermListClass node) {
		if (node.getAddop() instanceof Plus)
			Code.put(Code.add);
		
		if (node.getAddop() instanceof Minus)
			Code.put(Code.sub);
	}
	
	public void visit(MulopFactorListClass node) {
		if (node.getMulop() instanceof Mul)
			Code.put(Code.mul);
		
		if (node.getMulop() instanceof Div)
			Code.put(Code.div);
		
		if (node.getMulop() instanceof Mod)
			Code.put(Code.rem);	
	}
	
	public void visit(NewFactSquare node) {
		Code.put(Code.newarray);
		if (node.getType().struct==Tab.charType) 
			Code.put(0);							// Byte element
		else
			Code.put(1);							// Word element
	}
	
	public void visit(DesignatorStmtPlus2 node) {
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(node.getDesignator().obj);
	}
	
	public void visit(DesignatorStmtMinus2 node) {
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(node.getDesignator().obj);
	}
	
	public void visit(DesignatorStmtSqrt node) {
		
		//int i = node.getDesignator().obj.;
		
		Code.load(node.getDesignator().obj);					//designator
		
		Code.loadConst(0);
		Code.store(node.getDesignator().obj);
		
		int pocetak = Code.pc;
		
		Code.put(Code.dup);
		
		Code.load(node.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(node.getDesignator().obj);
		
		
		Code.load(node.getDesignator().obj);	//i
		Code.load(node.getDesignator().obj);	//i
		Code.put(Code.mul);						//i*i
		
		
		
		Code.putFalseJump(Code.lt, pocetak);
		
		/*?*/
		Code.load(node.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(node.getDesignator().obj);
		
		
	}
	
	public void visit(DesignatorStmtModif d) {
		Code.put(Code.load_1); 												// load_0
		Code.loadConst(0);		//const_0
		Code.put(Code.aload);	//aload
		Code.put(Code.load_3);												//load_2
		Code.putFalseJump(Code.lt, Code.pc+7); //jge 7 (=60)
		Code.loadConst(1);
		Code.putJump(Code.pc+4);
		Code.loadConst(0);    //56: const_1
		Code.put(Code.load_1);  											//61: load_0
		Code.put(Code.load_1);  											//62: load_1
		Code.put(Code.arraylength);
		Code.loadConst(1); //63: const_1
		Code.put(Code.sub); //64: sub
		Code.put(Code.aload); //65: aload
		Code.put(Code.load_3); 												//66: load_2
		Code.putFalseJump(Code.gt, Code.pc+7); //67: jle 7 (=74)
		Code.loadConst(1); //70: const_1
		Code.putJump(Code.pc + 4); //71: jmp 4 (=75)
		
		Code.loadConst(0);	//74: const_0
		Code.loadConst(0);	//75: const_0
		
		
		Code.putFalseJump(Code.ne, Code.pc+15); //76: jeq 15 (=91)
		
		
		Code.loadConst(0);	//79: const_0
		Code.putFalseJump(Code.ne, Code.pc+7); //67: jle 7 (=74)
		Code.loadConst(1);	//79: const_0
		Code.putJump(Code.pc + 4); //71: jmp 4 (=75)
		Code.loadConst(0);	//79: const_0
		Code.putJump(Code.pc + 5); //71: jmp 4 (=75)
		Code.put(Code.pop);
		
		Code.loadConst(0);	//74: const_0
		Code.loadConst(0);	//75: const_0
		Code.putFalseJump(Code.ne, Code.pc+106); //76: jeq 15 (=91)
		
		
		Code.loadConst(0);	//75: const_0
		
		Code.put(Code.store_2);
		Code.put(Code.load_2);
		Code.put(Code.load_1);
		Code.put(Code.arraylength);
		
		
		Code.putFalseJump(Code.lt, Code.pc+7); //80: jeq 7 (=87)
		Code.loadConst(1);	//83: const_1
		Code.putJump(Code.pc + 4); //84: jmp 4 (=88)
		Code.loadConst(0);	//87: const_0
		Code.loadConst(1);	//87: const_0
		
		Code.putFalseJump(Code.eq, Code.pc+86); //94: jeq 126 (=220)
		
		Code.putJump(Code.pc + 10); //88: jmp 5 (=93)
		
		Code.put(Code.load_2);
		Code.loadConst(1);	//93: const_0
		
		Code.put(Code.add); //91: pop
		Code.put(Code.store_2);
		Code.putJump(Code.pc -22);
		Code.put(Code.load_1);
		Code.put(Code.load_2);
		Code.put(Code.aload);
		Code.put(Code.load_3);
		
		Code.putFalseJump(Code.ge, Code.pc + 7);
		
		Code.loadConst(1);	//92: const_0
		Code.putJump(Code.pc+4);
		Code.loadConst(0);
		Code.loadConst(0);
		
		Code.putFalseJump(Code.ne, Code.pc+57); //94: jeq 126 (=220)
		
		Code.put(Code.load_1);
		Code.put(Code.arraylength);
		
		Code.loadConst(1);	//97: const_0
		Code.put(Code.sub);
		Code.put(Code.store_2);
		Code.put(Code.load_1);
		Code.put(Code.load_2);
		Code.put(Code.aload);
		Code.put(Code.load_3);
		
		Code.putFalseJump(Code.ge, Code.pc + 7);
		
		Code.loadConst(1);	//92: const_0
		Code.putJump(Code.pc+4);
		Code.loadConst(0);
		Code.loadConst(1);
		
		Code.putFalseJump(Code.eq, Code.pc + 24); 
		Code.putJump(Code.pc+10);
		Code.put(Code.load_2);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.store_2);
		Code.putJump(Code.pc - 23);
		
		Code.put(Code.load_1);
		Code.put(Code.load_2);
		Code.put(Code.load_1);
		Code.put(Code.load_2);
		
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.aload);
		Code.put(Code.astore);
		
		
		Code.putJump(Code.pc-15);
		Code.put(Code.load_1);
		Code.put(Code.load_2);
		
		Code.loadConst(1);
		Code.put(Code.add);
		Code.put(Code.load_3);
		
		Code.put(Code.astore);
		Code.putJump(Code.pc + 9); //208: jmp 9 (=217)
		Code.putJump(Code.pc + 3); //211: jmp 3 (=214)
		Code.putJump(Code.pc - 77); //214: jmp -94 (=120)
		Code.putJump(Code.pc + 3); //217: jmp 3 (=220)
		
	}
	
	public void visit(Term term) {
		SyntaxNode parent = term.getParent();
		if (parent instanceof ExprMinus)
			Code.put(Code.neg);								//ako je negativna vrednost
	}
		
	
	/////////////////// CONDITIONS ///////////////////
	public void visit(IfStmtJmp node) {
		Code.put(Code.jmp); 
		Code.put2(3); 
		System.out.println("IfStmtJmp" + adr.get(adr.size()-1)+ "," +Code.pc);
		Code.fixup(adr.remove(adr.size()-1));
		adr.add(Code.pc-2);
	}

	public void visit(ElseStmt node) {
		System.out.println("ElseStmt" + adr.get(adr.size()-1)+ "," +Code.pc);
		Code.fixup(adr.get(adr.size()-1));
	}
	
	public void visit(If node) {
		System.out.println("If" + adr.get(adr.size()-1)+ "," +Code.pc);
		adr.remove(adr.size()-1);							//kad se zavrsi if skida se adresa iz adr pomocnog steka
	}
	
	public void visit(IfElse node) {
		System.out.println("IfElse" + adr.get(adr.size()-1)+ "," +Code.pc);
		adr.remove(adr.size()-1);							//kad se zavrsi if skida se adresa iz adr pomocnog steka
	}
	
	//uslov ostavi mesto za skok
	public void visit(ConditionIfClass node) {
		Code.load(new Obj(Obj.Con, "%", Tab.intType, 0, 0));
		Code.put(Code.jcc + Code.eq); 
		Code.put2(0); 
		
		adr.add(Code.pc-2);
		System.out.println("ConditionIfClass" + adr.get(adr.size()-1)+ "," +Code.pc);
	}
	
	public void visit(Condition node) {
		
	}

	public void visit(ConditionNode conditionNode) {
	//	numOr ;
	}
	
	
	public void visit(ConditionOrNode node)
	{
		// A || B
		Code.loadConst(0);
		Code.putFalseJump(Code.eq, Code.pc + 15);
		// if (B == 0)
		// {
				Code.loadConst(0);
				Code.putFalseJump(Code.eq, Code.pc + 7);
				// if (A == 0)
				// {
						Code.loadConst(0);
				// }
				Code.putJump(Code.pc + 4);
				// else
				// {
						Code.loadConst(1);
				// }
		// }
		Code.putJump(Code.pc + 5);
		// else
		// {
				Code.put(Code.pop); // don't care A
				Code.loadConst(1);
		// }
		
	}
	public void visit(CondTermC node)
	{
		// A && B
		Code.loadConst(0);
		Code.putFalseJump(Code.ne, Code.pc + 15);
		// if (B != 0)
		// {
				Code.loadConst(0);
				Code.putFalseJump(Code.ne, Code.pc + 7);
				// if (A != 0)
				// {
						Code.loadConst(1);
				// }
				Code.putJump(Code.pc + 4);
				// else
				// {
						Code.loadConst(0);
				// }
		// }
		Code.putJump(Code.pc + 5);
		// else
		// {
				Code.put(Code.pop); // don't care A
				Code.loadConst(0);
		// }
		
		
	}
	
	public void visit(CondTermNode node) {
		
	}
	
	public void visit(CondFactExpr node) {
		// nista bool je vec stavljen#

	}
	
	private int getRelationOperator(Relop relOp) {
		if (relOp instanceof Eq)
			return Code.eq;
		if (relOp instanceof Neq)
			return Code.ne;
		if (relOp instanceof Gt)
			return Code.gt;
		if (relOp instanceof Gte)
			return Code.ge;
		if (relOp instanceof Lt)
			return Code.lt;
		if (relOp instanceof Lte)
			return Code.le;
		return 0;
	}

	public void visit(LogicalOr logOr) {
	}
	
	public void visit(LogicalAnd logAnd) {
	}
	
	public void visit(CondFactExprRelopExpr conditionExpressions) {
		// Check condition
		Code.putFalseJump(getRelationOperator(conditionExpressions.getRelop()), Code.pc + 7);
		Code.loadConst(1); // True
		Code.putJump(Code.pc + 4);
		Code.loadConst(0); // False
		this.condAdr.add(new ArrayList<Integer>());
		condAdr.get(condAdr.size()-1).add(Code.pc);
		
	}
	
	public void visit(ConditionOptClass node) {
		
		Code.loadConst(1);											//provera uslova
		Code.putFalseJump(Code.eq, 0); 								//iskoci iz fora
		
		forAdrFixupFalse.add(Code.pc - 2);							//sacuvam false, posle ze fixuje

		Code.putJump(0);											//uskoci u for
		forAdrFixupTrue.add(Code.pc - 2);							//sacuvam true, posle se fixuje		
		forAdrUpInkr.add(Code.pc);
		breakList.add(new ArrayList<>());
		
	}
	
	public void visit(NoConditionOpt node) {						//beskonacna petlja
		Code.putJump(0); 											//jump u for
		forAdrFixupTrue.add(Code.pc - 2);							//sacuvam true za condition address, posle se fixuje

		forAdrUpInkr.add(Code.pc);
		forAdrFixupFalse.add(-1);
		breakList.add(new ArrayList<>());							//za break
	}
	
	public void visit(DesignatorStmtOpt node) {
		designatorCond(node);
	}
	
	public void visit(NoDesignatorStmtOpt node) {
		designatorCond(node);
	}
	
	//kad se zavrsi for stavi skok na inkrement i za breakove i fiksuj 
	//i skini inkr adresu koja je trebala za continue
	public void visit(ForStatement node) {
		
		Code.putJump(forAdrUpInkr.get(forAdrUpInkr.size()-1));
		if (forAdrFixupFalse.get(forAdrFixupFalse.size()-1)!=-1)
			Code.fixup(forAdrFixupFalse.remove(forAdrFixupFalse.size()-1));
		else
			forAdrFixupFalse.remove(forAdrFixupFalse.size()-1);
		
		for (int i=0;i<breakList.get(breakList.size()-1).size();i++) {
			Code.fixup(breakList.get(breakList.size()-1).remove(0));
		}
		
		forAdrUpInkr.remove(forAdrUpInkr.size()-1);	
		breakList.remove(breakList.size()-1);
	}
	
	public void visit(BreakStmt node) {
		Code.put(Code.jmp);
		Code.put2(3); 
		breakList.get(breakList.size()-1).add(Code.pc-2);	
	}
	
	public void visit(ContinueStmt node) {
		Code.putJump(forAdrUpInkr.get(forAdrUpInkr.size()-1));
	}
}
