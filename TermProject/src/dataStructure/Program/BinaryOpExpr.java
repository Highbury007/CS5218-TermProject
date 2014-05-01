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
public class BinaryOpExpr extends AbstractExpr {
	
	private static final int exprNumC = 2;
	
	public BinaryOpExpr() {
		init(this);
	}
	
	public String getOpt() {
		return this.exprAttribute;
	}
	
	public AbstractExpr getLeftOpd() {
		if(expressions.size() == exprNumC) {
			return expressions.get(0);
		}else {
			printError();
			return null;
		}
	}

	public void setLeftOpd(AbstractExpr leftOpd) {
		if(expressions.size() == exprNumC) {
			expressions.set(0, leftOpd);
		} else {
			printError();
		}
	}

	public AbstractExpr getRightOpd() {
		if(expressions.size() == exprNumC) {
			return expressions.get(1);
		} else {
			printError();
			return null;
		}
	}

	public void setRightOpd(AbstractExpr rightOpd) {
		if(expressions.size() == exprNumC) {
			expressions.set(1, rightOpd);
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
		this.exprAttribute = (xmlNode.getAttributes().item(0).getNodeValue());
		System.out.println(" :" + this.exprAttribute);
		Node entryNode = xmlNode.getFirstChild();
		populateHelper(entryNode, builder, this);
		//System.out.println(this.expressions.size());
		if(this.expressions.size() != exprNumC) {
			printError();
		}
	}

}
