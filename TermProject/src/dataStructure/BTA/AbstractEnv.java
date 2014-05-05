package dataStructure.BTA;

import java.util.HashMap;
import java.util.Map;

import dataStructure.Program.AbstractExpr;

/**
 * @author WuJun A0106507M
 *
 */
public class AbstractEnv {

	/**
	 * 
	 */
	Map<AbstractExpr, Signature> funVar2Sign; 
	Map<AbstractValue, AbsPair> cp2Ap;
	
	public AbstractEnv() {
		// TODO Auto-generated constructor stub
		funVar2Sign = new HashMap<AbstractExpr, Signature>();
		cp2Ap = new HashMap<AbstractValue, AbsPair>();
	}
	
	public AbsPair getPair(AbstractValue cp) {
		if(cp != null) {
			return cp2Ap.get(cp);
		}else {
			System.err.println("cp is null!");
			return null;
		}
	}
	
	public Signature getSignature(AbstractExpr ae) {
		if(ae != null) {
			return funVar2Sign.get(ae);
		}else {
			System.err.println("ae is null!");
			return null;
		}
	}
	
	public void putFunVar2Sign(AbstractExpr ae, Signature sign) {
		funVar2Sign.put(ae, sign);
	}
	
	public void putConsPoint2AbsPair(AbstractValue cp, AbsPair ap) {
		cp2Ap.put(cp, ap);
	}
	
	public Map<AbstractExpr, Signature> getFunVar2Sign() {
		return funVar2Sign;
	}
	/*
	public void setFunVar2Sign(Map<AbstractExpr, Signature> funVar2Sign) {
		this.funVar2Sign = funVar2Sign;
	}
	*/
	public Map<AbstractValue, AbsPair> getCp2Ap() {
		return cp2Ap;
	}
	/*
	public void setCp2Ap(Map<ConsPoint, Pair> cp2Ap) {
		this.cp2Ap = cp2Ap;
	}
	*/
}
