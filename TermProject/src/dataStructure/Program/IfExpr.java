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
public class IfExpr extends AbstractExpr {

	private static final int exprNum_c = 3;
	//private static final String token = "conditional";
	
	public IfExpr() {
		init();
	}
	
	public AbstractExpr getCondition() {
		if(this.exprs.size() == exprNum_c) {
			return exprs.get(0);
		}else {
			printError();
			return null;
		}
	}
	
	public void setCondition(AbstractExpr expr) {
		if(this.exprs.size() == exprNum_c) {
			this.exprs.set(0, expr);
		}else {
			printError();
		}
	}

	public AbstractExpr getTBranch() {
		if(this.exprs.size() == exprNum_c) {
			return exprs.get(1);
		}else {
			printError();
			return null;
		}
	}

	public void setTBranch(AbstractExpr tBranch) {
		if(this.exprs.size() == exprNum_c) {
			exprs.set(1, tBranch);
		}else {
			printError();
		}
	}

	public AbstractExpr getFBranch() {
		if(this.exprs.size() == exprNum_c) {
			return exprs.get(2);
		}else {
			printError();
			return null;
		}
	}

	public void setFBranch(AbstractExpr fBranch) {
		if(this.exprs.size() == exprNum_c) {
			exprs.set(2, fBranch);
		}else {
			printError();
		}
	}

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
		// TODO Auto-generated method stub
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
		if(this.exprs.size() == exprNum_c) {
			this.exprs = aExprs;
		}else {
			printError();
		}
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :Expression num should be " + exprNum_c);
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		Node entryNode = xmlNode.getFirstChild();		
		while(entryNode != null) {
			NodeList nodeLst = entryNode.getChildNodes();
			for(int i = 0; i < nodeLst.getLength(); i ++) {
				AbstractExpr newExpr = builder.getExprInstance(nodeLst.item(i).getNodeName());
				if(newExpr != null) {
					this.getAbstractExprsRef().add(newExpr);
					this.getAbstractExprsRef().get(exprs.size() - 1).populateExpr(nodeLst.item(i), builder);
				}
			}
			entryNode = entryNode.getNextSibling();
		}	
	}	
}
