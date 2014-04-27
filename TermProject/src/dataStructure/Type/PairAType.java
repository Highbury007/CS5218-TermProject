package dataStructure.Type;

import java.util.Vector;

import dataStructure.ATypeFactory;
import dataStructure.Program.AbstractExpr;

/**
 * @author WuJun A0106507M
 *
 */
public class PairAType extends AbstractAType {
	
	
	private static final int tNumC = 2;
	private Vector<AbstractAType> typeInfo;
	public PairAType() {
		// TODO Auto-generated constructor stub
		typeInfo = new Vector<AbstractAType>();
		System.out.println(this.toString());
	}

	@Override
	public void populateType(AbstractExpr expr, ATypeFactory builder) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getTypeInfo() throws NullPointerException{
		// TODO Auto-generated method stub
		try {
			if(typeInfo.size() == tNumC) {
				return typeInfo.firstElement().getTypeInfo() + "->" + typeInfo.lastElement().getTypeInfo();
			}else {
				System.err.println(this.toString() + " :subType num should be " + tNumC);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Vector<AbstractAType> getTypeRefs() {
		// TODO Auto-generated method stub
		return null;
	}
}
