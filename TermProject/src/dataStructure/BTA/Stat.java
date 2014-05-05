package dataStructure.BTA;

/**
 * @author WuJun A0106507M
 *
 */
public class Stat extends AbstractValue {

	/**
	 * 
	 */
	private Stat() {
		// TODO Auto-generated constructor stub
	}
	
	private static class SingletonHolder {
		private static final AbstractValue INSTANCE = new Stat(); 
	}
	
	public static AbstractValue getInstance() {
		return SingletonHolder.INSTANCE;
	}

	@Override
	public String toStr() {
		// TODO Auto-generated method stub
		return "Static";
	}
	
}
