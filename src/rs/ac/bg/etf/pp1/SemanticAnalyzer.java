package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import org.apache.log4j.Logger;

import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;
import rs.etf.pp1.symboltable.*;
import rs.ac.bg.etf.pp1.ast.*;


public class SemanticAnalyzer extends VisitorAdaptor{
		
	int printCallCount = 0;
	int varDeclCount = 0;
	Obj currentMethod = null;
	boolean returnFound = false;
	boolean errorDetected = false;
	int nVars;
	
	String aps = "";
	
	int currentFor = 0;

	boolean mainVoid = false;
	
	Struct currentVarType = null;
	Obj currentClass = null;
	Obj currentExtAbsClass = null;
	
	ArrayList<Obj> currentMethodCall = new ArrayList<Obj>();
	ArrayList<Integer> currentActualParams = new ArrayList<Integer>();

	ArrayList<Obj> currentDesignator = new ArrayList<Obj>();

	static Struct boolType = new Struct(Struct.Bool);
	static Struct classType = new Struct(Struct.Class);
	static Struct abstractClassType = new Struct(Struct.Class);
	static Struct arrayType = new Struct(Struct.Array);
	
	Logger log = Logger.getLogger(getClass());
	
	
	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	private String getVarType(int i) {
		 switch (i) {
		case 1:
			return "int";
		case 2:
			return "char";
		case 3:
			return "array";
		case 4:
			return "class";
		case 5:
			return "bool";
		default:
			break;
		}
		return null;
	}
	
	// Con = 0, Var = 1, Type = 2, Meth = 3, Fld = 4, Elem=5, Prog = 6;
	private String getObjType(int i) {
		 switch (i) {
		 
		case 0:
			return "con";
		case 1:
			return "var";
		case 2:
			return "type";
		case 3:
			return "meth";
		case 4:
			return "fld";
		case 5:
			return "elem";
		case 6:
			return "prog";
		default:
			break;
		}
		return null;
	}
	
	
	private Obj getCurrDes() {
		return currentDesignator.get(currentDesignator.size()-1);
	}
	
	private void resetCurrDes(Obj obj) {
		currentDesignator.remove(currentDesignator.size()-1);
		currentDesignator.add(obj);
	}
	
	
	
	//pocetak programa
	public void visit(ProgName progName){
		Tab.insert(Obj.Type, "bool" , boolType);
	   	progName.obj = Tab.insert(Obj.Prog, progName.getPName(), Tab.noType);
		Tab.openScope();
	}
	    
	
	//kraj programa
	public void visit(Program program){
		nVars = Tab.currentScope.getnVars();
		
		//report_info(" \n\n\n ------------------------------------> nVars = "+ nVars+"\n\n\n", null);
		
	    Tab.chainLocalSymbols(program.getProgName().obj);
	    Tab.closeScope();
	}
	
	//kad se vidi type za deklaraciju promenljivih
	public void visit(TypeVar typeVar) {
		if (typeVar.getType().struct != Tab.noType) {
			currentVarType = typeVar.getType().struct;
		}
		else {
			report_error("Greska na liniji " + typeVar.getLine() + ", " + typeVar.getType().getTypeName() + " los tip: "+ typeVar.getType().getTypeName() + "!", null);
		}
	}	
	
	// tipovi za konstante
	public void visit(NumConstClass numConstClass) {
		
		SingleConstDecl parent = (SingleConstDecl) numConstClass.getParent();
		
		Obj constObj = Tab.find(parent.getConstName());
        if (constObj != Tab.noObj) {
        	report_error("Greska na liniji "  + parent.getLine() +", vec deklarisano: " + parent.getConstName()+ "!" , null);
        	return;
        }
		
		if (Tab.intType == currentVarType) {
			/**/report_info("* Deklarisana konstanta: "+ getVarType(currentVarType.getKind()) + " "+ parent.getConstName() + ",", parent);
			Obj obj = Tab.insert(Obj.Con, parent.getConstName(), currentVarType);
		
			//Obj m = new Obj(Obj.Con, "noObj", currentVarType);
			if (obj != Tab.noObj) {
				//za konstante u adr polju se cuva vrednost
				obj.setAdr(numConstClass.getN1());
			}
			else {
				report_error("Greska na liniji "  + parent.getLine() +", vec deklarisano: "+ parent.getConstName()  + "!", null);
			}
		}
		else {
			//ovo je svakako fatal error
			report_error ("Greska na liniji " + parent.getLine() + ": neekvivalentni tipovi" + "!", null );
		}
	
	}

	public void visit(CharConstClass charConstClass) {
		
		SingleConstDecl parent = (SingleConstDecl) charConstClass.getParent();
		
		Obj constObj = Tab.find(parent.getConstName());
        if (constObj != Tab.noObj) {
        	report_error("Greska na liniji "  + parent.getLine() +", vec deklarisano: " + parent.getConstName() + "!", null);
        	return;
        }
		if (Tab.charType == currentVarType) {
			/**/report_info("* Deklarisana konstanta: "+ getVarType(currentVarType.getKind()) + " " + parent.getConstName() + ",", parent);
			Obj obj = Tab.insert(Obj.Con, parent.getConstName(), currentVarType);
			
			if (obj !=Tab.noObj) {
				//za konstante u adr polju se cuva vrednost
				obj.setAdr(charConstClass.getC1());
			}
			else {
				report_error("Greska na liniji "  + parent.getLine() +", vec deklarisano: "+ parent.getConstName() + "!", null);
			}
		}
		else {
			report_error ("Greska na liniji " + parent.getLine() + ": neekvivalentni tipovi" + "!", null );
		}
	}
	
	public void visit(BoolConstClass boolConstClass) {
	
		SingleConstDecl parent = (SingleConstDecl) boolConstClass.getParent();
		
		Obj constObj = Tab.find(parent.getConstName());
        if (constObj != Tab.noObj) {
        	report_error("Greska na liniji "  + parent.getLine() +", vec deklarisano: " + parent.getConstName() + "!", null);
        	return;
        }
        
		if (boolType == currentVarType) {
			report_info("* Deklarisana konstanta: "+ getVarType(currentVarType.getKind()) + " " + parent.getConstName() + ",", parent);
			Obj obj = Tab.insert(Obj.Con, parent.getConstName(), currentVarType);
			
			if (obj !=Tab.noObj) {
				if (boolConstClass.getB1().equals("false"))
					obj.setAdr(0);
				else
					obj.setAdr(1);
			}
			else {
				report_error("Greska na liniji "  + parent.getLine() +", vec deklarisano: "+ parent.getConstName()+ "!" , null);
			}
		}
		else {
			report_error ("Greska na liniji " + parent.getLine() + ": neekvivalentni tipovi" + "!", null );
		}
		
		
	}
	
	//kad se zavrsi cela deklaracija promenljivih
	public void visit(SingleVarDecls singleVarDecls) {
		currentVarType = null;
	}
	
