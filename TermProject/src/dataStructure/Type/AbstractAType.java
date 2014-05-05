/**
 * 
 */
package dataStructure.Type;

/**
 * @author WuJun A0106507M
 *
 */
public abstract class AbstractAType {
	
	protected AbstractAType siblingType;
	public abstract String getTypeInfo();
	public AbstractAType getSibling() {
		return this.siblingType;
	}
	
	public void setSibling(AbstractAType t) {
		if(t != null) {
			this.siblingType = t;
		}else {
			System.err.println("Parameter should not be null");
		}
	}
	public abstract void setTypeInfo(String value);
}
