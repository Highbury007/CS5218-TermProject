/**
 * 
 */
package dataStructure.Type;

import java.util.Vector;

import dataStructure.ATypeFactory;
import dataStructure.Program.AbstractExpr;

/**
 * @author WuJun A0106507M
 *
 */
public abstract class AbstractAType {
	public AbstractAType() {
		// TODO Auto-generated constructor stub
	}
	public abstract void populateType(AbstractExpr expr, ATypeFactory builder);
	//public abstract void addTypeInfo(AbstractAType tInfo);
	public abstract Vector<AbstractAType> getTypeRefs();
	public abstract String getTypeInfo();
}
