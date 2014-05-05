package dataStructure.BTA;

/**
 * @author WuJun A0106507M
 *
 */
public class Dyn extends AbstractValue {

	/**
	 * 
	 */
	private Dyn() {
		// TODO Auto-generated constructor stub
	}

	private static class SingletonHolder {
		private static final AbstractValue INSTANCE = new Dyn();
	}
	
	public static AbstractValue getInstance() {
		return SingletonHolder.INSTANCE;
	}

	@Override
	public String toStr() {
		// TODO Auto-generated method stub
		return "Dynamic";
	}
}
