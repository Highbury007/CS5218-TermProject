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
public class NullListExpr extends AbstractExpr {

	public NullListExpr() {
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
		System.out.println(this.toString() + " :Attributes num should not be empty" );
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getNodeName();
		//System.out.println(" :" + this.exprAttribute);
	}
}
