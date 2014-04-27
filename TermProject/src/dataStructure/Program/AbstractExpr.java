/**
 * 
 */
package dataStructure.Program;

import java.util.List;

import org.w3c.dom.Node;

import dataStructure.ExprFactory;
import dataStructure.ExprTypeInfoInferable;
import dataStructure.Type.AbstractAType;

/**
 * @author Wu Jun A0106507M
 *
 */
public abstract class AbstractExpr implements ExprTypeInfoInferable{
	protected String exprAttribute;
	protected List<AbstractExpr> exprs;
	//protected AbstractAType exprAType;
	protected abstract void init();
	//protected void init(AbstractExpr e){
	//	exprAttribute = new String();
		
	//};
	protected abstract String getExprAttribute();
	//protected abstract void setAttributs(List<String> attrList);
	public abstract List<AbstractExpr> getAbstractExprsRef();
	//protected abstract void setAbstractExprs(List<AbstractExpr> aExprs);
	protected abstract void printError();
	//public abstract const List<AbstractExpr> getAbstractExprs();
	public abstract void populateExpr(Node xmlNode, ExprFactory builder);
	//@Override
	//public String toString() {
	
	//}
}
