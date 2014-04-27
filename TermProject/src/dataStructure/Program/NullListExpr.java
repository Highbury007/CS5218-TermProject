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
public class NullListExpr extends AbstractExpr {

	//private static final int attrNum_c = 1;
	
	public NullListExpr() {
		init();
		// TODO Auto-generated constructor stub
	}

	public String getValue() {
		if(!this.exprAttribute.isEmpty()) {
			return this.exprAttribute;
		}else {
			printError();
			return null;
		}
	}

	public void setValue(String value) {
		this.exprAttribute = value;
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
		if(this.exprAttributes.size() == attrNum_c) {
			this.exprAttributes = attrList;
		}else {
			printError();
		}
	}
	*/
	@Override
	public List<AbstractExpr> getAbstractExprsRef() {
		// TODO Auto-generated method stub
		System.out.println(this.toString() + " :No Expression");
		return null;
	}
	/*
	@Override
	protected void setAbstractExprs(List<AbstractExpr> aExprs) {
		// TODO Auto-generated method stub
		System.out.println(this.toString() + " :No Expression");
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.out.println(this.toString() + " :Attributes num should not be empty" );
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		this.exprAttribute = xmlNode.getNodeName();
		System.out.println(" :" + this.exprAttribute);
	}
}
