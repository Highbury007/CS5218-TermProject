package dataStructure.Type;

import java.util.Vector;

import dataStructure.ATypeFactory;
import dataStructure.FactoryHelper;
import dataStructure.Program.AbstractExpr;

/**
 * @author WuJun A0106507M
 *
 */
public class ListAType extends AbstractAType {

	/**
	 * 
	 */
	private static final int tNumC = 2;
	private Vector<AbstractAType> typeInfo; 
	//private static final String token = "List<>";
	public ListAType() {
		// TODO Auto-generated constructor stub
		typeInfo = new Vector<AbstractAType>();
		System.out.println(this.toString());
	}

	@Override
	public void populateType(AbstractExpr expr, ATypeFactory builder) {
		// TODO Auto-generated method stub
		if(expr != null) {
			//AbstractAType subAType = builder.getATypeInstance(expr.toString().split(FactoryHelper.splitStr)[0]);
			//subAType.populateType(expr., builder);
			for(AbstractExpr e : expr.getAbstractExprsRef()) {
				AbstractAType subAType = builder.getATypeInstance(e.toString().split(FactoryHelper.splitStr)[0]);
				if(subAType != null) {
					typeInfo.add(subAType);
					typeInfo.lastElement().populateType(e, builder);
				}
			}
		}
	}
	
	@Override
	public Vector<AbstractAType> getTypeRefs() {
		// TODO Auto-generated method stub
		return this.typeInfo;
	}
	
	@Override
	public String getTypeInfo(){
		// TODO Auto-generated method stub
		try {
			if(typeInfo.size() == tNumC) {
				return "List<" + typeInfo.firstElement().getTypeInfo() + ">";
			}else {
				System.err.println(this.toString() + " :TypeInfo num should be " + tNumC);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
