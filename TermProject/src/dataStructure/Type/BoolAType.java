package dataStructure.Type;

/**
 * @author WuJun A0106507M
 *
 */
public class BoolAType extends AbstractAType {

	private static final String typeInfo = "BOOL";
	private BoolAType() {
		// TODO Auto-generated constructor stub
		siblingType = null;
		System.out.println(this.toString());
	}
	
	private static class SingletonHolder {
		private final static AbstractAType INSTANCE = new BoolAType();
	}
	
	public static AbstractAType getInstance() {
		System.out.println(SingletonHolder.INSTANCE.toString());
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public String getTypeInfo() {
		// TODO Auto-generated method stub
		if(null == siblingType) {
			return typeInfo;
		}else {
			return typeInfo + "->" + siblingType.getTypeInfo();
		}
	}
	@Override
	public void setTypeInfo(String value) {
		// TODO Auto-generated method stub
		System.err.println(this.toString() + ":BOOL is constant!");
	}
	@Override
	public void setSibling(AbstractAType ae) {
		System.err.println(this.toString() + ":BOOL is constant! No sibling");
	}
}
