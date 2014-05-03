package xml2Pgm;
import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import dataStructure.*;
import dataStructure.Program.AbstractExpr;
import dataStructure.Program.Pgm;
/**
 * @author WuJun A0106507M
 *
 */
public class Xml2PgmParser {
		
	private Pgm program;
	private ExprFactory exprBuilder;
	
	public Xml2PgmParser() {
		program = new Pgm();
		exprBuilder = new ExprFactory();
	}

	public Pgm generatePgm(File file) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();			
		Document doc = parser.parse(file);
		
		NodeList nLst = doc.getDocumentElement().getChildNodes();
		for(int i = 0; i < nLst.getLength(); i ++) {
			//System.out.println(doc.getDocumentElement().getChildNodes().item(i).getNodeName());
			AbstractExpr newExpr = exprBuilder.getExprInstance(nLst.item(i).getNodeName());
			if(newExpr != null) {
				program.getFunDefs().add(newExpr);
				program.getFunDefs().get(program.getFunDefs().size() - 1).populateExpr(nLst.item(i), exprBuilder);
				System.out.println("----------------------------------");
			}
		}
		
		//System.out.println(FunDefExpr.class.toString().split(" ")[1]);
		return program;
	}
}