	// obicna promenljiva
	public void visit(VardClass vardClass){
		varDeclCount++;
		
		Obj constObj = Tab.find(vardClass.getVarName());
        if (constObj != Tab.noObj) {
        	report_error("Greska na liniji "  + vardClass.getLine() +", vec deklarisano: " + vardClass.getVarName() + "!", null);
        	return;
        }
		
		Tab.insert(Obj.Var, vardClass.getVarName(), currentVarType);
		report_info("* Deklarisana promenljiva: "+ getVarType(currentVarType.getKind()) + " "+ vardClass.getVarName() + ",", vardClass);
	}
	
	
	//nizovna promenljiva
	public void visit(VardClassSquares vardClassSquares) {
		
		
		Struct struct = new Struct(Struct.Array);
		struct.setElementType(currentVarType);	//elementi niza
		
		Obj constObj = Tab.find(vardClassSquares.getVarName());
        if (constObj != Tab.noObj) {
        	report_error("Greska na liniji "  + vardClassSquares.getLine() +", vec deklarisano: " + vardClassSquares.getVarName() + "!", null);
        	return;
        }
		Tab.insert(Obj.Var, vardClassSquares.getVarName(), struct);
		report_info("* Deklarisana promenljiva: "+ getVarType(currentVarType.getKind()) + " "+ vardClassSquares.getVarName() + ",", vardClassSquares);
		
	}
	
	
	// ime klase pocetak
	public void visit(ClassNameC className) {
		currentExtAbsClass = null;
		Struct currentClassStruct = new Struct(Struct.Class);
		currentClassStruct.setElementType(null);
		
		Obj constObj = Tab.find(className.getClassName());
        if (constObj != Tab.noObj) {
        	report_error("Greska na liniji "  + className.getLine() +", vec deklarisano: " + className.getClassName() + "!", null);
        	return;
        }
        aps = "";
		className.obj = Tab.insert(Obj.Type, className.getClassName(), currentClassStruct);
		currentClass = className.obj;
    	Tab.openScope();
    	Tab.insert(Obj.Fld, "%virtualtablestart", Tab.intType);
    	
    	report_info("* Deklarisana klasa: " + className.getClassName() + "," , className);

	}
	
	// ime apstraktne klase pocetak
	public void visit(AbsClassNameC className) {
		currentExtAbsClass = null;
		Struct currentClassStruct = new Struct(Struct.Class);//ILI da pamtim kao Struct.Interface ???
		currentClassStruct.setElementType(null);
		
		Obj constObj = Tab.find(className.getAcName());
        if (constObj != Tab.noObj) {
        	report_error("Greska na liniji "  + className.getLine() +", vec deklarisano: " + className.getAcName() + "!", null);
        	return;
        }
        aps = "apstraktnoj";
		className.obj = Tab.insert(Obj.Type, className.getAcName(), currentClassStruct);
		currentClass = className.obj;
    	Tab.openScope();
    	
    	SymbolDataStructure table=currentClass.getType().getMembersTable();
		table.insertKey(new Obj(Obj.Fld, "%virtualabstracttablestart", Tab.intType)); //oznaka da je aps klasa
    	
    //	constObj.getType().getMembers().add(new Obj(Obj.Fld, "%virtualabstracttablestart", Tab.intType));
    	Tab.insert(Obj.Fld, "%virtualabstracttablestart", Tab.intType);
    	
    	report_info("* Deklarisana apstraktna klasa: " + className.getAcName() + "," , className);

	}
	
	// extends ima
	public void visit(ExtendsClass extendsClass) {
		
		//currentAbsClass = null;
		
		Obj temp = Tab.find(extendsClass.getType().getTypeName());
		Object innerobjs[] = temp.getType().getMembers().toArray();
		
		if (temp!=Tab.noObj && temp.getType().getKind() == Struct.Class) {
			
			//System.out.println("!!!!!!!!  extendsClass -> " + temp.getName());
			
			//for (int i=0; i<innerobjs.length; i++) {
			//	System.out.println("!!!!!!!!  extendsClass -> polje u temp obj: "+ ((Obj)innerobjs[i]).getName() + "; " + getObjType(((Obj)innerobjs[i]).getKind()));
			//}
			
			currentClass.getType().setElementType(temp.getType());	//postavi roditelja
			//System.out.println(" " + temp.getName() + " " + extendsClass.getLine() + " " + temp.getType().getElemType());
			//uvezi sve metode iz te klase i promenljive
	
		}
		else {
			report_error("Greska na liniji "  + extendsClass.getLine() +", ne postoji klasa: "+ extendsClass.getType().getTypeName() + "!" , null);
		}
	}
	
	//kad se zavrsi cela deklaracija promenljivih u klasi
	public void visit(VardDeclsC singleVarDecls) {
		currentVarType = null;
	}
		
	// nizovna promenljiva u klasi
	public void visit(VardCSquares vardClass){
		varDeclCount++;
		//report_info("Deklarisana promenljiva u klasi "+ vardClass.getVarName(), vardClass);
		
		//Obj cObj = Tab.find(vardClass.getVarName());
        
		if(vardClass.getParent().getClass() == SingleVardListC.class || 
				vardClass.getParent().getClass() == VardListC.class) {
			
			SymbolDataStructure table=currentClass.getType().getMembersTable();
    		if (table.searchKey(vardClass.getVarName()) == null) {

    			report_info("* Deklarisan niz: "+ vardClass.getVarName() + " u "+ aps +" klasi: " + currentClass.getName(), vardClass);
    			Struct arrayStruct = new Struct(Struct.Array,currentVarType);
    			Obj var=Tab.insert(Obj.Fld, vardClass.getVarName(), arrayStruct);
    			table.insertKey(var);
    			currentClass.getType().setMembers(currentClass.getType().getMembersTable());
    		}else
    		{
    			report_error("Greska na liniji "  + vardClass.getLine() +", u "+ aps +" klasi: "+ currentClass.getName()+" vec deklarisano polje: "+ vardClass.getVarName() , null);
    		}
    	}
				
	}
		
	// obicna promenljiva u klasi
	public void visit(VardCClass vardClass) {
		
		if(vardClass.getParent().getClass() == SingleVardListC.class || 
				vardClass.getParent().getClass() == VardListC.class) {
		
    		SymbolDataStructure table=currentClass.getType().getMembersTable();
    		if (table.searchKey(vardClass.getVarName()) == null) {
    			
    			if(aps != "") {
    			//	table.insertKey(new Obj(Obj.Fld, "%virtualabstracttablestart", Tab.intType)); 
    			}
    			
    			report_info("* Deklarisano polje: "+ vardClass.getVarName() + " u "+ aps +" klasi: " + currentClass.getName(), vardClass);
    			Obj var=Tab.insert(Obj.Fld, vardClass.getVarName(), currentVarType);
    			table.insertKey(var);
    			currentClass.getType().setMembers(currentClass.getType().getMembersTable());
    		}else
    		{
    			report_error("Greska na liniji "  + vardClass.getLine() +", u "+ aps +" klasi: "+ currentClass.getName()+" vec deklarisano polje: "+ vardClass.getVarName() , null);
    		}
    	}
    	
	}
	
	
/////////////////////////////*********************/////////////////////////////
	
