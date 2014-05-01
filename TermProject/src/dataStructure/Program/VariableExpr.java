package dataStructure.Program;

import org.w3c.dom.Node;

import dataStructure.ExprFactory;


/**
 * @author Wu Jun A0106507M
 *
 */
public class VariableExpr extends AbstractExpr{
	
	public VariableExpr() {
		// TODO Auto-generated constructor stub
		init(this);
	}
	public VariableExpr(String name) {
		init(this);
		this.exprAttribute = name;
	}
	public void setVarName(String name) {
		this.exprAttribute = name;
	}
	
	public String getVarName() {
		if(!this.exprAttribute.isEmpty()) {
			return this.exprAttribute;
		}else {
			printError();
			return null;
		}
	}

	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.out.println(this.toString() + " :Attriibute should not be empty");
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getAttributes().item(0).getNodeValue();
		//System.out.println("Domain: " + this.domain.toString());
		//System.out.println(" :" + this.exprAttribute);
	}
}
