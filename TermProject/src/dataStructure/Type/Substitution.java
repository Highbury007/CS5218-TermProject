package dataStructure.Type;

/**
 * @author WuJun A0106507M
 *
 */
public class Substitution {

	/**
	 * 
	 */
	private AbstractAType left;
	private AbstractAType right;
	
	public Substitution() {
		// TODO Auto-generated constructor stub
		left = null;
		right = null;
	}
	/**
	 * @param left
	 * @param right
	 */
	public Substitution(AbstractAType left, AbstractAType right) {
		// TODO Auto-generated constructor stub
		this.left = left;
		this.right = right;
	}

	public AbstractAType getLeft() {
		return left;
	}
	public void setLeft(AbstractAType left) {
		this.left = left;
	}
	public AbstractAType getRight() {
		return right;
	}
	public void setRight(AbstractAType right) {
		this.right = right;
	}
}