	//provera dal su iste metode
	private boolean checkMethods(Obj m1,Obj m2) {
		//report_info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! rade se " + m1.getName() + " " + m2.getName() + " " 
		//			+ m1.getType().getKind() + " " + m2.getType().getKind() + " " 
		//			+ m1.getLevel() + " " + (m2.getLevel()),null);
		
		if (m1.getType() != m2.getType()) {
			return false;
		}
		
		if (m1.getLevel() != m2.getLevel() + 100) {
		}
		
		for (int i = 0; i < m1.getLevel(); i++) {
			//report_info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! method " + m1.getName() + " method2 " + m2.getName(),null);
			
			Obj arg1 =(Obj) m1.getLocalSymbols().toArray()[i];
			//report_info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! method " + m1.getName() + " arg1 " + getVarType(arg1.getType().getKind()) ,null);
			
			Obj arg2 =(Obj) m2.getLocalSymbols().toArray()[i];
			//report_info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! method " + m1.getName() + " arg2 " + getVarType(arg2.getType().getKind()) ,null);
			
			if (arg1.getType().getKind()!=arg2.getType().getKind()) {
				
				return false;
			}
		}
		report_info( m1.getName() + " i " + m2.getName() +" imaju isti potpis!", null);
		return true;
	}


/////////////////////////////*********************/////////////////////////////
	
	//provera dal su iste abs metode (neg level)
	private boolean checkAbsMethods(Obj m1,Obj m2) {
	//report_info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! rade se " + m1.getName() + " " + m2.getName() + " " 
	//			+ m1.getType().getKind() + " " + m2.getType().getKind() + " " 
	//			+ m1.getLevel() + " " + (m2.getLevel()),null);
	
	if (m1.getType() != m2.getType()) {
	return false;
	}
	
	if (m1.getLevel() != m2.getLevel()) {
	return false;
	}
	
	for (int i = 0; i < m1.getLevel(); i++) {
	//report_info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! method " + m1.getName() + " method2 " + m2.getName(),null);
	
	Obj arg1 =(Obj) m1.getLocalSymbols().toArray()[i];
	//report_info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! method " + m1.getName() + " arg1 " + getVarType(arg1.getType().getKind()) ,null);
	
	Obj arg2 =(Obj) m2.getLocalSymbols().toArray()[i];
	//report_info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! method " + m1.getName() + " arg2 " + getVarType(arg2.getType().getKind()) ,null);
	
	if (arg1.getType().getKind()!=arg2.getType().getKind()) {
	
	return false;
	}
	}
	report_info( m1.getName() + " i " + m2.getName() +" imaju isti potpis!", null);
	return true;
	}

	
/////////////////////////////*********************/////////////////////////////
	
	// kraj klase
	public void visit(ClassDecl classDecl) {
		
		Tab.chainLocalSymbols(classDecl.getClassName().obj.getType());	//members
    	Object metodeklasa[] = classDecl.getClassName().obj.getType().getMembers().toArray();
		
    	/*
    	for(int i = 0; i<metodeklasa.length; i++) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! KRAJ KLASE: "+ classDecl.getClassName().obj.getName() +", MEMBERS -> "+((Obj)metodeklasa[i]).getName());
		}*/
    	
		if(currentExtAbsClass != null) { //izvodim iz apstraktne moraju sve metode biti redefinisane
			Obj o = Tab.find(currentExtAbsClass.getName());
			
			Object metodeApsNatklase[] = o.getType().getMembers().toArray();
			
			/*
			for(int i = 0; i < metodeklasa.length; i++) {
				if (((Obj)metodeklasa[i]).getKind()==Obj.Meth) 
				System.out.println("***************************** "+ ((Obj)metodeklasa[i]).getName() + " "+((Obj)metodeklasa[i]).getLevel() +" *****************************");
			}
			for(int i = 0; i < metodeApsNatklase.length; i++) {
				if (((Obj)metodeApsNatklase[i]).getKind()==Obj.Meth)
				System.out.println("///////////////////////////// "+ ((Obj)metodeApsNatklase[i]).getName() +  " "+((Obj)metodeApsNatklase[i]).getLevel() +" /////////////////////////////");
				System.out.println("                            "  );
			}
			*/
			
			for(int i = 0; i < metodeApsNatklase.length; i++) {
				boolean found = false;
				if(((Obj)metodeApsNatklase[i]).getLevel() >= 0) continue;
				if(((Obj)metodeApsNatklase[i]).getKind() != Obj.Meth) continue;
				
				
				for(int k=0; k < metodeklasa.length; k++) {
					if (((Obj)metodeklasa[k]).getKind() == Obj.Meth) {
						if (((Obj)metodeklasa[k]).getName().equals(((Obj)metodeApsNatklase[i]).getName())) {
							found = true;
							if (!checkMethods((Obj)metodeklasa[k],(Obj)metodeApsNatklase[i])){
								report_error("Greska na liniji " + classDecl.getLine() + ", ne poklapa se potpis funkcije: " + ((Obj)metodeklasa[k]).getName() +
							" sa istoimenom metodom apstraktne natklase", null);
							}	
						}
					}
				}
				
				if (!found)
					report_error("Greska na liniji " + classDecl.getLine() + ", nije redefinisana fja: "+ ((Obj)metodeApsNatklase[i]).getName() +" iz apstraktne klase koja se nasledjuje u klasi " + classDecl.getClassName().obj.getName(), null);
			}
		}
		
		Tab.closeScope();
    		
		currentClass=null;
		currentExtAbsClass = null;
	}
	
