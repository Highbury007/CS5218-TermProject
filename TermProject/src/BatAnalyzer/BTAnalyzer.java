package batAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Vector;

import dataStructure.BTA.*;
import dataStructure.Program.*;
import dataStructure.BTA.Stat;


/**
 * @author WuJun A0106507M
 *
 */
public class BTAnalyzer {

	/**
	 * 
	 */
	private AbstractEnv absEnv;
	private Map<AbstractExpr, AbstractValue> consExpr2ConsPoint;

	private Map<String, AbstractExpr> funName2FunExpr;
	private boolean isAbsEnvUpdated;
	private List<AbstractValue> absVFromInput;
	private AbstractExpr mainFun;
	
	
	private static final String bot = "b";
	private static final String stat = "s";
	//private static final String consP = "p";
	private static final String dyn = "d";
	
	private static final int PRINTALL = 1;
	private static final int PRINTFUNSIGN = 2;
	private static final int PRINTCPAP = 3;
	//private static final int PRINTNONE = 4;
	
	private Pgm programExpr;
	
	public BTAnalyzer() {
		
		isAbsEnvUpdated = true;
		absEnv = new AbstractEnv();
		mainFun = null;
		consExpr2ConsPoint = new HashMap<AbstractExpr, AbstractValue>();
		funName2FunExpr = new HashMap<String, AbstractExpr>();
		absVFromInput = new Vector<AbstractValue>();
	}
	
	public void analyzeBtv(Pgm program, List<String> input) {
		
		init(program, input);
		programExpr = program;
		
		while(isAbsEnvUpdated) {
			isAbsEnvUpdated = false;
			BindingTimeEnv bTAEnvNow = constructBTAEnv(program, absEnv);
			for(AbstractExpr ae : program.getFunDefs()) {
				bTAMethodDecider(ae, bTAEnvNow);
			}
		}
		
		System.out.println("-------------------------------Binding Time Analysis result----------------------------");
		printAbsEnv(PRINTALL);

	}
	
	private void init(Pgm program, List<String> input) {
		if(null == program) {
			System.err.println("Program is empty!");
			System.exit(1);
		}
		
		mainFun = program.getFunDefs().get(0);
		if(null == mainFun) {
			System.err.println("No function inside!");
		}
		
		if(program.getFunDefs() != null) {
			for(AbstractExpr funDef : program.getFunDefs()) {
				String funName = ((FunDefExpr) funDef).getFunName();
				funName2FunExpr.put(funName, funDef);
				//initial the input
				List<AbstractExpr> varList = null;
				Signature funSign = new Signature();
				
				if( funDef == mainFun) {
					varList = ((FunDefExpr) funDef).getFunVarList();
					for(int i = 0; i < varList.size(); i ++) {
						if(i < input.size()) {
							if(input.get(i).contains(bot)) {
								funSign.addFunSignElement(Bottom.getInstance());
								absVFromInput.add(Bottom.getInstance());
							}else if(input.get(i).contains(stat)) {
								funSign.addFunSignElement(Stat.getInstance());
								absVFromInput.add(Stat.getInstance());
							}else if(input.get(i).contains(dyn)) {
								funSign.addFunSignElement(Dyn.getInstance());
								absVFromInput.add(Dyn.getInstance());
							}else {
								System.err.println("Please input b(bottom), s(static) or d(dynamic)");
								System.exit(1);
							}
						}else {
							funSign.addFunSignElement(Bottom.getInstance());
							absVFromInput.add(Bottom.getInstance());
						}
					}
					//the function result of the entry function
					funSign.addFunSignElement(Bottom.getInstance());
				}else {
					varList = ((FunDefExpr) funDef).getFunVarList();
					for(int i = 0; i < varList.size(); i ++) {
						funSign.addFunSignElement(Bottom.getInstance());
					}
					//add the result of this function
					funSign.addFunSignElement(Bottom.getInstance());
				}
				absEnv.putFunVar2Sign(funDef, funSign);
			}
		}else {
			printNullPointerError();
		}

	}
	
