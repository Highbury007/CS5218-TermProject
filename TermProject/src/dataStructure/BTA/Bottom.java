package dataStructure.BTA;

/**
 * @author WuJun A0106507M
 *
 */
public class Bottom extends AbstractValue {

	/**
	 * 
	 */
	private Bottom() {
		// TODO Auto-generated constructor stub
	}

	private static class SingletonHolder {
		private static final AbstractValue INSTANCE = new Bottom();
	}
	
	public static AbstractValue getInstance() {
		//System.out.println("Bottom");
		return SingletonHolder.INSTANCE;
	}

	@Override
	public String toStr() {
		// TODO Auto-generated method stub
		return "Bottom";
	}
	
	
}
