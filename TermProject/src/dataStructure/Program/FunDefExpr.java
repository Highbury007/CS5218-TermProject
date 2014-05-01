/**
 * 
 */
package dataStructure.Program;

import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Node;
import dataStructure.ExprFactory;

/**
 * @author Wu Jun A0106507M
 *
 */
public class FunDefExpr extends AbstractExpr {
	
	private static final int exprNumC = 1;
	
	public FunDefExpr() {
		// TODO Auto-generated constructor stub
		init(this);
		this.domain = this;
	}

	public String getFunName() {
		if(!exprAttribute.isEmpty()) {
			return this.exprAttribute;
		}else {
			printError();
			return null;
		}
	}
	
	public List<AbstractExpr> getFunVarList() {
		if(expressions.size() > exprNumC) {
			return this.expressions.subList(0, expressions.size() - 1);
		}else {
			System.err.println(this.toString() + " :Expression num should > " + exprNumC);
			return null;
		}
	}
	public AbstractExpr getFunBody() {
		if(expressions.size() > exprNumC) {
			return this.expressions.get(expressions.size() - 1);
		}else {
			System.err.println(this.toString() + " :Expression num should > " + exprNumC);
			return null;
		}
	}
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :FunName should not be empty");
	}
	
	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getAttributes().item(0).getNodeValue();
		//System.out.println(" :" + this.exprAttribute);
		//generate variables
		extractVarList(xmlNode);

		Node entryNode = xmlNode.getFirstChild();

		populateHelper(entryNode, builder, this);
	}
	
	private void extractVarList(Node xmlNode) {
		String[] result = xmlNode.getAttributes().item(1).getNodeValue().split(",");
		//System.out.println("result length: " + result.length);
		
		HashMap<String, AbstractExpr> table = new HashMap<String, AbstractExpr>();
		
		for(int i = 0; i < result.length; i ++ ) {
			AbstractExpr newExpr = null;
			if(!table.containsKey(result[i])) {
				newExpr = new VariableExpr();
				this.expressions.add(newExpr);
				newExpr.setDomain(this);
				((VariableExpr)newExpr).setVarName(result[i]);
				
				table.put(newExpr.getExprAttribute(), newExpr);

			}else {
				//newExpr = table.get(result[i]);
			}
		}
		//populate the symbol table	
		Pgm.symbolTables.put(this, table);
		//System.out.println("EntrySize " + Pgm.symbolTables.get(this).size());
	}
	
	//public Map<AbstractAType, AbstractAType> typeInfer(Map<AbstractExpr, AbstractAType> environemnt) {
		

	//}
}
