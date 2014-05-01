/**
 * 
 */
package dataStructure.Program;

import org.w3c.dom.Node;
import dataStructure.ExprFactory;

/**
 * @author Wu Jun A0106507M
 *
 */
public class BoolConstantExpr extends AbstractExpr {
	
	
	public BoolConstantExpr() {
		init(this);
	}

	public String getBoolValue() {
		if(!exprAttribute.isEmpty()) {
			return exprAttribute;
		} else {
			printError();
			return null;
		}
	}

	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + ": Attribute should not be empty");
	}
	
	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getAttributes().item(0).getNodeValue();
		//System.out.println(" :" + this.exprAttribute);
	}

}
