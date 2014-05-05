package dataStructure.BTA;

import java.util.HashMap;
import java.util.Map;

import dataStructure.Program.AbstractExpr;

/**
 * @author WuJun A0106507M
 *
 */
public class BindingTimeEnv {

	/**
	 * 
	 */
	Map<AbstractExpr, AbstractValue> varExpr2AbsValue;
	
	public BindingTimeEnv() {
		// TODO Auto-generated constructor stub
		varExpr2AbsValue = new HashMap<AbstractExpr, AbstractValue>();
	}
	
	public AbstractValue getAbsValue(AbstractExpr varExpr) {
		if(varExpr != null) {
			return varExpr2AbsValue.get(varExpr);
		}else {
			System.err.println("AbstractExpr varExpr is null!");
			System.exit(1);
		}
		
		return null;
	}
	
	public void putAbsValue(AbstractExpr varExpr, AbstractValue absValue) {
		if(varExpr != null && absValue != null) {
			varExpr2AbsValue.put(varExpr, absValue);
		}else {
			System.err.println("AbstractExpr varExpr or AbstractValue is null!");
			System.exit(1);
		}
	}

}
