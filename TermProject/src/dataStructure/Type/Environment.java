package dataStructure.Type;

import java.util.List;
import java.util.Vector;

import dataStructure.Program.AbstractExpr;

/**
 * @author WuJun A0106507M
 *
 */
public class Environment {

	/**
	 * 
	 */
	private List<AbstractExpr> keys;
	private List<AbstractAType> values;
	public Environment() {
		// TODO Auto-generated constructor stub
		keys = new Vector<AbstractExpr>();
		values = new Vector<AbstractAType>();
	}
	
	public void put(AbstractExpr key, AbstractAType value) {
		keys.add(key);
		values.add(value);
	}
	
	public AbstractAType get(AbstractExpr key) {
		return getValue(key);
	}
	
	public AbstractAType getValue(AbstractExpr key) {
		for(int i = 0; i < keys.size(); i ++) {
			if(keys.get(i) == key) {
				return values.get(i);
			}
		}
		return null;
	}
	
	public void setValue(AbstractExpr key, AbstractAType value) {
		for(int i = 0; i < keys.size(); i ++) {
			if(keys.get(i) == key) {
				values.set(i, value);
				break;
			}
		}
	}
	
	public List<AbstractExpr> getKeyList() {
		return this.keys;
	}
	
	public List<AbstractAType> getValueList() {
		return this.values;
	}
	
	public boolean containsKey(AbstractExpr key) {
		return this.keys.contains(key);
	}
	
	public boolean containsValue(AbstractAType value) {
		return this.values.contains(value);
	}
}
