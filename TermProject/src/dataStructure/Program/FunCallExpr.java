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
public class FunCallExpr extends AbstractExpr {
	
	//private static final int funNameNum_c = 1;
	public FunCallExpr() {
		init();
		// TODO Auto-generated constructor stub
	}
	
	public String getFunName() {
		if(!exprAttribute.isEmpty()) {
			return exprAttribute;
		}else {
			printError();
			return null;
		}
	}
	public void setFunName(String fName) {
		//if(exprAttribute.size() == funNameNum_c) {
		this.exprAttribute  = fName;
		//}else {
		//	printError();
		//}
		
	}
	public List<AbstractExpr> getArgList() {
		return this.exprs;
	}
	public void setArgList(List<AbstractExpr> argList) {
		this.exprs = argList;
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
		if(this.exprAttributes.size() == funNameNum_c && attrList.size() == funNameNum_c) {
			this.exprAttributes = attrList;
		}else {
			printError();
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
		this.exprs = aExprs;
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :Attribute should not be empty");
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getAttributes().item(0).getNodeValue();
		System.out.println(" :" + this.exprAttribute);
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
