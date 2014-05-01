import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.lang.reflect.Constructor;

import typeInferAnalyzer.WulAlgorithm;
import dataStructure.Program.Pgm;
import Xml2Pgm.*;

/**
 * @author OrangeR
 *
 */

public class Main {

	/**
	 * @param args
	 * @throws SAXException 
	 * @throws TransformerFactoryConfigurationError 
	 * @throws TransformerException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(!(args.length == 1 || args.length == 3)) {
			System.err.println("Usage: java input-file-path static/dynamic static/dynamic");
			System.exit(1);
		}
		
		File xmlInput = new File(args[0]);
		if(!xmlInput.exists()) {
			System.err.println("file " + xmlInput + " does not exist.");
			System.exit(1);
		}else {
			System.out.println(xmlInput.toString());
		}
		
		Pgm program = null;
		Xml2PgmParser transfer = new Xml2PgmParser();
		try {
			program = transfer.generatePgm(xmlInput);
		}catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		
		WulAlgorithm typeAnalyzer = new WulAlgorithm();
		typeAnalyzer.isTypeCheckPass(program);
		//System.out.println(program.getFunDefs().get(0).toString());
		//Pgm program1 = new Pgm();
		//System.out.println(program.toString());
		//System.out.println(program1.toString());
		//System.out.println(program == program1);
		//program1 = program;
		//System.out.println(program.toString());
		//System.out.println(program1.toString());
		//System.out.println(program == program1);
		//String str1 = new String("str1");
		//String str2 = new String("str2");
		//str1 = str2;
		//System.out.println();
		/*Singleton Test

		try{
			Constructor con = IntAType.class.getDeclaredConstructor();
			con.setAccessible(true);
			IntAType iAT1 = (IntAType)con.newInstance();
			IntAType iAT2 = (IntAType)con.newInstance();
			IntAType iAT3 = IntAType.getInstance();
			IntAType iAT4 = IntAType.getInstance();
			System.out.println(iAT1.toString());
			System.out.println(iAT2.toString());
			System.out.println(iAT3.toString());
			System.out.println(iAT4.toString());
			System.out.println(iAT1.equals(iAT2));
			System.out.println(iAT3.equals(iAT4));
		}catch (Exception e) {
			e.printStackTrace();
		}
		*/
		/*
		AbstractAType t1 = new IntAType();
		AbstractAType t2 = new BoolAType();
		System.out.println("t1:" + t1.toString());
		System.out.println("t2:" + t2.toString());
		t1 = t2;
		System.out.println("t1:" + t1.toString());
		*/
	}
}
