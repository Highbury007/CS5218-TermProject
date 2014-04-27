package typeInferAnalyzer;

import dataStructure.ATypeFactory;
import dataStructure.Program.AbstractExpr;
import dataStructure.Program.Pgm;

/**
 * @author Wu Jun A0106507M
 *
 */
public class typeAnalyzer {

	private ATypeFactory typeBuilder;
	public typeAnalyzer() {
		// TODO Auto-generated constructor stub
		typeBuilder = new ATypeFactory();
	}
	
	private void pgmTypeInfoPopulate(Pgm program) {
		for(AbstractExpr e : program.getFunDefs()) {
			//.genExprAType(e, typeBuilder);
			//program.genExprAType(program, typeBuilder);
		}
	}
	
	public boolean typeCheck(Pgm program) {
		pgmTypeInfoPopulate(program);
		
		return true;
	}
	
	//return null to denote the identity
	
}
