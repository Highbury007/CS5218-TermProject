/**
 * 
 */
package dataStructure.Program;

import java.util.List;
import org.w3c.dom.Node;
import dataStructure.ExprFactory;

/**
 * @author Wu Jun A0106507M
 *
 */
public class FunCallExpr extends AbstractExpr {
	
	public FunCallExpr() {
		init(this);
	}
	
	public String getFunName() {
		if(!exprAttribute.isEmpty()) {
			return exprAttribute;
		}else {
			printError();
			return null;
		}
	}
	/*
	public void setFunName(String fName) {
		this.exprAttribute  = fName;
	}
	*/
	public List<AbstractExpr> getArgList() {
		return this.expressions;
	}
	/*
	public void setArgList(List<AbstractExpr> argList) {
		this.expressions = argList;
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :Attribute should not be empty");
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getAttributes().item(0).getNodeValue();
		//generate a function variable
		//AbstractExpr funVar = new VariableExpr(this.exprAttribute);
		//this.expressions.add(funVar);
		
		//System.out.println(" :" + this.exprAttribute);
		Node entryNode = xmlNode.getFirstChild();

		populateHelper(entryNode, builder, this);
		//System.out.println("");
	}

}
