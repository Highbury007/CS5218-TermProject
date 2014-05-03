package dataStructure.Type;

import java.util.HashSet;
import java.util.Iterator;
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
	//private AbstractAType groupTypeInfo;
	//private AbstractAType groupTypeInfo;
	//private Set<AbstractAType> sameTypeGroup;
	
	public Substitution() {
		// TODO Auto-generated constructor stub
		left = null;
		right = null;
		//groupTypeInfo = null;
		//sameTypeGroup = new HashSet<AbstractAType>();
		//for(Iterator<AbstractAType> it = sameTypeGroup.iterator(); it.hasNext(); ){
		//	
		//}
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
	/*
	public Substitution(AbstractAType left, AbstractAType right) {
		this.left = left;
		this.right = right;
		sameTypeGroup = new HashSet<AbstractAType>();
		if(left != null)
			sameTypeGroup.add(left);
		if(right != null)
			sameTypeGroup.add(right);
	}
	*/
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
	
	//public boolean equal(Substitution obj) {
	//	if((this.getLeft() == obj.getLeft() && this.getRight() == obj.getRight())
	//		|| this.getLeft() == obj.getRight() && this.getRight() == obj.getLeft()) {
	//		return true;
	//	} else {
	//		return false;
	//	}
	//}
	/*
	public Set<AbstractAType> getSameTypeSet() {
		return this.sameTypeGroup;
	}
	
	public void addElement(AbstractAType at) {
		this.sameTypeGroup.add(at);
	}
	
	public void setGroupInfo(AbstractAType info) {
		this.groupTypeInfo = info;
	}
	
	public AbstractAType getGroupInfo() {
		return this.groupTypeInfo;
	}
	
	public void updateGroupInfo(AbstractAType info) {
		setGroupInfo(info);
	}
	*/
}
