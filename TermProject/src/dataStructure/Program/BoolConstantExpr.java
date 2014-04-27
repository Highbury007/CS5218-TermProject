/**
 * 
 */
package dataStructure.Program;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Node;

import dataStructure.ATypeFactory;
import dataStructure.ExprFactory;
import dataStructure.Type.AbstractAType;

/**
 * @author Wu Jun A0106507M
 *
 */
public class BoolConstantExpr extends AbstractExpr {

	//private static final int boolValueNumC = 1;

	public BoolConstantExpr() {
		init();
	}

	public String getBoolValue() {
		if(!exprAttribute.isEmpty()) {
			return exprAttribute;
		} else {
			printError();
			return null;
		}
	}
	/*
	public void setBoolValue(String value) {
		if(exprAttribute.size() == boolValueNumC) {
			exprAttribute.set(0, value);
		}else {
			printError();
		}
	}
	*/
	@Override
	protected void init() {
		exprAttribute = new String();
		//exprs = new Vector<AbstractExpr>();
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
		if(exprAttributes.size() == boolValueNum_c && attrList.size() == boolValueNum_c) {
			this.exprAttributes = attrList;
		}else {
			printError();
		}
	}
	*/
	@Override
	public List<AbstractExpr> getAbstractExprsRef() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " No Sub-expressions");
		return null;
	}
	/*
	@Override
	protected void setAbstractExprs(List<AbstractExpr> aExprs) {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " No Sub-expressions");
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + ": Attribute should not be empty");
	}
	
	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getAttributes().item(0).getNodeValue();
		System.out.println(" :" + this.exprAttribute);
	}

}
