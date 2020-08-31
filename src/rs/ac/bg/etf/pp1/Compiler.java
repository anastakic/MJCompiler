package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;

public class Compiler {

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void removeLineFromFile(String file) throws IOException {
		List<String> lines = new LinkedList<String>();
		File inFile = new File(file);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		br.close();
		PrintStream ps = new PrintStream(inFile);
		ps.println("");		
		int i = 0;
		while(i < lines.size()) {
			if(lines.get(i).contains("ERROR"))
			ps.println(lines.get(i++));
			else i++;
		}
		ps.close();
	}
	
	
	public static void main(String[] args) throws Exception {
    	boolean err = false;
		Logger log = Logger.getLogger(Compiler.class);
		File sourceCode = new File(args[0]);
	
		if (!sourceCode.exists()) {
			log.error("Source file [" + sourceCode.getAbsolutePath() + "] not found!");
			return;
		}
		if (args.length < 1) {
			log.error("Not enough arguments supplied! Usage: MJParser <source-file> <obj-file> ");
			return;
		}
		
		/////////////////////////////////////////////////////////////////////////////
		/////////////////////// pravljenje fajla ////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////////
		PrintStream psOutFile = null; 
		
		String fileName = "";
		File outFile = null;
		if(args.length == 3) {
			int x = args[0].lastIndexOf('/');
			x++;

			fileName = args[0].substring(x, args[0].length());
			
			if(args[2].equals("izlaz.out")) {
				String pom = "test/out/" + fileName;
				fileName = pom;
				fileName = fileName.replaceAll("mj", "out");
			}
			else if(args[2].equals("izlaz.err")) {
				String pom = "test/err/" + fileName;
				fileName = pom;
				fileName = fileName.replaceAll("mj", "err");
				err = true;
			}
			System.out.print(fileName);
			outFile = new File(fileName);
			if (outFile.exists())
	        	outFile.delete();
			
			if(args[2].endsWith(".out")) {
				psOutFile = new PrintStream(outFile); 
				psOutFile.print("\n======================= BYTECODE: =======================\n");
		    	System.setOut(psOutFile);
			}
			else if(args[2].endsWith(".err")) {
				//log.addAppender(new FileAppender(new SimpleLayout(), outFile.getAbsolutePath()));
			}
		}
		
		
		/////////////////////////////////////////////////////////////////////////////
		
		log.info("Compiling source file: " + sourceCode.getAbsolutePath());
		
		try (BufferedReader br = new BufferedReader(new FileReader(sourceCode))) {
			
			Yylex lexer = new Yylex(br);
			MJParser p = new MJParser(lexer);
			if(err) {
				p.log.addAppender(new FileAppender(new SimpleLayout(), outFile.getAbsolutePath()));
			}
			Symbol s = p.parse();  //pocetak parsiranja
	        Program prog = (Program)(s.value);
	       	
	        /////////////////////////////////////////////////////////////////////////////////
	        ////trenutno ispisano u fajlu premesti u bufrd, da bi se upisale i semanticke greske
	        /////////////////////////////////////////////////////////////////////////////////
	        
	        List<String> lines = new LinkedList<String>();
	        if(err) {
	        	removeLineFromFile(outFile.getAbsolutePath());
				BufferedReader bufrd = new BufferedReader(new FileReader(outFile.getAbsolutePath()));
				String line = null;
				while ((line = bufrd.readLine()) != null) {
					lines.add(line);
				}
				bufrd.close();
				PrintStream ps = new PrintStream(outFile);
				ps.println("");		
				ps.close();
		        
	        }
	        SemanticAnalyzer semanticCheck = new SemanticAnalyzer();
	        
	        if(err) {
				semanticCheck.log.addAppender(new FileAppender(new SimpleLayout(), outFile.getAbsolutePath()));
			}
	        	
        	log.info(prog.toString(""));
	        log.info("=================================================================================");
			Tab.init(); // Universe scope
			prog.traverseBottomUp(semanticCheck);
			//Tab.dump();
			
			if(err){
	        	removeLineFromFile(outFile.getAbsolutePath());
	        	
	        	List<String> linesSem = new LinkedList<String>();
	        	BufferedReader bufrd = new BufferedReader(new FileReader(outFile.getAbsolutePath()));
				String line = null;
				while ((line = bufrd.readLine()) != null) {
					linesSem.add(line);
				}
				bufrd.close();
				PrintStream ps = new PrintStream(outFile);
				ps.println("");		
				int i = 0;
				if(lines.size() != 0) ps.print("======================== SINTAKSNE GRESKE: =========================\n");
				else ps.print("Nema sintaksnih gresaka!:\n");
				
				while(i < lines.size()) {
					ps.println(lines.get(i++));
				}
				i = 0;
				if(linesSem.size() != 0)ps.print("\n======================== SEMANTICKE GRESKE: ========================\n");
				else ps.print("Nema semantickih gresaka!\n");
				
				while(i < linesSem.size()) {
					ps.println(linesSem.get(i++));
				}
				ps.close();
	        }
	        
	        if (!p.errorDetected && semanticCheck.passed()) {
	        	log.info("\n\n------ Parsiranje uspesno zavrseno! ------\n");

	        	File objFile = new File(args[1]);
	        	log.info("\nGenerating bytecode file: " + objFile.getAbsolutePath());
	        	if (objFile.exists())
	        		objFile.delete();
	        	
	        	//Code generation...
	        	//CodeGenerator codeGenerator = new CodeGenerator();
	        	CodeGenerator codeGenerator = new CodeGenerator();
	        	//codeGenerator.varCount = semanticCheck.nVars;
	        	//codeGenerator.virtualTableStart = semanticCheck.nVars;
	        	prog.traverseBottomUp(codeGenerator);
	        	
	        	
		        Code.dataSize = semanticCheck.nVars;
	        	//Code.dataSize = codeGenerator.virtualTableStart;
	        	Code.mainPc = codeGenerator.getMainPc();
	        	Code.write(new FileOutputStream(objFile));
	        		
	        	
	        	String[] arg = {"test/program.obj" };
	        	rs.etf.pp1.mj.runtime.disasm.main(arg);
	        	rs.etf.pp1.mj.runtime.Run.main(arg);
	        	
	        	//for(int i = 0; i < Code.buf.length; i++)
	        	//System.out.println(Code.buf[i]);
	        	
	        }
	        else {
	        	log.error("\n\nxxxxxx Parsiranje NIJE uspesno zavrseno! xxxxxx\n");
	        }
		}
	}
}
