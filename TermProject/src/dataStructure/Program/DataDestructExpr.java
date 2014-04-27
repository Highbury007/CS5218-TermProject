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
public class DataDestructExpr extends AbstractExpr {

	/**
	 * 
	 */
	private static final int exprNumC = 1;
	//private static final int attrNum_c = 1;
	//private static final String token = "body";

	public DataDestructExpr() {
		// TODO Auto-generated constructor stub
		init();
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
		//if(!exprAttribute.isEmpty()) {
		this.exprAttribute = name;
		//} else {
		//	System.err.println(this.toString() + " :Attribute should be " + attrNum_c);
		//}
	}
	
	public AbstractExpr getEpxr() {
		if(exprs.size() == exprNumC) {
			return exprs.get(0);
		}else {
			printError();
			return null;
		}
	}

	public void setExpr(AbstractExpr listHead) {
		if(exprs.size() == exprNumC) {
			exprs.set(0, listHead);
		}else {
			printError();
		}
	}
	
	@Override
	protected void init() {
		exprAttribute = new String();
		exprs = new Vector<AbstractExpr>();
		System.out.println(this.toString());
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
		if(this.exprAttributes.size() == attrNum_c) {
			this.exprAttributes = attrList;
		} else {
			System.err.println(this.toString() + " :Attribute num should be " + attrNum_c);
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
		System.err.println(this.toString() + " :Expression num should be " + exprNumC);
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getNodeName();
		Node entryNode = xmlNode.getFirstChild();

		while(entryNode != null) {
			NodeList nLst = entryNode.getChildNodes();
			for(int i = 0; i < nLst.getLength(); i ++) {
				AbstractExpr newExpr = builder.getExprInstance(nLst.item(i).getNodeName());
				if(newExpr != null) {
					this.getAbstractExprsRef().add(newExpr);
					this.getAbstractExprsRef().get(exprs.size() - 1).populateExpr(nLst.item(i), builder);
				}
			}
			entryNode = entryNode.getNextSibling();
		}
	}
}
