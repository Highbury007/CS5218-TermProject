package dataStructure.BTA;

import java.util.List;
import java.util.Vector;

/**
 * @author WuJun A0106507M
 *
 */
public class Signature {

	/**
	 * 
	 */
	private List<AbstractValue> funSign;
	//private AbstractValue resultBtAV;
	public Signature() {
		// TODO Auto-generated constructor stub
		//paraBtAv = new Vector<AbstractValue>();
		//resultBtAV = Bottom.getInstance();
		funSign = new Vector<AbstractValue>();
	}
	public List<AbstractValue> getParaBtAv() {
		return funSign.subList(0, funSign.size() - 1);
	}
	public List<AbstractValue> getFunSign() {
		return funSign;
	}
	public void setFunSign(List<AbstractValue> funSign) {
		this.funSign = funSign;
	}
	
	public AbstractValue getFunBodyAbsV() {
		if(funSign.size() <= 1) {
			System.err.println("Parameter number is not corret!");
			System.exit(1);
		}
		return funSign.get(funSign.size() -1 );
	}
	
	public void setFunBodyAbsV(AbstractValue absV) {
		funSign.set(funSign.size() - 1, absV);
	}
	
	public void addFunSignElement(AbstractValue absV) {
		funSign.add(absV);
	}
	
	public void setFunSignElement(int index, AbstractValue element) {
		if(index > funSign.size() - 1) {
			System.err.println("Out of bound!");
			System.exit(1);
		}
		
		funSign.set(index, element);
	}

	//public void setParaBtAv(List<AbstractValue> paraBtAv) {
	//	this.paraBtAv = paraBtAv;
	//}
	
	//public void addParaAbsValue(AbstractValue av) {
	//	paraBtAv.add(av);
	//}
	//public AbstractValue getResultBtAV() {
	//	return resultBtAV;
	//}
	//public void setResultBtAV(AbstractValue resultBtAV) {
	//	this.resultBtAV = resultBtAV;
	//}
}