	public void visit(AbsClassDeclC absClassDecl) {
		Tab.chainLocalSymbols(absClassDecl.getAbsClassName().obj.getType());	//members
    	Object metodeklasa[] = absClassDecl.getAbsClassName().obj.getType().getMembers().toArray();
		
    	/*
    	for(int i = 0; i<metodeklasa.length; i++) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! KRAJ KLASE: "+ absClassDecl.getAbsClassName().obj.getName() +", MEMBERS -> "+((Obj)metodeklasa[i]).getName());
		}*/
    	
		if(currentExtAbsClass != null) { //izvodim iz apstraktne moraju sve metode biti redefinisane
			Obj o = Tab.find(currentExtAbsClass.getName());
			
			Object metodeApsNatklase[] = o.getType().getMembers().toArray();
			
			/*
			for(int i = 0; i < metodeklasa.length; i++) {
				if (((Obj)metodeklasa[i]).getKind()==Obj.Meth) 
				System.out.println("***************************** "+ ((Obj)metodeklasa[i]).getName() + " "+((Obj)metodeklasa[i]).getLevel() +" *****************************");
			}
			for(int i = 0; i < metodeApsNatklase.length; i++) {
				if (((Obj)metodeApsNatklase[i]).getKind()==Obj.Meth)
				System.out.println("///////////////////////////// "+ ((Obj)metodeApsNatklase[i]).getName() +  " "+((Obj)metodeApsNatklase[i]).getLevel() +" /////////////////////////////");
				System.out.println("                            "  );
			}*/
			
			
			for(int i = 0; i < metodeApsNatklase.length; i++) {
				boolean found = false;
				if(((Obj)metodeApsNatklase[i]).getLevel() >= 0) continue;
				if(((Obj)metodeApsNatklase[i]).getKind() != Obj.Meth) continue;
				
				
				for(int k=0; k < metodeklasa.length; k++) {
					if (((Obj)metodeklasa[k]).getKind() == Obj.Meth) {
						if (((Obj)metodeklasa[k]).getName().equals(((Obj)metodeApsNatklase[i]).getName())) {
							found = true;
							if (((Obj)metodeklasa[k]).getLevel() < 0) {
								if (!checkAbsMethods((Obj)metodeklasa[k],(Obj)metodeApsNatklase[i])){
									report_error("Greska na liniji " + absClassDecl.getLine() + ", ne poklapa se potpis funkcije: " + ((Obj)metodeklasa[k]).getName() +
								" sa istoimenom metodom apstraktne natklase", null);
								}
							}
							else {
								if (!checkMethods((Obj)metodeklasa[k],(Obj)metodeApsNatklase[i])){
									report_error("Greska na liniji " + absClassDecl.getLine() + ", ne poklapa se potpis funkcije: " + ((Obj)metodeklasa[k]).getName() +
								" sa istoimenom metodom apstraktne natklase", null);
								}
							}
							//if (((Obj)metodeklasa[k]).getLevel() < 0) continue; 
							
						}
					}
				}
				
				if (!found) {
					//ako nije redefinisana -> nije greska nego je nasledjena apstraktna
					//report_error("Greska na liniji " + absClassDecl.getLine() + ", nije redefinisana fja: "+ ((Obj)metodeApsNatklase[i]).getName() +" iz apstraktne klase koja se nasledjuje u klasi " + absClassDecl.getAbsClassName().obj.getName(), null);
					//absClassDecl.
					currentMethod = Tab.insert(Obj.Meth, ((Obj)metodeApsNatklase[i]).getName(), Tab.noType);
					currentMethod.setLevel(((Obj)metodeApsNatklase[i]).getLevel());
				}
			}
		}
		
		Tab.closeScope();	
		currentClass=null;
		currentExtAbsClass = null;
	}
	
    // ZA TIP 
    public void visit(Type type){
    	
    	//if (o != null) {
    		//	report_error("Greska na liniji " + typeVar.getLine() + ", ne moze se instancirati objekat apstraktne klase!" , null);
    		//}
    	
    	Obj typeNode = Tab.find(type.getTypeName());
    	
    	
    	if(typeNode == Tab.noObj){
    		report_error("Greska na liniji " + type.getLine() + ", nije pronadjen tip: " + type.getTypeName() + " u tabeli simbola! ", null);
    		type.struct = Tab.noType;
    		
    	}else{
    		if(Obj.Type == typeNode.getKind()){
    			type.struct = typeNode.getType();
    		}else{
    			report_error("Greska na liniji " + type.getLine() + ", ime :" + type.getTypeName() + " ne predstavlja tip!", null);
    			type.struct = Tab.noType;
    		}
    	}
    	
    	if (type.getParent().getClass() == NewType.class) {
    		if (Tab.noObj == typeNode)
    			typeNode = new Obj(Obj.NO_VALUE,"%error",Tab.noType);
    		((NewType)type.getParent()).obj = typeNode;
    		
    		Obj temp = Tab.find(type.getTypeName());
			Object innerobjs[] = temp.getType().getMembers().toArray();
			if(innerobjs.length != 0) {
				if( ((Obj)innerobjs[0]).getName() == "%virtualabstracttablestart") {
					report_error("Greska na liniji " + type.getLine() + ", ne moze se instancirati objekat apstraktne klase!" , null);
				}
			}
    	}
    	/*
    	if (type.getParent().getClass() == NewFactSquare.class) {
    		Obj temp = Tab.find(type.getTypeName());
			Object innerobjs[] = temp.getType().getMembers().toArray();
			if(innerobjs.length != 0) {
				if( ((Obj)innerobjs[0]).getName() == "%virtualabstracttablestart") {
					report_error("Greska na liniji " + type.getLine() + ", ne moze se instancirati objekat apstraktne klase!" , null);
				}
			}
    	}
    	*/
    	if (type.getParent().getClass() == ExtendsClass.class) {
    		if(type.struct.getKind() == Struct.Class) {
    			
    			Obj temp = Tab.find(type.getTypeName());
    			Object innerobjs[] = temp.getType().getMembers().toArray();
    			if( ((Obj)innerobjs[0]).getName() == "%virtualabstracttablestart") {
    				currentExtAbsClass = temp;
    				//System.out.println("CURRENT ABS CLASS = " + temp.getName());
    			}
    			else {
        			currentExtAbsClass = null;
        			//System.out.println("CURRENT ABS CLASS = nema");
        		}
    			
    		}
    		
    	}
    	
    }
    
    // definicija fje sa povratnim tipom
    public void visit(MethodTypeNameWithType methodTypeNameWithType){
    	currentMethod = Tab.insert(Obj.Meth, methodTypeNameWithType.getMethName(), methodTypeNameWithType.getType().struct);
    	methodTypeNameWithType.obj = currentMethod;
    	currentMethod.setLevel(0);
    	Tab.openScope();
    	
    	if (currentClass!=null) {

    		Obj temp = Tab.find(currentClass.getName());
			Object innerobjs[] = temp.getType().getMembers().toArray();
			
    		if( ((Obj)innerobjs[0]).getName() != "%virtualabstracttablestart") {
	    		Tab.insert(Obj.Var, "this", currentClass.getType());
	    		currentMethod.setLevel(1);
    		}
    		else {
    			Tab.insert(Obj.Var, "this", currentClass.getType());
    			currentMethod.setLevel(1);
	    		
    		}
    	}
    	
		report_info("~ Obradjuje se funkcija: " + methodTypeNameWithType.getMethName() + ",", methodTypeNameWithType);
    }
    
    
    // definicija void fje
    public void visit(MethodTypeNameWithVoid methodTypeNameWithVoid) {
    	
    	currentMethod = Tab.insert(Obj.Meth, methodTypeNameWithVoid.getMethName(), Tab.noType);
    	methodTypeNameWithVoid.obj = currentMethod;
    	currentMethod.setLevel(0);
    	Tab.openScope();
    	
    	if (currentClass!=null) {
    		
    		
    		Obj temp = Tab.find(currentClass.getName());
			Object innerobjs[] = temp.getType().getMembers().toArray();
			
    		if( ((Obj)innerobjs[0]).getName() != "%virtualabstracttablestart") {
	    		Tab.insert(Obj.Var, "this", currentClass.getType());
	    		currentMethod.setLevel(1);
    		}
    		else {
    			Tab.insert(Obj.Var, "this", currentClass.getType());
	    		currentMethod.setLevel(1);
	    	}
    	}
    
    	report_info("~ Obradjuje se funkcija " + methodTypeNameWithVoid.getMethName(), methodTypeNameWithVoid);
    }
    
    public void visit(AbsClassAbsMethodDeclList absClassAbsMethodDecl) {
    	if(currentMethod != null)
    	currentMethod.setLevel(currentMethod.getLevel() - 100);
    	
    	currentMethod = null;
    }

	  /*
  //kreaj interfejs metode jbt
  public void visit(InterfaceMethodDecl node ) {
  	Tab.chainLocalSymbols(currentMethod);
  	Tab.closeScope();
  	returnFound = false;
  	
  }
  */
	
