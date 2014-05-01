/**
 * 
 */
package dataStructure.Program;



import org.w3c.dom.Node;
import dataStructure.ExprFactory;

/**
 * @author Wu Jun A0106507M
 *
 */
public class ConsExpr extends AbstractExpr {

	private static final int exprNumC = 2;
		
	public ConsExpr() {
		// TODO Auto-generated constructor stub
		init(this);
	}

	public AbstractExpr getHeadExpr() {
		if(expressions.size() == exprNumC) {
			return expressions.get(0);
		}else {
			printError();
			return null;
		}

	}
	/*
	public void setHeadExpr(AbstractExpr headExpr) {
		if(expressions.size() == exprNumC) {
			expressions.set(0, headExpr);
		}else {
			printError();
		}
	}
	*/
	public AbstractExpr getTailExpr() {
		if(expressions.size() == exprNumC) {
			return expressions.get(1);
		}else {
			printError();
			return null;
		}
	}
	/*
	public void setTailExpr(AbstractExpr tailExpr) {
		if(expressions.size() == exprNumC) {
			expressions.set(1, tailExpr);
		}else {
			printError();
		}
	}
	*/
	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + " :Exression num should be " + exprNumC);
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		Node entryNode = xmlNode.getFirstChild();

		populateHelper(entryNode, builder, this);
		if(this.expressions.size() != exprNumC) {
			printError();
		}
		//System.out.println("");
	}

}
