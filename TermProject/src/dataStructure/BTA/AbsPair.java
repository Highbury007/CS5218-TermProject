package dataStructure.BTA;

/**
 * @author WuJun A0106507M
 *
 */
public class AbsPair{

	/**
	 * 
	 */
	AbstractValue head;
	AbstractValue tail;
	public AbsPair() {
		// TODO Auto-generated constructor stub
		head = Bottom.getInstance();
		tail = Bottom.getInstance();
	}
	public AbstractValue getHead() {
		return head;
	}
	public void setHead(AbstractValue head) {
		this.head = head;
	}
	public AbstractValue getTail() {
		return tail;
	}
	public void setTail(AbstractValue tail) {
		this.tail = tail;
	}
}