	public void visit(AbsMethodDeclClass absMethodDecl) {
		
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		
		returnFound = false;
	  	currentMethod = null;
	}
	
    // kraj fje
    public void visit(MethodDeclClass methodDecl){
    	if(!returnFound && currentMethod.getType() != Tab.noType){
			//za testiranje runtime error komentarisati ovo dole
    		report_error("Greska na liniji " + methodDecl.getLine() + ", funkcija: " + currentMethod.getName() + " nema return iskaz!", null);
    	}
    	Tab.chainLocalSymbols(currentMethod);
    	Tab.closeScope();
    	
    	returnFound = false;
    	currentMethod = null;
    	
    	if (methodDecl.getMethodTypeName().obj.getName().equals("main")) {
    		if (methodDecl.getMethodTypeName().obj.getType() == Tab.noType && methodDecl.getMethodTypeName().obj.getLevel() ==0)
    			mainVoid = true;
    	}
    }
    
  
    
    //formalni paramater
    public void visit(FormalParam formalParam) { 
    	Obj obj = Tab.insert(Obj.Var, formalParam.getPName(), formalParam.getType().struct);
    	obj.setFpPos(currentMethod.getLevel());
    	
    	if (obj==Tab.noObj)
			report_error("Greska na liniji "  + formalParam.getLine() +", vec deklarisano: "+ formalParam.getPName() , null);
    	
    	currentMethod.setLevel(currentMethod.getLevel()+1);
    }
    
    //formalni paramater kao niz
    public void visit(FormalParamSquares formalParamSquares) { 	
    	Struct struct = new Struct(Struct.Array);
    	struct.setElementType(formalParamSquares.getType().struct);
    	Obj obj = Tab.insert(Obj.Var, formalParamSquares.getPName(), struct);
    	obj.setFpPos(currentMethod.getLevel());
    	
    	if (obj==Tab.noObj)
			report_error("Greska na liniji "  + formalParamSquares.getLine() +", vec deklarisano: "+ formalParamSquares.getPName() , null);
    	currentMethod.setLevel(currentMethod.getLevel()+1); 	
    }
    
    
    
    
    public void visit(FuncName funcName) {
    	currentMethodCall.add(funcName.getDesignator().obj);
    	currentActualParams.add(funcName.getDesignator().obj.getLevel()-1);
    	
    }
    
    // poziv funkcije
    public void visit(DesignatorStmtFunc designatorStmtFunc){
    	Obj func = designatorStmtFunc.getFuncName().getDesignator().obj;
    	if(Obj.Meth == func.getKind()){

			report_info("Pretraga na liniji " + designatorStmtFunc.getLine() +", pronadjen poziv funkcije: " + func.getName(), null);
			designatorStmtFunc.struct = func.getType();
		
    	}else{
			report_error("Greska na liniji " + designatorStmtFunc.getLine()+ "  ime " + func.getName() + " nije funkcija!", null);
			designatorStmtFunc.struct = Tab.noType;
    	}
    	currentMethodCall.remove(currentMethodCall.size()-1);
    	currentActualParams.remove(currentActualParams.size() -1);
    
    }
    
    
    public void visit(BreakStmt breakStmt) {
    	if (currentFor>0) {
    		//ok
    	}
    	else {
    		report_error("Greska na liniji " + breakStmt.getLine() + ", break naredba je izvan for petlje!", null);
    	}
    }
    
    public void visit(ContinueStmt continueStmt) {
    	if (currentFor>0) {
    		
    	}
    	else {
    		report_error("Greska na liniji " + continueStmt.getLine() + ", continue naredba je izvan for petlje!", null);
    	}
    }
    
    // designator++
    public void visit(DesignatorStmtPlus2 designatorStmtPlus2) {
    	//provera da li postoji taj designator
    	Obj var = designatorStmtPlus2.getDesignator().obj;
    	if (var.getType() == Tab.intType) {
    		// ok
    	}
    	else {
    		report_error("Greska na liniji " + designatorStmtPlus2.getLine() + ", designator nije tipa int!" , null);
    	}
    }
    
    // designator--
    public void visit(DesignatorStmtMinus2 designatorStmtMinus2) {
    	//provera da li postoji taj designator
    	Obj var = designatorStmtMinus2.getDesignator().obj;
    	if (var.getType() == Tab.intType) {
    		// ok
    	}
    	else {
    		report_error("Greska na liniji " + designatorStmtMinus2.getLine() + ", designator nije tipa int!", null);
    	}
    }
    public void visit(DesignatorStmtSqrt designatorStmtSqrt) {
    	//provera da li postoji taj designator
    	Obj var = designatorStmtSqrt.getDesignator().obj;
    	if (var.getType() == Tab.intType) {
    		// ok
    	}
    	else {
    		report_error("Greska na liniji " + designatorStmtSqrt.getLine() + ", designator nije tipa int!", null);
    	}
    }
    public void visit(DesignatorStmtModif designatorStmtModif) {
    	//provera da li postoji taj designator
    	if (designatorStmtModif.getFactor().struct.getKind() == Struct.Int) {
    		// ok
    	}
    	else {
    		report_error("Greska na liniji " + designatorStmtModif.getLine() + ", designator nije tipa int!", null);
    	}
    	if (designatorStmtModif.getDesignator().obj.getType().getKind() == Struct.Array) {
    		// ok
    	}
    	else {
    		report_error("Greska na liniji " + designatorStmtModif.getLine() + ", designator nije niz int!", null);
    	}
    	
    }
    
    public void visit(ReadStmt readStmt) {
    	//provera da li je promenljiva polje klase ili element niza ***********************mora se odradi svugde gde treba
    	if (readStmt.getDesignator().obj.getType() != Tab.intType && readStmt.getDesignator().obj.getType() != Tab.charType && readStmt.getDesignator().obj.getType() !=boolType)
    		report_error ("Greska na liniji " + readStmt.getLine() + ", operand instrukcije READ mora biti char ili int ili bool tipa", null );
		
    }
    
    public void visit(PrintStmt print) {
    	if	(print.getExpr().struct != Tab.intType && print.getExpr().struct!= Tab.charType && print.getExpr().struct!=boolType) 
    		report_error ("Greska na liniji " + print.getLine() + ", operand instrukcije PRINT mora biti char ili int ili bool tipa", null );
		
    	printCallCount++;
	}
    
    // expr bez minusa
    public void visit(ExprNoMinus exprNoMinus) {
    	Struct te = exprNoMinus.getAddopTermList().struct;
    	Struct t = exprNoMinus.getTerm().struct;
    	
    	if (te == null) {
    		//ok
    		
    		exprNoMinus.struct = t;
    	}
    	else if (t==Tab.intType && te==Tab.intType) {	
    		exprNoMinus.struct = t;
    	}
    	else{
			report_error("Greska na liniji "+ exprNoMinus.getLine() + ", nekompatibilni tipovi u izrazu za sabiranje.", null);
			exprNoMinus.struct = Tab.noType;
    	}
    }
   
