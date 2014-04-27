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
public class BinaryOpExpr extends AbstractExpr {
	
	private static final int exprNumC = 2;
	
	public BinaryOpExpr() {
		init();
	}
	
	public AbstractExpr getLeftOpd() {
		if(exprs.size() == exprNumC) {
			return exprs.get(0);
		}else {
			printError();
			return null;
		}
	}

	public void setLeftOpd(AbstractExpr leftOpd) {
		if(exprs.size() == exprNumC) {
			exprs.set(0, leftOpd);
		} else {
			printError();
		}
	}

	public AbstractExpr getRightOpd() {
		if(exprs.size() == exprNumC) {
			return exprs.get(1);
		} else {
			printError();
			return null;
		}
	}

	public void setRightOpd(AbstractExpr rightOpd) {
		if(exprs.size() == exprNumC) {
			exprs.set(1, rightOpd);
		}else {
			printError();
		}
	}
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		exprAttribute = new String();
		exprs = new Vector<AbstractExpr>();
		System.out.print(this.toString());
	}
	
	@Override
	protected String getExprAttribute() {
		// TODO Auto-generated method stub	
		return this.exprAttribute;
	}
	/*
	@Override
	protected void setAttributs(List<String> attrList) {
		// TODO Auto-generated method stub
		if(this.exprAttributes.size() == 1) {
			this.exprAttributes = attrList;
		}else {
			System.err.println(this.toString() + " :Attribute num should be 1");
		}
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
		if(exprs.size() == exprNum_c && aExprs.size() == exprNum_c) {
			this.exprs = aExprs;
		} 
		else {
			printError();
		}
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + ": Expression num should be" + exprNumC);		
	}
	
	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		this.exprAttribute = (xmlNode.getAttributes().item(0).getNodeValue());
		System.out.println(" :" + this.exprAttribute);
		Node entryNode = xmlNode.getFirstChild();
		
		while(entryNode != null) {
			NodeList nLst = entryNode.getChildNodes();
			for(int i = 0; i < nLst.getLength(); i ++) {
				AbstractExpr newExpr = builder.getExprInstance(nLst.item(i).getNodeName());
				if(newExpr != null) {
					this.exprs.add(newExpr);
					this.exprs.get(exprs.size() - 1).populateExpr(nLst.item(i), builder);
				}
			}
			entryNode = entryNode.getNextSibling(); 
		}
	}

}
