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
public class NullListTestExpr extends AbstractExpr {

	private static final int exprNumC = 1;
	
	public NullListTestExpr() {
		// TODO Auto-generated constructor stub
		init(this);
	}

	public AbstractExpr getExpr() {
		if(this.expressions.size() == exprNumC) {
			return this.expressions.get(0);
		}else {
			printError();
			return null;
		}
	}

	public void setExpr(AbstractExpr expr) {
		if(this.expressions.size() == exprNumC) {
			this.expressions.set(0, expr);
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
		//System.out.println(" :" + this.expressions.get(0).toString());
	}
}
