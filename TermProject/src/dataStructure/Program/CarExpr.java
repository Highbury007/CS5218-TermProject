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
public class CarExpr extends AbstractExpr {

	private static final int exprNumC = 1;

	public CarExpr() {
		init(this);
	}
	
	public AbstractExpr getEpxr() {
		if(expressions.size() == exprNumC) {
			return expressions.get(0);
		}else {
			printError();
			return null;
		}
	}

	public void setExpr(AbstractExpr listHead) {
		if(expressions.size() == exprNumC) {
			expressions.set(0, listHead);
		}else {
			printError();
		}
	}
	
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :Expression num should be " + exprNumC);
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		Node entryNode = xmlNode.getFirstChild();

		populateHelper(entryNode, builder, this);
		
		if(this.expressions.size() != exprNumC) {
			printError();
		}

	}
}