    // expresion ako su oba tipa ista moze
    public void visit(ExprMinus exprMinus){
    	Struct te = exprMinus.getAddopTermList().struct;
    	Struct t = exprMinus.getTerm().struct;
    	
    	if (te == null) {
    		//ok
    		exprMinus.struct = t;
    	}
    	else if(t==Tab.intType && te==Tab.intType){
    		exprMinus.struct = t;
    	}else{
			report_error("Greska na liniji "+ exprMinus.getLine() + ", nekompatibilni tipovi u izrazu za sabiranje. ", null);
			exprMinus.struct = Tab.noType;
    	}
    }
    
    
    // addop term list
    public void visit(AddopTermListClass node) {
    	
    	if (node.getAddopTermList().struct!=null) {
    		if (node.getTerm().struct==Tab.intType && node.getAddopTermList().struct==Tab.intType) {
    			//ok
    			node.struct = node.getTerm().struct;
    		}
    		else {
    			report_error("Greska na liniji "+ node.getLine() + ", nekompatibilni tipovi u izrazu za sabiranje. ", null);
    			node.struct = Tab.noType;
    		}
    	}
    	else {
    		//ok kraj liste
    		node.struct = node.getTerm().struct;
    	}

    }
    
    
    public void visit(NoAddopTermList node) {
    	node.struct = null;
    }
    
    public void visit(BoolFact boolFact){
    	boolFact.struct = boolType;
    }
    
    public void visit(ExprFact exprFact) {
    	exprFact.struct = exprFact.getExpr().struct;
    }
    
    public void visit(NumFact numFact){
    	numFact.struct = Tab.intType;
    }
    
    public void visit(CharFact charFact){
    	charFact.struct = Tab.charType;
    }
    
    public void visit(NullFact nullFact){
    	nullFact.struct = Tab.nullType;
    }

    public void visit(DesignatorFact designatorFact){
    	designatorFact.struct = designatorFact.getDesignator().obj.getType();
    }
    
    public void visit (FactorFuncCall node ) {
    	Obj func = node.getFuncName().getDesignator().obj;
    	if(Obj.Meth == func.getKind()){

			report_info("Pretraga na liniji " + node.getLine() +", pronadjen poziv funkcije: " + func.getName(), null);
			node.struct = func.getType();
		
    	}else{
			report_error("Greska na liniji " + node.getLine()+ ", ime: " + func.getName() + " nije funkcija!", null);
			node.struct = Tab.noType;
    	}
    	currentMethodCall.remove(currentMethodCall.size()-1);
    	currentActualParams.remove(currentActualParams.size() -1);
    	
    }
    
    
    public void visit(ReturnSmt returnSmt){
    	if (currentMethod==null)
    	{
    		report_error("Greska na liniji " + returnSmt.getLine() + ", ne moze return van tela funkcije!", null);
	    	
    	}
    	else {
    		returnFound = true;
	    	Struct currMethType = currentMethod.getType();
	    	if(!currMethType.compatibleWith(returnSmt.getExpr().struct)){
				report_error("Greska na liniji " + returnSmt.getLine() + ", tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije!" + currentMethod.getName(), null);
	    	}
    	}
    }
    
    // pocetak fora
    public void visit(ForHeader forHeader) {
    	currentFor++;
    }
    // kraj fora
    public void visit(ForStatement forStatement) {
    	currentFor--;
    }
    
    public void visit(ActPars actPars) {
    	
    	if (currentActualParams.get(currentActualParams.size()-1) >= 0 ) {
    		
    		Object object[] = currentMethodCall.get(currentMethodCall.size()-1).getLocalSymbols().toArray();
    		Obj obj = (Obj) object[0];
    		
    		if (obj.getName().equals("this") && currentActualParams.get(currentActualParams.size()-1) == 0) {
    			//ok
    		}
    		else {
    			//System.out.println(obj.getName() + " " + currentActualParams.get(currentActualParams.size()-1) );
    			report_error("Greska na liniji " + actPars.getLine() + ", nema dovoljan broj parametara za poziv funkcije: " + currentMethodCall.get(currentMethodCall.size()-1).getName()  , null);
    		}
    	}
    }

    public void visit(ExprListClass exprListClass) {	
    	Obj obj ;
    	Struct formParam ;
    	
    	if (currentMethod!=null && currentMethodCall.get(currentMethodCall.size()-1).getName().equals(currentMethod.getName())) {
    		Scope scope = Tab.currentScope();
    		Object object[] = scope.getLocals().symbols().toArray();
    		obj = (Obj) object[currentActualParams.get(currentActualParams.size()-1)];
    		formParam = obj.getType();
    		if (checkClassExtends(formParam,exprListClass.getExpr().struct)) {
    			// okej je
    		}
    		else if (!exprListClass.getExpr().struct.assignableTo(formParam)) {
	    		
	    		report_error("Greska na liniji " + exprListClass.getLine() + ", nekompatibilni parametri " + obj.getName()  , null);
	    	}
	    	else {
		    	if (currentActualParams.get(currentActualParams.size()-1) > currentMethodCall.get(currentMethodCall.size()-1).getLevel()) {
		    		report_error("Greska na liniji " + exprListClass.getLine() + ", vise parametara nego sto funkcija ima " + currentMethodCall.get(currentMethodCall.size()-1).getName() , null);
		    	}
	    	}
    	}
    	else
    	if (currentActualParams.get(currentActualParams.size()-1)< 0) {
    		
    		report_error("Greska na liniji " + exprListClass.getLine() + ", veci broj parametara od potrebnog za poziv funkcije: " + currentMethodCall.get(currentMethodCall.size()-1).getName(), null);
    	}
    	else {
	    	obj = (Obj) currentMethodCall.get(currentMethodCall.size()-1).getLocalSymbols().toArray()[currentActualParams.get(currentActualParams.size()-1)];	
	    	formParam = obj.getType();
	    	
	    	if (checkClassExtends(formParam,exprListClass.getExpr().struct)) {
    			// okej je
    		}
    		else
	    	if (!exprListClass.getExpr().struct.assignableTo(formParam)) {
	    		
	    		report_error("Greska na liniji " + exprListClass.getLine() + ", nekompatibilni parametri " + obj.getName()  , null);
	    	}
	    	else {
		    	if (currentActualParams.get(currentActualParams.size()-1) > currentMethodCall.get(currentMethodCall.size()-1).getLevel()) {
		    		report_error("Greska na liniji " + exprListClass.getLine() + ", vise parametara nego sto funkcija ima " + currentMethodCall.get(currentMethodCall.size()-1).getName() , null);
		    	}
	    	}
    	}
    	int x = currentActualParams.remove(currentActualParams.size()-1);
    	currentActualParams.add(x-1);
    }
    
