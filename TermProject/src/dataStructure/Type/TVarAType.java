package dataStructure.Type;

/**
 * @author WuJun A0106507M
 *
 */
public class TVarAType extends AbstractAType {

	private String typeInfo;
	public TVarAType() {
		// TODO Auto-generated constructor stub
		siblingType = null;
		typeInfo = new String();
		typeInfo = "";
		System.out.println(this.toString());
	}
	@Override
	public String getTypeInfo() {
		// TODO Auto-generated method stub
		if(null == siblingType && typeInfo != null) {
			return typeInfo;
		}else if(null == siblingType && typeInfo == null){
			return "";
		}
		else {
			return siblingType.getTypeInfo();
		}
	}
	@Override
	public void setTypeInfo(String value) {
		// TODO Auto-generated method stub
		typeInfo = value;
	}
}
