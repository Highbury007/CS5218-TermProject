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
public class BracketedExpr extends AbstractExpr {

	private static final int exprNumC = 1;
	public BracketedExpr() {
		// TODO Auto-generated constructor stub
		init(this);
	}

	public AbstractExpr getExpr() {
		if(expressions.size() == exprNumC) {
			return expressions.get(0);
		}else {
			printError();
			return null;
		}
	}

	public void setExpr(AbstractExpr expr) {
		if(expressions.size() == exprNumC) {
			expressions.set(0, expr);
		}else {
			printError();
		}
	}

	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + ": Expression num should be " + exprNumC);
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		populateHelper(xmlNode, builder, this);
		
		if(this.expressions.size() != exprNumC) {
			System.err.println(this.toString() + " :the expression num should be " + exprNumC);
		}
	}
}
