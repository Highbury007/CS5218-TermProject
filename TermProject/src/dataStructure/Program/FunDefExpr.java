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
public class FunDefExpr extends AbstractExpr {
	
	//private static final int funNameNum_c = 1;
	private static final int exprNumC = 1;
	//private static final String token = "body";
	
	public FunDefExpr() {
		// TODO Auto-generated constructor stub
		init();
	}

	public String getFunName() {
		if(!exprAttribute.isEmpty()) {
			return this.exprAttribute;
		}else {
			printError();
			return null;
		}
	}
	/*
	public void setFunName(String fName) {
		if(this.exprAttributes.size() >= funNameNum_c) {
			this.exprAttributes.set(0, fName);
		}else {
			printError();
		}
	}
	*/
	/*
	public List<String> getVarList() {
		if(exprAttribute.size() >=  funNameNum_c) {
			return this.exprAttribute.subList(1, exprAttribute.size() - 1);
		}else {
			printError();
			return null;
		}
	}

	public void setVarList(List<String> varList) {
		if(this.exprAttribute.size() - 1 == varList.size() 
				&& this.exprAttribute.size() >= funNameNum_c) {
			for(int i = 0; i < varList.size(); i ++) {
				this.exprAttribute.set(i + 1, varList.get(i));
			}
		}else {
			System.err.println(this.toString() + ": different size error!");
		}
	}
	*/
	public AbstractExpr getFunBody() {
		if(exprs.size() > exprNumC) {
			return this.exprs.get(exprs.size() - 1);
		}else {
			System.err.println(this.toString() + " :Expression num should > " + exprNumC);
			return null;
		}
	}
	/*
	public void setFunBody(AbstractExpr fBody) {
		if(exprs.size() == exprNum_c) {
			this.exprs.set(0, fBody);
		}else {
			System.err.println(this.toString() + " :Expression num should be " + exprNum_c);
		}
	}
	*/
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
		if(this.exprAttributes.size() >= funNameNum_c) {
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
		if(this.exprs.size() == exprNum_c) {
			this.exprs = aExprs;
		}else {
			System.out.println(this.toString() + " :Expression num should be " + exprNum_c);
		}
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :FunName should not be empty");
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getAttributes().item(0).getNodeValue();
		System.out.println(" :" + this.exprAttribute);
		extractVarList(xmlNode);

		Node entryNode = xmlNode.getFirstChild();

		while(entryNode != null) {
			NodeList nodeLst = entryNode.getChildNodes();
			for(int i = 0; i < nodeLst.getLength(); i ++) {
				AbstractExpr newExpr = builder.getExprInstance(nodeLst.item(i).getNodeName());
				if (newExpr != null) {
					this.getAbstractExprsRef().add(newExpr);
					this.getAbstractExprsRef().get(exprs.size() - 1).populateExpr(nodeLst.item(i), builder);
				}
			}
			entryNode = entryNode.getNextSibling();
		}
	}
	
	private void extractVarList(Node xmlNode) {
		String[] result = xmlNode.getAttributes().item(1).getNodeValue().split(","); 
		List<AbstractExpr> tempExprs = exprs;
		for(int i = 0; i < result.length; i ++ ) {
			//this.exprAttribute.add(result[i]);
			AbstractExpr newExpr = new VariableExpr();
			tempExprs.add(newExpr);
			((VariableExpr)newExpr).setVarName(result[i]);
			//System.out.println(result[i]);
			//tempExrps = 
			
		}
	}
}
