/*
 * 
 */

package dataStructure.Program;

import org.w3c.dom.Node;
import dataStructure.ExprFactory;

/**
 * @author Wu Jun A0106507M
 *
 */
public class DataDestructExpr extends AbstractExpr {

	/**
	 * 
	 */
	private static final int exprNumC = 1;

	public DataDestructExpr() {
		// TODO Auto-generated constructor stub
		init(this);
	}

	public String getName() {
		if(!exprAttribute.isEmpty()) {
			return this.exprAttribute;
		} else {
			System.err.println(this.toString() + " :Attribute should not be empty");
			return null;
		}
	}
	
	public void setName(String name) {
		this.exprAttribute = name;
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
		this.exprAttribute = xmlNode.getNodeName();
		Node entryNode = xmlNode.getFirstChild();

		populateHelper(entryNode, builder, this);
		if(this.expressions.size() != exprNumC) {
			printError();
		}
		//System.out.println("");
	}
}