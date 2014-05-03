package dataStructure;

import java.util.HashMap;
import dataStructure.Program.*;
import java.util.Map;

import dataStructure.Program.AbstractExpr;
import dataStructure.Type.AbstractAType;
import dataStructure.Type.*;

/**
 * @author WuJun A0106507M
 *
 */
public class ATypeFactory {
	
	private static Map<String, String> expr2AType;
	
	public ATypeFactory() {
		// TODO Auto-generated constructor stub
		expr2AType = new HashMap<String, String>();
		for(int i = 0; FactoryHelper.exprClassNames[i] != FactoryHelper.endFlag; i ++) {
			expr2AType.put(FactoryHelper.exprClassNames[i], FactoryHelper.typeClassNames[i]);
		}
	}
	
	public AbstractAType getATypeInstance(AbstractExpr source) {
		/*
		try {
			String typeClassName = expr2AType.get(source.toString().split(FactoryHelper.splitStr)[0]);
			if(typeClassName != null) {
				return (AbstractAType)Class.forName(typeClassName).newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		if(source instanceof FunDefExpr   || source instanceof IfExpr 
		|| source instanceof FunCallExpr  || source instanceof BinaryOpExpr 
		|| source instanceof VariableExpr || source instanceof BracketedExpr) {
			return new TVarAType();
		}else if(source instanceof NullListExpr || source instanceof CdrExpr
			  || source instanceof CarExpr      || source instanceof ConsExpr
			  || source instanceof NullListTestExpr ) {
			AbstractAType listT = new ListAType();
			AbstractAType listCore = new TVarAType();
			((ListAType) listT).setListCore(listCore);
			return listT;
		} 
		else {
			if(source instanceof BoolConstantExpr) {
				return BoolAType.getInstance();
			}else if(source instanceof NumConstantExpr) {
				return IntAType.getInstance();
			}
		}
		return null;
	}
}