    public void visit(SingleExpr singleExpr) {
    	if (currentMethod!=null && currentMethodCall.get(currentMethodCall.size()-1).getName().equals(currentMethod.getName())) {
    		Scope scope = Tab.currentScope();
    		Object object[] = scope.getLocals().symbols().toArray();
    		Obj obj = (Obj) object[currentActualParams.get(currentActualParams.size()-1)];
    		Struct formParam = obj.getType();
    		
    		if (checkClassExtends(formParam,singleExpr.getExpr().struct)) {
    			// okej je
    		}
    		else
    		if (!singleExpr.getExpr().struct.assignableTo(formParam)) {
	    		report_error("Greska na liniji " + singleExpr.getLine() + ", nekompatibilni parametri " + obj.getName()  , null);
	    	}
	    	else {
	    		//ok
	    	}
    	}
    	else if (currentActualParams.get(currentActualParams.size()-1) < 0 ) {
    		report_error("Greska na liniji " + singleExpr.getLine() + ", veci broj parametara od potrebnog za poziv funkcije: " + currentMethodCall.get(currentMethodCall.size()-1).getName()  , null);
        
    	}
    	else {
    		Obj obj = (Obj) currentMethodCall.get(currentMethodCall.size()-1).getLocalSymbols().toArray()[currentActualParams.get(currentActualParams.size()-1)];	
	    	Struct formParam = obj.getType();
	    	if (checkClassExtends(formParam, singleExpr.getExpr().struct)) {
    			// okej je
    		}
    		else if (!singleExpr.getExpr().struct.assignableTo(formParam)) {
	    		report_error("Greska na liniji " + singleExpr.getLine() + ", nekompatibilni parametri " + obj.getName()  , singleExpr);
	    	}
	    	else {
	    		//ok
	    	}
    	}
    	int x = currentActualParams.remove(currentActualParams.size()-1);
    	currentActualParams.add(x-1);
    }
    
    public void visit(CondTermC node) {
    	if (node.getCondFact().struct!=boolType)
    		report_error("Greska na liniji " + node.getLine() + " uslov nije bool tip " , null);
    	node.struct=boolType;
    }
    
    public void visit(CondFactExpr condFactExpr) {
    	
    	if (condFactExpr.getExpr().struct!=boolType)
    		report_error("Greska na liniji " + condFactExpr.getLine() + " uslov nije bool tip " , null);
    	
    	condFactExpr.struct = condFactExpr.getExpr().struct;
    }
    
    private boolean checkClassNull(Struct s1,Struct s2) {
    	
    	if (s1.getKind()==Struct.Class && s2==Tab.nullType)
    		return true;
    	
    	if (s1.getKind()==Struct.Array && s2==Tab.nullType)
    		return true;
    	
    	if (s2.getKind()==Struct.Class && s1==Tab.nullType)
    		return true;
    	
    	if (s2.getKind()==Struct.Array && s1==Tab.nullType)
    		return true;
    	
    	return false;
    }
    
    public void visit(CondFactExprRelopExpr node) {
    	
    	if ((node.getExpr().struct.getKind() == Struct.Array && node.getExpr1().struct==Tab.nullType) 
    			|| (node.getExpr1().struct.getKind()==Struct.Array && node.getExpr().struct==Tab.nullType)){
    		//ok
    	}
    	else if (checkClassNull(node.getExpr().struct, node.getExpr1().struct)){
    		
    	}
    	else if (node.getExpr().struct.equals(node.getExpr1().struct)){
    		
    	}
    	else if (!node.getExpr().struct.compatibleWith(node.getExpr1().struct)) {
    		report_error("Greska na liniji " + node.getLine() + ", nekompatibilni tipovi " , null);
    	}
    	else if ((node.getExpr().struct.getKind()==Struct.Array || node.getExpr().struct.getKind()==Struct.Class)) {
    		report_error("Greska na liniji " + node.getLine() + ", nekompatibilni tipovi " , null);
    	}
    	else {
    		//ok
    	}
    	
    	node.struct = boolType;
    }
    
    
    private boolean checkClassExtends(Struct left, Struct right) {
    	if (left.getKind()!=Struct.Class) {	//ako nije klasa levo ne proveravaj
    		return false;
    	}
    	if (right==Tab.nullType){
    		return true;
    	}
    	if (right==left){
    		return true;
    	}
    	Struct curr;
    	if (left.getKind()==Struct.Class){ // klasa
    		curr = right;
    		
    		while (curr!=null) { 			
    			if (left==curr) {
    				return true;
    			}
    			curr = curr.getElemType();
    		}
    	}
    	
    	return false;
    }
    
    
    public void visit(Assign assign){
    	
    	if (assign.getDesignator().obj.getType().getKind()==Struct.Class) {	//klasa i nadklasa mora i sve natklase i sve interfejse da se provere jbg
    		if (checkClassExtends(assign.getDesignator().obj.getType(),assign.getExpr().struct)) {
    			//ok
    		}
    		else {
    			report_error("Greska na liniji " + assign.getLine() + ", nekompatibilni tipovi u dodeli vrednosti!", null);
    		}
    	}
    	else if (assign.getDesignator().obj.getType().getKind()==Struct.Array && assign.getDesignator().obj.getType().getElemType().getKind()==Struct.Class)  {
    		Struct levo = assign.getDesignator().obj.getType().getElemType();
    		Struct desno = assign.getExpr().struct.getElemType();
    		//System.out.println(" E " + desno.getKind());
    		
    		if (levo==desno.getElemType() || levo==desno) {
    			//ok
    		}
    		else {
    			report_error("Greska na liniji " + assign.getLine() + ", nekompatibilni tipovi u dodeli vrednosti!", null);
    		}
    	}
    	else if ((assign.getDesignator().obj.getType().getKind()==Struct.Array || assign.getDesignator().obj.getType().getKind()==Struct.Array) && assign.getExpr().struct==Tab.nullType){
    		//ok
    	}
    	else if(!assign.getExpr().struct.assignableTo(assign.getDesignator().obj.getType())) {
    		//System.out.println(assign.getDesignator().obj.getType().getKind() +" " + assign.getExpr().struct.getKind());
    		report_error("Greska na liniji " + assign.getLine() + ", nekompatibilni tipovi u dodeli vrednosti!", null);
    	}
    	else {
    		//ok
    	}
    }
    //term kao prosledjivanje samo strukture da se vidi dal je kompatibilna
    public void visit(Term term){
    	
    	if (term.getMulopFactorList().struct!=null) {
    		term.struct = Tab.intType;	//int
    	}
    	else if (term.getMulopFactorList().struct!=null && !term.getFactor().struct.compatibleWith(term.getMulopFactorList().struct)){
    		report_error("Greska na liniji " + term.getLine() + ", nekompatibilni! ", null);
    		term.struct=Tab.noType;
    	}
    	else {
    		
    		term.struct = term.getFactor().struct;
    	}
    }
    
    public void visit(MulopFactorListClass node ) {
    	if (node.getFactor().struct!=Tab.intType) {
    		report_error("Greska na liniji " + node.getLine() + ", nekompatibilni tipovi ", null);
    		node.struct = Tab.noType;
    	}
    	else if (node.getMulopFactorList().struct!=null && node.getMulopFactorList().struct!=Tab.intType) {
    		report_error("Greska na liniji " + node.getLine() + ", nekompatibilni tipovi ", null);
    		node.struct = Tab.noType;
    	}
    	else {
    		node.struct = node.getFactor().struct; 
    	}
    }
    
    public void visit(NoMulopFactorList node) {
    	node.struct = null;
    }
    
