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
public class IfExpr extends AbstractExpr {

	private static final int exprNumC = 3;
	
	public IfExpr() {
		init(this);
	}
	
	public AbstractExpr getCondition() {
		if(this.expressions.size() == exprNumC) {
			return expressions.get(0);
		}else {
			printError();
			return null;
		}
	}
	/*
	public void setCondition(AbstractExpr expr) {
		if(this.expressions.size() == exprNumC) {
			this.expressions.set(0, expr);
		}else {
			printError();
		}
	}
	*/
	public AbstractExpr getTBranch() {
		if(this.expressions.size() == exprNumC) {
			return expressions.get(1);
		}else {
			printError();
			return null;
		}
	}

	/*
	public void setTBranch(AbstractExpr tBranch) {
		if(this.expressions.size() == exprNumC) {
			expressions.set(1, tBranch);
		}else {
			printError();
		}
	}
	*/
	public AbstractExpr getFBranch() {
		if(this.expressions.size() == exprNumC) {
			return expressions.get(2);
		}else {
			printError();
			return null;
		}
	}
	/*
	public void setFBranch(AbstractExpr fBranch) {
		if(this.expressions.size() == exprNumC) {
			expressions.set(2, fBranch);
		}else {
			printError();
		}
	}
	*/
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
		//System.out.println("Domain: " + this.domain.toString());
	}	
}
