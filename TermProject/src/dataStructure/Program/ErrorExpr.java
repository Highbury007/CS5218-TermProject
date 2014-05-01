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
public class ErrorExpr extends AbstractExpr {
	//private String value;
	
	public ErrorExpr() {
		// TODO Auto-generated constructor stub
		init(this);
	}

	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :Error!");
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		
	}
}