    // new KLASA
    public void visit(NewFact newFact) {
    	Struct tip = newFact.getNewType().obj.getType();
    	
    	if (tip.getKind()!=Struct.Class) {
    		report_error("Greska na liniji " + newFact.getLine() + " tip nije klasa ", newFact);
    	}
    	else {
    		//ok
    		newFact.struct = tip;	
    	}
    }
    
    // new int[] .....
    public void visit(NewFactSquare newFactSquare) {
    	Struct tip = newFactSquare.getType().struct;
    	
    	if (newFactSquare.getExpr().struct!=Tab.intType) {
    		report_error("Greska na liniji " + newFactSquare.getLine() + " tip nije niz ", newFactSquare);
    	}
    	else {
    		//ok
    		newFactSquare.struct = new Struct(Struct.Array);
    		newFactSquare.struct.setElementType(tip);
    	}
    }
    
    public void visit(Designator designator){
    	designator.obj = currentDesignator.remove(currentDesignator.size()-1);	
    }
    
    public void visit(DesignatorName designatorName) {
    	designatorName.obj = Tab.find(designatorName.getName());
    	Obj obj = designatorName.obj;
    	boolean found = false;
    	int i;
    	if (obj == Tab.noObj && currentClass!=null) {
    		Object innerobjs[] =  currentClass.getType().getMembers().toArray();
    		for (i=0;i<innerobjs.length;i++) {
    			if (((Obj)innerobjs[i]).getName().equals(designatorName.getName())) {
    				found = true;		
    				obj = (Obj)innerobjs[i];
    				designatorName.obj = obj;
    				break;
    			}
    		}
    		
    		// roditelji
    		Struct pom = currentClass.getType();
    		while (!found && pom.getElemType()!=null) {
    			innerobjs =  pom.getElemType().getMembers().toArray();
        		for (i=0;i<innerobjs.length;i++) {
        			if (((Obj)innerobjs[i]).getName().equals(designatorName.getName())) {
        				found = true;
        				obj = (Obj)innerobjs[i];
        				designatorName.obj = obj;
        				break;
        			}
        		}
        		pom = pom.getElemType();
    		}
    		
    		if (found) {
    			//ok
    			report_info("Pretraga na liniji " +  designatorName.getLine() + " (" + designatorName.getName() +"), nadjeno ",null);
    		}
    		else {
    			designatorName.obj=Tab.noObj;
    			report_error("Greska na liniji " + designatorName.getLine()+ ", ime: "+ designatorName.getName() + " nije deklarisano! ", null);
    		}
    	}
    	
    	else if(obj == Tab.noObj){
    		designatorName.obj=Tab.noObj;
			report_error("Greska na liniji " + designatorName.getLine()+ ", ime: "+ designatorName.getName() + " nije deklarisano! ", null);
    	}
    	else {
    		//designatorName.obj=Tab.noObj;
    		designatorName.obj = obj;
    		report_info("Pretraga na liniji " +  designatorName.getLine() + " (" + designatorName.getName() +"), nadjeno ",null);
    	}
    	currentDesignator.add(obj);
    }
    
    // designator.SLEDECI
    public void visit(DesignatorSufixDot node) {
    	Obj obj = Tab.find(node.getName());
    	boolean found = false;
    	
    	int i;
    	
    	if (getCurrDes().getType().getKind()==Struct.Class){
    		
    		Object innerobjs[] =  getCurrDes().getType().getMembers().toArray();
    		for (i=0;i<innerobjs.length;i++) {
    			//System.out.println("imena u klasi: " + getCurrDes().getName() + "-> " + ((Obj)innerobjs[i]).getName());
    			if (((Obj)innerobjs[i]).getName().equals(node.getName())) {
    				found = true;
    				
    				resetCurrDes((Obj)innerobjs[i]);
    				
    				//System.out.println("osnovna: "  + getVarType(getCurrDes().getType().getKind()) + " "+ getCurrDes().getName() );
    				break;
    			}
    		}
    		
    		//System.out.println(found?"found = true":"found = false");
    		
    		if (!found && currentClass!= null && currentClass.getType() == currentDesignator.get(currentDesignator.size()-1).getType()) {
    			Obj curr = Tab.find(node.getName());
    			if (curr!=Tab.noObj) {
    				found = true;
    				resetCurrDes(curr);
    			}
    		}
    		Struct pom  = null;
    		if (!found) {
    			pom = getCurrDes().getType();
    			//System.out.println("e " + found + " " +getCurrDes().getName()  + " " + pom.getElemType());
    		}
    		
    		while (!found && pom.getElemType()!=null) {
    			
    			innerobjs =  pom.getElemType().getMembers().toArray();
    			
        		for (i=0;i<innerobjs.length;i++){
        			//System.out.println("nadklasa " + getCurrDes().getName() + " " + node.getName() + " " + ((Obj)innerobjs[i]).getName());
        			if (((Obj)innerobjs[i]).getName().equals(node.getName())) {
        				found = true;
        				
        				resetCurrDes((Obj)innerobjs[i]);
        				
        				//System.out.println("nadklasa " + getCurrDes().getName() + " " + getCurrDes().getType().getKind());
        				break;
        			}
        		}
        		pom = pom.getElemType();
    		}	
    		if (found) {
    			//ok
    			//report_info("\n\n\n\n >>>>>>>>>>>>>>>>>>>>>>>>>> 111Usao u klasu "+ getObjType(getCurrDes().getKind()) +" "+ getCurrDes().getName() /*+ " " + currentDesignator.get(currentDesignator.size()-1).getName()*/, null);
    			report_info("Pretraga na liniji " +  node.getLine() + " (" + node.getName() +"), nadjeno ",null);
    		}
    		else {
    			report_error("Greska na liniji " + node.getLine()+ ", ime: "+ node.getName() + " nije deklarisano u klasi! ", null);
    			resetCurrDes(Tab.noObj);
    		}	
    	}
    	else if (obj == Tab.noObj){
			report_error("Greska na liniji " + node.getLine()+ ", ime: "+ node.getName() + " nije deklarisano! ", null);
			resetCurrDes(Tab.noObj);
    	}    		
    }
    
    public void visit(DesignatorSufixSquare node) {
    	
    	if (getCurrDes().getType()!=null && getCurrDes().getType().getKind()!=Struct.Array) {
    		report_error("Greska na liniji " + node.getLine() + ", nije nizovni tip: " + getCurrDes().getName() , node);
    		resetCurrDes(Tab.noObj);
    	}
    	else
    	if (node.getExpr().struct!=Tab.intType) {
    		report_error("Greska na liniji " + node.getLine() + ", nije int tip unutar uglastih zagrada: " , null);
    		resetCurrDes(Tab.noObj);
    	}
    	else {
    		//ok
    		if (getCurrDes().getType().getElemType().getKind()==Struct.Class) {
    			resetCurrDes(new Obj(Obj.Elem,"%mytemp",getCurrDes().getType().getElemType()));
    		}
    		else {
    			resetCurrDes(new Obj(Obj.Elem,"%mytemp", getCurrDes().getType().getElemType()));
    		}
    	}
    }
    
    
    public boolean passed(){
    	return !errorDetected && mainVoid;
    }	
	
}
