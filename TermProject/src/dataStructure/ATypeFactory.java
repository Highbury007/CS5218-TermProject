package dataStructure;

import java.util.HashMap;
import java.util.Map;

import dataStructure.Type.AbstractAType;

/**
 * @author WuJun A0106507M
 *
 */
public class ATypeFactory {
	
	private Map<String, String> expr2AType;
	
	public ATypeFactory() {
		// TODO Auto-generated constructor stub
		expr2AType = new HashMap<String, String>();
		for(int i = 0; FactoryHelper.exprClassNames[i] != FactoryHelper.endFlag; i ++) {
			expr2AType.put(FactoryHelper.exprClassNames[i], FactoryHelper.typeClassNames[i]);
		}
	}
	
	/*
	private static class SingletonHandler{
		private static final ATypeFactory INSTANCE = new ATypeFactory(); 
	}
	
	private static ATypeFactory getInstance() {
		return SingletonHandler.INSTANCE;
	}
	*/
	public AbstractAType getATypeInstance(String exprClassName) {
		try {
			return (AbstractAType)Class.forName(expr2AType.get(exprClassName)).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
