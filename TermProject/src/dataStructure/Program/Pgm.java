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
public class Pgm extends AbstractExpr{
	//private List<AbstractExpr> funDefinitions;

	public Pgm () {
		//funDefinitions = new Vector<AbstractExpr>();
		init();
	}
	
	//public void addFunDef(AbstractExpr def) {
	//	funDefinitions.add(def);
	//}
	
	public List<AbstractExpr> getFunDefs() {
		return this.exprs;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		this.exprs = new Vector<AbstractExpr>();
	}

	@Override
	protected String getExprAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AbstractExpr> getAbstractExprsRef() {
		// TODO Auto-generated method stub
		return this.exprs;
	}

	@Override
	protected void printError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void populateExpr(Node xmlNode, ExprFactory builder) {
		// TODO Auto-generated method stub
		
	}
}
