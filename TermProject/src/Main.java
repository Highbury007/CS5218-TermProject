import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.lang.reflect.Constructor;



import java.util.List;
import java.util.Vector;

import batAnalyzer.BTAnalyzer;
import typeInferAnalyzer.WulAlgorithm;
import xml2Pgm.*;
import dataStructure.Program.Pgm;

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
		if(args.length == 0) {
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
		
		if(args.length == 1) {
			WulAlgorithm typeAnalyzer = new WulAlgorithm();
			typeAnalyzer.isTypeCheckPass(program);
			System.out.println("No Type Error!");
		}else {
			List<String> bTAInput = new Vector<String>();
			for(int i = 1; i < args.length; i ++) {
				bTAInput.add(args[i]);
			}
			
			BTAnalyzer bTAnalyzer = new BTAnalyzer();
			bTAnalyzer.analyzeBtv(program, bTAInput);
		}
	}
}
