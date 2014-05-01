/**
 * 
 */
package dataStructure.Program;

import java.util.List;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dataStructure.ExprFactory;

/**
 * @author Wu Jun A0106507M
 *
 */
public abstract class AbstractExpr{

	protected String exprAttribute;
	protected FunDefExpr domain;
	protected List<AbstractExpr> expressions;
	public static final String symbolkeyWord = "variable";
	//protected int exprNumC;

	protected void init(AbstractExpr e) {
		domain = null;
		exprAttribute = new String();
		expressions = new Vector<AbstractExpr>();
		//System.out.println(e.toString());
	};
		
	public String getExprAttribute() {
		return this.exprAttribute;
	}
	
	protected void setDomain(FunDefExpr expr) throws NullPointerException{
		try {
			if(expr != null)
				this.domain = expr;
			else {
				System.err.println("Domain should not be null!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<AbstractExpr> getAbstractExpressions() {
		return this.expressions;
	}
		
	protected abstract void printError();
	public abstract void populateExpr(Node xmlNode, ExprFactory builder);
	
	public void populateHelper(Node entryNode, ExprFactory builder, AbstractExpr expr) {
		while(entryNode != null) {
			NodeList nLst = entryNode.getChildNodes();
			for(int i = 0; i < nLst.getLength(); i ++) {
				AbstractExpr newExpr = null;
				
				if(nLst.item(i).getNodeName().equals(AbstractExpr.symbolkeyWord)) {
					String varName = nLst.item(i).getAttributes().item(0).getNodeValue();
					//System.out.println(varName);
					if(Pgm.getSymbol(expr.domain, varName) != null) {
						newExpr = Pgm.getSymbol(expr.domain, varName);
						//System.out.println(newExpr.toString());
					}
				}else {
						newExpr = builder.getExprInstance(nLst.item(i).getNodeName());					
				}
				
				if(newExpr != null) {
					expr.expressions.add(newExpr);
					//set the function domain
					newExpr.setDomain(expr.domain);
					newExpr.populateExpr(nLst.item(i), builder);

				}
			}
			entryNode = entryNode.getNextSibling(); 
		}
	}
}
