/**
 * 
 */
package dataStructure.Program;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
/**
 * @author Wu Jun A0106507M
 *
 */
public class Pgm{
	private List<AbstractExpr> funDefinitions;
	//public static Map<FunDefExpr, HashMap<String, AbstractExpr>> symbolTables;
	
	public static Map<FunDefExpr, HashMap<String, AbstractExpr>> symbolTables;
	public Pgm () {
		funDefinitions = new Vector<AbstractExpr>();
		symbolTables = new HashMap<FunDefExpr, HashMap<String, AbstractExpr>>();
	}
	
	public static AbstractExpr getSymbol(FunDefExpr e, String symbolName) {
		return symbolTables.get(e).get(symbolName);
	}
	//public void populateSymbolTables(FunDefExpr e, HashMap<String, AbstractExpr> table) {
	//}
	
	public List<AbstractExpr> getFunDefs() {
		return this.funDefinitions;
	}
}
