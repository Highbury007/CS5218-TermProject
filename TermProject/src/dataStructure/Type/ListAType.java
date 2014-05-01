package dataStructure.Type;

/**
 * @author WuJun A0106507M
 *
 */
public class ListAType extends AbstractAType {

	private String typeInfo = "";
	public ListAType() {
		// TODO Auto-generated constructor stub
		siblingType = null;
		System.out.println(this.toString());
	}

	public AbstractAType getListCore() {
		return getSibling();
	}
	
	public void setListCore(AbstractAType value) {
		setSibling(value);
	}
	
	@Override
	public String getTypeInfo() {
		// TODO Auto-generated method stub
		if(null == siblingType) {
			return "List<" + typeInfo + ">";
		}else {
			return "List<" + siblingType.getTypeInfo() + ">";
		}
	}

	@Override
	public void setTypeInfo(String value) {
		// TODO Auto-generated method stub
		if(null != value) {
			typeInfo = value;
		}else {
			System.err.println("Value is null");
		}
	}

}
