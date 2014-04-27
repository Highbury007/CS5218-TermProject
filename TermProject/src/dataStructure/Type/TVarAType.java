package dataStructure.Type;

import java.util.Vector;

import dataStructure.ATypeFactory;
import dataStructure.Program.AbstractExpr;

/**
 * @author WuJun A0106507M
 *
 */
public class TVarAType extends AbstractAType {

	private AbstractAType typeInfo;
	public TVarAType() {
		// TODO Auto-generated constructor stub
		typeInfo = null;
		System.out.println(this.toString());
	}

	@Override
	public void populateType(AbstractExpr expr, ATypeFactory builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector<AbstractAType> getTypeRefs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeInfo() {
		// TODO Auto-generated method stub
		return (typeInfo == null ? this.toString() : typeInfo.getTypeInfo());
	}

}
