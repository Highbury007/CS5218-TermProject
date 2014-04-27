package dataStructure.Type;

import java.util.Vector;

import dataStructure.ATypeFactory;
import dataStructure.Program.AbstractExpr;

/**
 * @author WuJun A0106507M
 *
 */
public class NullListAType extends AbstractAType {

	/**
	 * 
	 */
	private static final String nullType = "nil";
	private static final int tNumC = 1;
	private Vector<AbstractAType> typeInfo;

	public NullListAType() {
		// TODO Auto-generated constructor stub
		typeInfo = new Vector<AbstractAType>();
		System.out.println(this.toString());
	}

	@Override
	public void populateType(AbstractExpr expr, ATypeFactory builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector<AbstractAType> getTypeRefs() {
		// TODO Auto-generated method stub
		return this.typeInfo;
	}
	
	@Override
	public String getTypeInfo() {
		// TODO Auto-generated method stub
		//return (typeInfo.size() == 0 ? nullType : typeInfo.firstElement().getTypeInfo());
		if(typeInfo.size() == 0) {
			return nullType;
		}else if(typeInfo.size() == tNumC) {
			return typeInfo.firstElement().getTypeInfo();
		}else {
			System.err.println(this.toString() + " :SubType num should <= " + tNumC);
			return null;
		}
	}

}