	private BindingTimeEnv constructBTAEnv(Pgm program, AbstractEnv env) {
		BindingTimeEnv bTAEnvNow = new BindingTimeEnv();
		for(AbstractExpr funDef : program.getFunDefs()) {
			List<AbstractExpr> varList = ((FunDefExpr) funDef).getFunVarList();
			List<AbstractValue> paraValues = env.getSignature(funDef).getFunSign();
			for(int i = 0; i < varList.size(); i ++) {
				bTAEnvNow.putAbsValue(varList.get(i), paraValues.get(i));
			}
		}
		return bTAEnvNow;
	}
	
	private void printAbsEnv(int printMode) {
		if(printMode == PRINTALL || printMode == PRINTFUNSIGN) { 
			for(AbstractExpr funDef : programExpr.getFunDefs()) {
				Signature funSign = absEnv.getSignature(funDef);
				System.out.print(((FunDefExpr) funDef).getFunName() + "( ");
				for(AbstractValue para : funSign.getParaBtAv()) {
					System.out.print(para.toStr() + " ");
				}
				System.out.println(") -> " + funSign.getFunBodyAbsV().toStr());
				if(funSign.getFunBodyAbsV() instanceof ConsPoint) {
					System.out.println(funSign.getFunBodyAbsV().toString());
				}
				System.out.println("");
			}
		}
		if(printMode == PRINTALL || printMode == PRINTCPAP) {
			Map<AbstractValue, AbsPair> cp2Ap = absEnv.getCp2Ap();
			if(cp2Ap != null) {
				for(AbstractValue key : cp2Ap.keySet()) {
					System.out.println(key.toString() + " <" + cp2Ap.get(key).getHead().toStr()
							+ ", " + cp2Ap.get(key).getTail().toStr() + "> ");
					if(cp2Ap.get(key).getHead() instanceof ConsPoint) {
						System.out.println(cp2Ap.get(key).getHead().toString());
					}
					if(cp2Ap.get(key).getTail() instanceof ConsPoint) {
						System.out.println(cp2Ap.get(key).getTail().toString());
					}
					System.out.println("");
				}
			}
		}
	}
	
	//used to compare whether the environment should be updated or not
	private int absV2Int(AbstractValue absV) {
		if(absV instanceof Bottom) {
			return 0;
		}else if(absV instanceof Stat) {
			return 1;
		}else if(absV instanceof ConsPoint) {
			return 2;
		}else if(absV instanceof Dyn) {
			return 3;
		}else {
			System.err.println("Wrong AbstractValue Type!");
			System.exit(1);
		}
		return 0;
	}
	
