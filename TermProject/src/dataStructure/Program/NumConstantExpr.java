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
public class NumConstantExpr extends AbstractExpr {

	public NumConstantExpr() {
		// TODO Auto-generated constructor stub
		init(this);
	}

	public String getValue() {
		if(!this.exprAttribute.isEmpty()) {
			return this.exprAttribute;
		}else {
			printError();
			return null;
		}
	}

	public void setValue(String value) {
		this.exprAttribute = value;
	}

	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :Attribute should not be empty");
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getAttributes().item(0).getNodeValue();
		//System.out.println(" :" + this.exprAttribute);
	}
}
