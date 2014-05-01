package dataStructure.Type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
	//all the type variables has the relationship of substitution 
	//are in the same set. The type variable in the same group 
	//has the same type
	Set<AbstractAType> sameTypeGroups;
	
	public Substitution() {
		// TODO Auto-generated constructor stub
		left = null;
		right = null;
		sameTypeGroups = new HashSet<AbstractAType>();
	}
	
	public Substitution(AbstractAType left, AbstractAType right) {
		this.left = left;
		this.right = right;
		sameTypeGroups = new HashSet<AbstractAType>();
		if(left != null)
			sameTypeGroups.add(left);
		if(right != null)
			sameTypeGroups.add(right);
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
	
	public boolean equal(Substitution obj) {
		if((this.getLeft() == obj.getLeft() && this.getRight() == obj.getRight())
			|| this.getLeft() == obj.getRight() && this.getRight() == obj.getLeft()) {
			return true;
		} else {
			return false;
		}
	}
	
	public Set<AbstractAType> getSameTypeSet() {
		return this.sameTypeGroups;
	}
}