	private AbstractValue bTAMethodDecider(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {
		if(sourceExpr instanceof FunDefExpr) {
			return bTAfunDefExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof NumConstantExpr 
		|| sourceExpr instanceof BoolConstantExpr
		|| sourceExpr instanceof NullListExpr) {
			return bTAConstantExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof VariableExpr) {
			return bTAVariableExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof IfExpr) {
			return bTAIfExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof FunCallExpr) {
			return bTAFunCallExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof BinaryOpExpr) {
			return bTABinaryOpExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof ConsExpr) {
			return bTAConsExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof CarExpr) {
			return bTACarExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof CdrExpr) {
			return bTACdrExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof NullListTestExpr) {
			return NullListTestExpr(sourceExpr, bTAEnv);
		}else if(sourceExpr instanceof BracketedExpr) {
			return bTAMethodDecider(sourceExpr.getAbstractExpressions().get(0), bTAEnv);
		}else {
			System.err.println("No this kind of expression: " + sourceExpr.toString());
			System.exit(1);
		}
		return null;	
	} 
	
	private void printNullPointerError() {
		System.err.println("null pointer");
		System.exit(1);
	}
	
	//get the least lower upper bound of two abstract value
	//don't change the isAbsEnvUpdatedValue
	//only changed the value when absV2Int(before) < absV2Int(now)
	private Entry<AbstractValue, Boolean> absVLup(AbstractValue before, AbstractValue now) {
		if(absV2Int(before) >= absV2Int(now)) {
			Entry<AbstractValue, Boolean> result = new SimpleEntry<AbstractValue, Boolean>(before, false);
			return result;
		}else {
			isAbsEnvUpdated = true;
			Entry<AbstractValue, Boolean> result = new SimpleEntry<AbstractValue, Boolean>(now, true);
			return result;
		}
	}
		
	private boolean foldFunCall(AbstractExpr funDef, Signature funSign) {
		if(null == funDef || null == absEnv.getSignature(funDef)) {
			printNullPointerError();
		}
		
		List<AbstractValue> signBefore = absEnv.getSignature(funDef).getFunSign();
		List<AbstractValue> signNow = funSign.getFunSign();
		if(null == signBefore || signBefore.size() != signNow.size()) {
			//System.out.println("Before: " + signBefore.size() + " After: " + signNow.size());
			System.err.println("Error in update: null pointer or size mismatch!");
			System.exit(1);
		}
		Boolean result = false;
		for(int i = 0; i < signBefore.size(); i ++) {
			Entry<AbstractValue, Boolean> tempResult = absVLup(signBefore.get(i), signNow.get(i));
			if(tempResult.getValue()) {
				result = true;
			}
			signBefore.set(i, tempResult.getKey());
		}
		
		//printAbsEnv(PRINTALL);
		
		return result;
	}
	
	private boolean foldCons(AbstractValue cp, AbstractValue headNow, AbstractValue tailNow) {
		if(null == cp || null == absEnv.getPair(cp) 
		|| null == headNow || null == tailNow) {
			printNullPointerError();
		}
		
		AbsPair ap = absEnv.getPair(cp);
		Entry<AbstractValue, Boolean> headAfter = absVLup(ap.getHead(), headNow);
		Entry<AbstractValue, Boolean> tailAfter = absVLup(ap.getTail(), tailNow);
		ap.setHead(headAfter.getKey());
		ap.setTail(tailAfter.getKey());
		
		//printAbsEnv(PRINTALL);
		
		return (headAfter.getValue() | tailAfter.getValue());
	}
	
	private AbstractValue bTAConstantExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {
		
		if(null == sourceExpr) {
			printNullPointerError();
		}
		
		return Stat.getInstance();
	}

	private AbstractValue bTAVariableExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {
		
		if(null == sourceExpr) {
			printNullPointerError();
		}
		if(null == bTAEnv.getAbsValue(sourceExpr)) {
			System.err.println("Not defined in the environment!");
			return Bottom.getInstance();
		}else { 
			return bTAEnv.getAbsValue(sourceExpr);
		}
	}

	private AbstractValue bTAIfExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {

		AbstractValue conditionAbsV = bTAMethodDecider(((IfExpr) sourceExpr).getCondition(), bTAEnv);
		AbstractValue trueBranchAbsV = bTAMethodDecider(((IfExpr) sourceExpr).getTBranch(), bTAEnv);
		AbstractValue falseBranchAbsV = bTAMethodDecider(((IfExpr) sourceExpr).getFBranch(), bTAEnv);

		if(conditionAbsV instanceof Bottom) {
			return Bottom.getInstance();
		}else if(conditionAbsV instanceof Stat) {
			return (absV2Int(trueBranchAbsV) >= absV2Int(falseBranchAbsV) ? trueBranchAbsV : falseBranchAbsV);			 			
		}else {
			return Dyn.getInstance();
		}
	}

	private AbstractValue bTAfunDefExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {

		AbstractValue result = bTAMethodDecider(((FunDefExpr) sourceExpr).getFunBody(), bTAEnv);
		List<AbstractExpr> varList = ((FunDefExpr) sourceExpr).getFunVarList();
		Signature funSign = new Signature();
		for(AbstractExpr var : varList) {
			if(bTAEnv.getAbsValue(var) != null) {
				funSign.addFunSignElement(bTAEnv.getAbsValue(var));
			}else {
				System.err.println("Variable list match error!");
			}
		}
		funSign.addFunSignElement(result);
		foldFunCall(sourceExpr, funSign);
		return result;
	}
	
	private AbstractValue bTAFunCallExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {

		String funName = ((FunCallExpr) sourceExpr).getFunName();
		//already analyzed
		List<AbstractExpr> paraList = ((FunCallExpr) sourceExpr).getArgList();
		List<AbstractExpr> varList = ((FunDefExpr) funName2FunExpr.get(funName)).getFunVarList();
		//System.out.println("para: " + paraList.size());
		BindingTimeEnv bTAEnvNow = new BindingTimeEnv();
		Signature tempFunSign = new Signature();
		for(int i = 0; i < paraList.size(); i ++) {
			AbstractValue tempResult = bTAMethodDecider(paraList.get(i), bTAEnv);
			//construct the new environment and function signature
			bTAEnvNow.putAbsValue(varList.get(i), tempResult);
			tempFunSign.addFunSignElement(tempResult);
		}
		
		tempFunSign.addFunSignElement(Bottom.getInstance());
		//System.out.println("tempFunSign :" + tempFunSign.getFunSign().size());
		boolean isUpdated = foldFunCall(funName2FunExpr.get(funName), tempFunSign);
		if(isUpdated) {
			return bTAMethodDecider(funName2FunExpr.get(funName), bTAEnvNow);
		}else {
			return absEnv.getSignature(funName2FunExpr.get(funName)).getFunBodyAbsV();
		}
	}

	private AbstractValue bTABinaryOpExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {

		AbstractValue leftOpdAbsV = bTAMethodDecider(((BinaryOpExpr) sourceExpr).getLeftOpd(), bTAEnv);
		AbstractValue rightOpdAbsV = bTAMethodDecider(((BinaryOpExpr) sourceExpr).getRightOpd(), bTAEnv);
		if(absV2Int(leftOpdAbsV) > absV2Int(rightOpdAbsV)) {
			return leftOpdAbsV;
		}else {
			return rightOpdAbsV;
		}
	}

	private AbstractValue bTAConsExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {

		AbstractValue cp = null;

		//if the cons point not generated, generate a new one
		if(null == consExpr2ConsPoint.get(sourceExpr)) {
			//initialization
			cp = new ConsPoint();
			consExpr2ConsPoint.put(sourceExpr, cp);
			absEnv.putConsPoint2AbsPair(cp, new AbsPair());
		}else {
			cp = consExpr2ConsPoint.get(sourceExpr);
		}
		
		AbstractValue headAbsVNow = bTAMethodDecider(((ConsExpr) sourceExpr).getHeadExpr(), bTAEnv);
		AbstractValue tailAbsVNow = bTAMethodDecider(((ConsExpr) sourceExpr).getTailExpr(), bTAEnv);

		foldCons(cp, headAbsVNow, tailAbsVNow);
		
		if(absEnv.getPair(cp).getHead() == absEnv.getPair(cp).getTail()
	       && !(absEnv.getPair(cp).getHead() instanceof ConsPoint)) {
			return absEnv.getPair(cp).getHead();
		}else {
			return cp;
		}
	}
	
	private AbstractValue bTACarExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {

		AbstractValue result = bTAMethodDecider(((CarExpr) sourceExpr).getEpxr(), bTAEnv);
		if(result instanceof ConsPoint) {
			return absEnv.getPair(result).getHead();
		}else {
			return result;
		}
	}

	private AbstractValue bTACdrExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {

		AbstractValue result = bTAMethodDecider(((CdrExpr) sourceExpr).getExpr(), bTAEnv);
		if(result instanceof ConsPoint) {
			return absEnv.getPair(result).getTail();
		}else {
			return result;
		}
	}

	private AbstractValue NullListTestExpr(AbstractExpr sourceExpr, BindingTimeEnv bTAEnv) {

		AbstractValue result = bTAMethodDecider(((NullListTestExpr) sourceExpr).getExpr(), bTAEnv);
		if(result instanceof ConsPoint || result instanceof Stat) {
			return Stat.getInstance();
		}else {
			return result;
		}
	}
}
