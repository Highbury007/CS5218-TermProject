/**
 * 
 */
package dataStructure.Program;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dataStructure.ATypeFactory;
import dataStructure.ExprFactory;
import dataStructure.Type.AbstractAType;

/**
 * @author Wu Jun A0106507M
 *
 */
public class BracketedExpr extends AbstractExpr {

	private static final int exprNumC = 1;
	public BracketedExpr() {
		// TODO Auto-generated constructor stub
		init();
	}

	public AbstractExpr getExpr() {
		if(exprs.size() == exprNumC) {
			return exprs.get(0);
		}else {
			printError();
			return null;
		}
	}

	public void setExpr(AbstractExpr expr) {
		if(exprs.size() == exprNumC) {
			exprs.set(0, expr);
		}else {
			printError();
		}
	}

	/* (non-Javadoc)
	 * @see dataStructure.AbstractExpr#init()
	 */
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		exprAttribute = new String();
		exprs = new Vector<AbstractExpr>();
		System.out.println(this.toString());
	}

	@Override
	protected String getExprAttribute() {
		// TODO Auto-generated method stub
		System.out.println(this.toString() + " :No attributes!");
		return null;
	}
	/*
	@Override
	protected void setAttributs(List<String> attrList) {
		System.out.println(this.toString() + " :No attributes!");
	}
	*/
	@Override
	public List<AbstractExpr> getAbstractExprsRef() {
		// TODO Auto-generated method stub
		return this.exprs;
	}
	/*
	@Override
	protected void setAbstractExprs(List<AbstractExpr> aExprs) {
		// TODO Auto-generated method stub
		if(exprs.size() == exprNum_c) {
			exprs = aExprs;
		}else {
			printError();
		}
		
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + ": Expression num should be " + exprNumC);
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		NodeList nLst = xmlNode.getChildNodes();
		for(int i = 0; i < nLst.getLength(); i ++) {
			AbstractExpr newExpr = builder.getExprInstance(nLst.item(i).getNodeName());
			if(newExpr != null) {
				this.getAbstractExprsRef().add(newExpr);
				this.getAbstractExprsRef().get(exprs.size() - 1).populateExpr(nLst.item(i), builder);
			}
		}
	}
}
