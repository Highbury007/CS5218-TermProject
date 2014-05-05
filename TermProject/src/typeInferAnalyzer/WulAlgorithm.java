package typeInferAnalyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import dataStructure.ATypeFactory;
import dataStructure.Program.*;
import dataStructure.Type.AbstractAType;
import dataStructure.Type.BoolAType;
import dataStructure.Type.Environment;
import dataStructure.Type.IntAType;
import dataStructure.Type.ListAType;
import dataStructure.Type.Substitution;
import dataStructure.Type.TVarAType;

import java.util.AbstractMap.SimpleEntry;
/**
 * @author WuJun A0106507M
 *
 */
public class WulAlgorithm {

	private Environment env;
	
	private Map<String, AbstractExpr> funName2FunExpr;
	//private Map<AbstractExpr, AbstractExpr> funExpr2FunVar;
	private Map<String, AbstractExpr> funName2FunVar;
	private ATypeFactory typeBuilder;
	private int enVersion;
	
	public WulAlgorithm() {
		// TODO Auto-generated constructor stub
		env = new Environment();
		enVersion = 1;
		typeBuilder = new ATypeFactory();
		funName2FunExpr = new HashMap<String, AbstractExpr>();
		funName2FunVar = new HashMap<String, AbstractExpr>();
		//funExpr2FunVar = new HashMap<AbstractExpr, AbstractExpr>();
	}

	public boolean isTypeCheckPass(Pgm program) {
		//construct the mapping from function Name to expression
		if(program.getFunDefs() != null) {
			//System.out.println(program.getFunDefs().size());
			for(AbstractExpr ae : program.getFunDefs()) {
				funName2FunExpr.put(ae.getExprAttribute(), ae);
				//funName2FunVar.put(ae.getExprAttribute(), value);
			}
		}

		int oldEnVersion = 0;
		while(enVersion > oldEnVersion) {
			oldEnVersion = enVersion;
			
			for(AbstractExpr ae : program.getFunDefs()) {				
				wULMethodDecider(ae);
				System.out.println("------------------------------------");
				for(AbstractExpr var : ae.getAbstractExpressions()) {
					System.out.println(env.get(var).toString());
					System.out.println(env.get(var).getTypeInfo());
				}
			}
		}
		return true;
	}
	
	private Entry<AbstractAType, List<Substitution>> wULMethodDecider(AbstractExpr input) {
		if(input instanceof FunDefExpr) {
			return wULFunDefExpr(input);
		}else if(input instanceof VariableExpr) {
			return wULVariableExpr(input);
		}else if(input instanceof BoolConstantExpr 
				|| input instanceof NumConstantExpr) {
			return wULConstantExpr(input);
		}else if(input instanceof NullListExpr) {
			return wULNullListExpr(input);
		}else if(input instanceof IfExpr) {
			return wULIfExpr(input);
		}else if(input instanceof FunCallExpr) {
			return wULFunCallExpr(input);
		}else if(input instanceof BinaryOpExpr) {
			return wULBinaryOpExpr(input);
		}else if(input instanceof ConsExpr) {
			return wULConsExpr(input);
		}else if(input instanceof CarExpr) {
			return wULCarExpr(input);
		}else if(input instanceof CdrExpr) {
			return wULCdrExpr(input);
		}else if(input instanceof NullListTestExpr) {
			return wULNullListTestExpr(input);
		}else if(input instanceof BracketedExpr) {
			return wULMethodDecider(input.getAbstractExpressions().get(0));
		}else {
			System.err.println("Expression type match error!");
			return null;
		}
	}
	
	private AbstractAType getBinaryOptType(String op) {
		if(op.equals(">") || op.equals(">=") ||
		   op.equals("==") || op.equals("<=") ||
		   op.equals("|") || op.equals("&")) {
			return BoolAType.getInstance();
		}else if(op.equals("+") || op.equals("-") ||
				op.equals("*") || op.equals("/") ||
				op.equals("^")){
			return IntAType.getInstance();
		}else {
			System.err.println("No such operator: " + op);
			System.exit(1);
		}
		return null;
	}
	
	private AbstractAType getBinaryOpdType(String op) {
		if(op.equals("|") || op.equals("&")) {
			return BoolAType.getInstance();
		}else if(op.equals("==") || op.equals("<=") ||
				op.equals(">") || op.equals(">=") ||
				op.equals("+") || op.equals("-") ||
				op.equals("*") || op.equals("/") ||
				op.equals("^")){
			return IntAType.getInstance();
		}else {
			System.err.println("No such operator: " + op);
			System.exit(1);
		}
		return null;		
	}
		
	//return null to denote the identity
	//using entry to store the return type and set of substitution
	//key : type, value: set of substitution
	
	//when it does the unification, 
	//bring the two type variables' corresponding substitution
	//in the unifyType function, find the two groups, then find the
	//most accurate information of the two groups respectively.
	//compare them, if type conflict, return error;
	
	//to handle with the variable inList error,
	//may some situation may need the information in environment
	//only use the refined environment information statically,
	//the information is not sufficient. So need to fetch the 
	//environment related substitution to reveal the relationships
	//of variables related with the environment variable
	//For instance:
	// define f(a, b, c) = call f(cons(b,nil), c, a)
	//a is in the cons(b, nil), and a <-> c <-> b
	//so the program should give a type error for this
	private List<Substitution> collectEnvRefinements(List<Substitution> refinements) {
		if(refinements != null && refinements.size() != 0) {
			Set<AbstractAType> envTVarSet = new HashSet<AbstractAType>(env.getValueList());
			List<Substitution> result = new Vector<Substitution>();
			boolean updated = true;
			while(updated) {
				updated = false;
				for(Substitution st : refinements) {
					//if(envTVarSet.contains(st.getLeft()) || envTVarSet.contains(st.getRight())) {
					//	result.add(st);
					updated |= envTVarSet.add(st.getLeft());
					updated |= envTVarSet.add(st.getRight());
					if(updated) {
						result.add(st);
					}
				}
			}
			return result;
		}
		return null;
	}
	
	private void updateEnvironment(List<Substitution> tVar2ATypeList) {
		if(null != tVar2ATypeList && tVar2ATypeList.size() != 0){
			for(Substitution st : tVar2ATypeList) {
				AbstractAType inEnvTypeVar = st.getLeft();
				AbstractAType value = null;
				if(env.containsValue(st.getLeft())) {
					value = st.getRight();
				}else if (env.containsValue(st.getRight())) {
					inEnvTypeVar = st.getRight();
					value = st.getLeft();
				}else {
					continue;
				}
							
				for(AbstractExpr ae : env.getKeyList()) {
					if(env.get(ae).equals(inEnvTypeVar)) {
						//only refine the environment at least 
						//when it can get more or equal accurate information
						if(env.get(ae).getTypeInfo() == "" && value.getTypeInfo() != "")
							//env.setValue(ae, value);
							env.get(ae).setTypeInfo(value.getTypeInfo());
					}
				}	
			}	
		}
	}
	
	private void populateSubstitution(List<Substitution> target, List<Substitution> source) {
		if(null != source && null != target){
			if(source.size() != 0) {
				target.addAll(source);		
			}
		}
		System.out.println(target.size());
		
		System.out.println("************************");
		for(Substitution st : target) {
			System.out.println(st.getLeft().toString() + " ------- " + st.getRight().toString());
			System.out.println(st.getLeft().getTypeInfo() + " ------- " + st.getRight().getTypeInfo());
		}
		System.out.println("************************");
	}
	
	
	private Set<AbstractAType> refineType(AbstractAType key, List<Substitution> substitutionList) {
		if(substitutionList != null && substitutionList.size() != 0) {
			String typeInfo = "";
			//make it more accurate
			Set<AbstractAType> collection = new HashSet<AbstractAType>();
			collection.add(key);
			boolean updated = true;
			while(updated) {
				updated = false;
				for(Substitution st : substitutionList) {
					if(collection.contains(st.getLeft()) && !collection.contains(st.getRight())) {
						if(st.getRight().getTypeInfo().length() > typeInfo.length()) {
							typeInfo = st.getRight().getTypeInfo();
						}
						collection.add(st.getRight());
						updated = true;
					}else if(collection.contains(st.getRight()) && !collection.contains(st.getLeft())) {
						if(st.getLeft().getTypeInfo().length() > typeInfo.length()) {
							typeInfo = st.getLeft().getTypeInfo();
						}
						collection.add(st.getLeft());
						updated = true;
					}
				}
			}
			//remove the key
			collection.remove(key);
			//make the information more concrete
			for(AbstractAType at : collection) {
				if(at instanceof TVarAType) {
					at.setTypeInfo(typeInfo);
				}
			}
			
			if(key instanceof TVarAType)
				key.setTypeInfo(typeInfo);
			if(collection.size() == 0)
				return null;
			else 
				return collection;
		}
		return null;
	}

	private void methodTraceInfo() {
		System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
	}
	
	//*******************************************************Wul Algorithm***************************************************//
	//apply the named function rule
	private Entry<AbstractAType, List<Substitution>> wULFunDefExpr(AbstractExpr input) {
		methodTraceInfo();
		//if the function has been called before, it must has been analyzed
		//used for outer loop
		String funName = ((FunDefExpr) input).getFunName();
		if(funName2FunVar.get(funName) != null) {
			return wULMethodDecider(funName2FunVar.get(funName));
		}
		

		AbstractAType bodyTypeVar = null;
		AbstractAType value = null;
		//generate fresh typeVariables		
		for(AbstractExpr ae : input.getAbstractExpressions()) {
			 value = typeBuilder.getATypeInstance(ae);
			
			if(null == value) {
				System.err.println(this.toString() + " :null pointer error!");
				System.exit(enVersion);
			}
			//construct the environment
			env.put(ae, value);
		}
		//the function body type variable
		bodyTypeVar = value;
		
		//generate a function variable and put it into environment;
		AbstractExpr funTypeVar = new VariableExpr(funName);
		funName2FunVar.put(funName, funTypeVar);
		//only map the function Variable to the body type
		//the variable list type info can be got from the function name
		//using function name -> function expression -> function variable list
		
		env.put(funTypeVar, bodyTypeVar);
		
		AbstractExpr funBodyExpr = ((FunDefExpr) input).getFunBody();
		
		//apply the rule to function body
		Entry<AbstractAType, List<Substitution>> funBodyTs = wULMethodDecider(funBodyExpr);
		
		List<Substitution> funBodyUnification = unifyType(funBodyTs.getKey(), null, env.get(funBodyExpr), funBodyTs.getValue());
		
		//construct result;
		List<Substitution> substitution = new Vector<Substitution>();
	
		populateSubstitution(substitution, funBodyTs.getValue());
		populateSubstitution(substitution, funBodyUnification);

		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(funBodyTs.getKey(), substitution);
		
		//print out the information
		for(AbstractExpr ae : input.getAbstractExpressions()) {
			System.out.print(env.get(ae).getTypeInfo() + " ");
		}
		System.out.println("");
		return result;
	}
		
	private Entry<AbstractAType, List<Substitution>> wULConstantExpr(AbstractExpr input) {
		methodTraceInfo();
		AbstractAType resultType = typeBuilder.getATypeInstance(input);
		
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(resultType, null);
		
		return result;
	} 
	
	private Entry<AbstractAType, List<Substitution>> wULNullListExpr(AbstractExpr input) {
		methodTraceInfo();
		//denote Null list as a List<?> first
		AbstractAType listT = typeBuilder.getATypeInstance(input);

		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(listT, null);
		
		return result;
	} 
	
	private Entry<AbstractAType, List<Substitution>> wULVariableExpr(AbstractExpr input) {
		methodTraceInfo();
		//all variable should be defined first, 
		//otherwise undefined behavior
		//return error
		if(null == env.get(input)) {
			System.err.println(input.toString() + ": No type Info!");
			System.exit(enVersion);
			return null;
		}else {
			Entry<AbstractAType, List<Substitution>> result 
				= new SimpleEntry<AbstractAType, List<Substitution>>(env.get(input), null);
			//System.out.println(env.get(input).toString());
			return result;
		}
	} 
	
	private Entry<AbstractAType, List<Substitution>> wULIfExpr(AbstractExpr input) {
		methodTraceInfo();

		//apply rule to condition
		Entry<AbstractAType, List<Substitution>> conditionTs 
			= wULMethodDecider(((IfExpr) input).getCondition());
		
		updateEnvironment(conditionTs.getValue());
			
		//apply rule to true branch
		Entry<AbstractAType, List<Substitution>> trueBranchTs 
			= wULMethodDecider(((IfExpr) input).getTBranch());
		
		updateEnvironment(conditionTs.getValue());
		updateEnvironment(trueBranchTs.getValue());

		//apply rule to false branch
		Entry<AbstractAType, List<Substitution>> falseBranchTs 
			= wULMethodDecider(((IfExpr) input).getFBranch());
				
		//refine the condition
		List<Substitution> conditionRefinement = new Vector<Substitution>();
		populateSubstitution(conditionRefinement, conditionTs.getValue());
		populateSubstitution(conditionRefinement, trueBranchTs.getValue());
		populateSubstitution(conditionRefinement, falseBranchTs.getValue());
		//unify condition
		List<Substitution> conditionUnification = unifyType(conditionTs.getKey(), conditionRefinement, BoolAType.getInstance(), null);
		
		//refine the true branch		
		List<Substitution> trueBranchRefinement = new Vector<Substitution>();
		
		populateSubstitution(trueBranchRefinement, trueBranchTs.getValue());
		populateSubstitution(trueBranchRefinement, falseBranchTs.getValue());
		populateSubstitution(trueBranchRefinement, conditionUnification);
		List<Substitution> envRefinements = collectEnvRefinements(conditionTs.getValue());
		populateSubstitution(trueBranchRefinement, envRefinements);

		//refine the false branch
		List<Substitution> falseBranchRefinement = new Vector<Substitution>();
		populateSubstitution(falseBranchRefinement, falseBranchTs.getValue());
		populateSubstitution(falseBranchRefinement, conditionUnification);
		populateSubstitution(falseBranchRefinement, envRefinements);
		populateSubstitution(falseBranchRefinement, collectEnvRefinements(trueBranchTs.getValue()));
		
		//unify the true and false branches
		List<Substitution> trueFalseBranchUnification 
			= unifyType(trueBranchTs.getKey(), trueBranchRefinement, falseBranchTs.getKey(), falseBranchRefinement);

		List<Substitution> resultTsInfo = new Vector<Substitution>();
		populateSubstitution(resultTsInfo, trueBranchTs.getValue());
		populateSubstitution(resultTsInfo, falseBranchTs.getValue());
		populateSubstitution(resultTsInfo, conditionUnification);
		populateSubstitution(resultTsInfo, trueFalseBranchUnification);

		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(falseBranchTs.getKey(), resultTsInfo);
		
		return result;
	} 
	
	private Entry<AbstractAType, List<Substitution>> wULFunCallExpr(AbstractExpr input) {	
		methodTraceInfo();
		//get the function type
		Entry<AbstractAType, List<Substitution>> funVarTs = null;		
		//attention
		String funName = ((FunCallExpr) input).getFunName();
		if(!funName2FunVar.containsKey(funName)) {
			funVarTs = wULMethodDecider(funName2FunExpr.get(funName));
		}else {
			funVarTs = wULMethodDecider(funName2FunVar.get(funName));
		}
		//apply rule to arg list
		List<AbstractAType> argListTypeInfo = new Vector<AbstractAType>();
		List<AbstractExpr> argExprList = input.getAbstractExpressions();
		List<Substitution> resultTsInfo = new Vector<Substitution>();
		//System.out.println(argExprList.size());
		List<Substitution> envRefinements = collectEnvRefinements(funVarTs.getValue());
		
		for(int i = 0; i < argExprList.size(); i ++) {
			updateEnvironment(resultTsInfo);
			Entry<AbstractAType, List<Substitution>> argTs 
				= wULMethodDecider(argExprList.get(i));
			argListTypeInfo.add(argTs.getKey());
			populateSubstitution(resultTsInfo, argTs.getValue());
		}
		
		//unify the varList and argList
		AbstractExpr funExpr = funName2FunExpr.get(funName);
		List<AbstractExpr> varExprList = ((FunDefExpr) funExpr).getFunVarList();
		if(varExprList.size() != argExprList.size()) {
			System.err.println("VarList and argList does not match!");
			System.exit(enVersion);
		}
		
		List<Substitution> varArgUnification = new Vector<Substitution>();
		//put in the necessary environment refinements
		populateSubstitution(varArgUnification, envRefinements);
		populateSubstitution(varArgUnification, resultTsInfo);
		
		populateSubstitution(resultTsInfo, funVarTs.getValue());
		List<Substitution> varArgRefinement = resultTsInfo;
		
		for(int i = 0; i < varExprList.size(); i ++) {
			List<Substitution> tVar2AType = 
				unifyType(env.get(varExprList.get(i)), varArgRefinement, argListTypeInfo.get(i), varArgUnification);
			populateSubstitution(varArgUnification, tVar2AType);
			populateSubstitution(varArgRefinement, tVar2AType);
		}
		//unify the function body
		AbstractAType funBodyTypeVar = new TVarAType();
		
		List<Substitution> funBodyUnification = unifyType(funVarTs.getKey(), varArgRefinement, funBodyTypeVar, null);
		
		populateSubstitution(resultTsInfo, funBodyUnification);
		
		Entry<AbstractAType, List<Substitution>> result
			= new SimpleEntry<AbstractAType, List<Substitution>>(funBodyTypeVar, resultTsInfo);
		
		return result;
	} 
	
	private Entry<AbstractAType, List<Substitution>> wULBinaryOpExpr(AbstractExpr input) {
		methodTraceInfo();
		//apply rule to the left operand
		Entry<AbstractAType, List<Substitution>> leftOpdTs 
			= wULMethodDecider(((BinaryOpExpr) input).getLeftOpd());
		updateEnvironment(leftOpdTs.getValue());
		
		//apply rule to the right operand
		Entry<AbstractAType, List<Substitution>> rightOpdTs
			= wULMethodDecider(((BinaryOpExpr) input).getRightOpd());
		
		//unify the left operand
		List<Substitution> leftOpdRefinements = new Vector<Substitution>();
		populateSubstitution(leftOpdRefinements, leftOpdTs.getValue());
		populateSubstitution(leftOpdRefinements, rightOpdTs.getValue());
		AbstractAType opdAType = getBinaryOpdType(((BinaryOpExpr) input).getOpt());
		List<Substitution> leftOpdUnification = unifyType(leftOpdTs.getKey(), leftOpdRefinements, opdAType, null);

		//unify the right operand
		List<Substitution> rightOpdRefinements = new Vector<Substitution>();
		populateSubstitution(rightOpdRefinements, rightOpdTs.getValue());
		populateSubstitution(rightOpdRefinements, leftOpdUnification);
		
		List<Substitution> rightOpdUnification = unifyType(rightOpdTs.getKey(), rightOpdRefinements, opdAType, null);
		//get the opt corresponding result
		AbstractAType optAType = getBinaryOptType(((BinaryOpExpr) input).getOpt());
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, leftOpdTs.getValue());
		populateSubstitution(resultTSInfo, rightOpdTs.getValue());
		populateSubstitution(resultTSInfo, leftOpdUnification);
		populateSubstitution(resultTSInfo, rightOpdUnification);
		
		//result type must the operator corresponding type
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(optAType, resultTSInfo);
		return result;

	} 

	private Entry<AbstractAType, List<Substitution>> wULConsExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> consHeadTs 
			= wULMethodDecider(((ConsExpr) input).getHeadExpr());
		
		updateEnvironment(consHeadTs.getValue());
		
		Entry<AbstractAType, List<Substitution>> consTailTs
			= wULMethodDecider(((ConsExpr) input).getTailExpr());
		
		//construct a list of varT
		AbstractAType listT = typeBuilder.getATypeInstance(input);
		//cons tail must be a list
		List<Substitution> consTailRefinements = new Vector<Substitution>();
		populateSubstitution(consTailRefinements, consTailTs.getValue());
		populateSubstitution(consTailRefinements, collectEnvRefinements(consHeadTs.getValue()));
		List<Substitution> listTypeUnification = unifyType(consTailTs.getKey(), consTailTs.getValue(), listT, null);
		populateSubstitution(listTypeUnification, consTailTs.getValue());
		
		//the cons's tail list core's type must be the same as the cons's head's type
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, consHeadTs.getValue());
		populateSubstitution(resultTSInfo, consTailTs.getValue());
		populateSubstitution(resultTSInfo, listTypeUnification);
		
		List<Substitution> listCoreUnification 
			= unifyType(consHeadTs.getKey(), resultTSInfo, ((ListAType) listT).getListCore(), listTypeUnification);
			  //unifyType(consHeadTs.getKey(), resultTSInfo, consTailTs.getKey().getSibling(), listTypeUnification);
		populateSubstitution(resultTSInfo, listCoreUnification);
		
		//result type must be list type
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(consTailTs.getKey(), resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULCarExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> exprTs
			= wULMethodDecider(((CarExpr) input).getEpxr());
		
		//construct a list of varT
		AbstractAType listT = typeBuilder.getATypeInstance(input);
		
		//expression in car must be a list
		List<Substitution> listTypeUnification = unifyType(exprTs.getKey(), exprTs.getValue(), listT, null);
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, exprTs.getValue());
		populateSubstitution(resultTSInfo, listTypeUnification);
		
		//result type must be the list core's type
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(exprTs.getKey().getSibling(), resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULCdrExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> exprTs 
			= wULMethodDecider(((CdrExpr) input).getExpr());
		
		//construct a list of varT
		AbstractAType listT = typeBuilder.getATypeInstance(input);
		//expression in cdr must be a list
		List<Substitution> listTypeUnification = unifyType(exprTs.getKey(), exprTs.getValue(), listT, null);
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, exprTs.getValue());
		populateSubstitution(resultTSInfo, listTypeUnification);
		
		//result type must be list type
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(exprTs.getKey(), resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULNullListTestExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> nullListTestTs 
			= wULMethodDecider(((NullListTestExpr) input).getExpr());
		
		AbstractAType listT = typeBuilder.getATypeInstance(input);
		//expression in null list test must be a list
		List<Substitution> listTypeUnification = unifyType(nullListTestTs.getKey(), nullListTestTs.getValue(), listT, null);
				
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, nullListTestTs.getValue());
		populateSubstitution(resultTSInfo, listTypeUnification);
		
		//result type must be boolean
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(BoolAType.getInstance(), resultTSInfo);
		return result;
	}
	
	private List<Substitution> unifyType(AbstractAType left, List<Substitution> stLeft,
			AbstractAType right, List<Substitution> stRight){
		
		Set<AbstractAType> leftGroup = refineType(left, stLeft);
		Set<AbstractAType> rightGroup = refineType(right, stRight);
	
		if((left instanceof IntAType && right instanceof IntAType) 
			|| (left instanceof BoolAType && right instanceof BoolAType)
			|| (left == right)) {
			//do nothing;
			return null;
		}else if(left instanceof TVarAType && right instanceof IntAType) { 
			return unifyTVarInt(left, leftGroup);
		}else if(left instanceof TVarAType && right instanceof BoolAType) {
			return unifyTVarBool(left, leftGroup);
		}else if(left instanceof IntAType && right instanceof TVarAType) {
			return unifyTVarInt(right, rightGroup);
		}else if(left instanceof BoolAType && right instanceof TVarAType) {
			return unifyTVarBool(right, rightGroup);
		}else if(left instanceof TVarAType && right instanceof ListAType) {
			return unifyTVarTList(left, stLeft, right, stRight);
		}else if(left instanceof ListAType && right instanceof TVarAType) {
			return unifyTVarTList(right, stRight, left, stLeft);
		}else if(left instanceof ListAType && right instanceof ListAType) {
			return unifyTListPair(left, stLeft, right, stRight);
		}else if(left instanceof TVarAType && right instanceof TVarAType) {
			return unifyTVarPair(left, stLeft, right, stRight);
		}else {
			System.err.println("Type Error: " + left.getTypeInfo() + " ------- " + right.getTypeInfo());
			System.exit(enVersion);
		}
		return null;
	}
	
	private List<Substitution> unifyTVarInt(AbstractAType tVar, Set<AbstractAType> tVarGroup) {
		if(tVar.getTypeInfo().equals(IntAType.getInstance().getTypeInfo())) {
			return null;
		}
		else if(tVarGroup != null && tVarGroup.contains(IntAType.getInstance())) {
			//do nothing
		}
		else {
			if(tVarGroup != null) {
				for(AbstractAType at : tVarGroup) {
					if(at instanceof BoolAType || at instanceof ListAType) {
						System.err.println("Type error: " + at.toString() + " ---------- " + IntAType.getInstance().toString());
						System.exit(enVersion);
					}
				}
			}else {
				if(tVar.getTypeInfo().length() != 0 
						&& !tVar.getTypeInfo().equals(BoolAType.getInstance().getTypeInfo())) {
					System.err.println("Type error: " + tVar.toString() + " ---------- " + BoolAType.getInstance().getTypeInfo());
					System.exit(enVersion);
				}
			}
			
		}
		tVar.setTypeInfo(IntAType.getInstance().getTypeInfo());
		Substitution st =  new Substitution(tVar, IntAType.getInstance());
		List<Substitution> result = new Vector<Substitution>();
		result.add(st);
		return result;
	}
	
	private List<Substitution> unifyTVarBool(AbstractAType tVar, Set<AbstractAType> tVarGroup) {
		if(tVar.getTypeInfo().equals(BoolAType.getInstance().getTypeInfo())) {
			return null;
		}
		else if(tVarGroup != null && tVarGroup.contains(BoolAType.getInstance())) {
			//do nothing
		}else {
			if(tVarGroup != null) {
				for(AbstractAType at : tVarGroup) {
					if(at instanceof IntAType || at instanceof ListAType) {
						System.err.println("Type error: " + at.toString() + " ---------- " + BoolAType.getInstance().getTypeInfo());
						System.exit(enVersion);
					}
				}	
			}else {
				if(tVar.getTypeInfo().length() != 0 
				&& !tVar.getTypeInfo().equals(BoolAType.getInstance().getTypeInfo())) {
					System.err.println("Type error: " + tVar.toString() + " ---------- " + BoolAType.getInstance().getTypeInfo());
					System.exit(enVersion);
				}
			}
		}
		tVar.setTypeInfo(BoolAType.getInstance().getTypeInfo());
		Substitution st =  new Substitution(tVar, BoolAType.getInstance());
		List<Substitution> result = new Vector<Substitution>();
		result.add(st);
		return result;
	}
	
	private List<Substitution> unifyTVarTList(AbstractAType tVar, List<Substitution> tVarSTGroup, AbstractAType listT, List<Substitution> listSTGroup) {
		if(tVar.getTypeInfo().equals(IntAType.getInstance().getTypeInfo()) 
		|| tVar.getTypeInfo().equals(BoolAType.getInstance().getTypeInfo())) {
			//tVar is a Int or Bool
			System.err.println("Type error: " + tVar.getSibling() + " ---------- " + listT.getTypeInfo());
			System.exit(enVersion);
		}else {
			if(tVar == listT.getSibling()) { 
				//tVar is the list core
				tVarInListError(tVar, listT);
			} else {
				//collection all the information in the list core
				Set<AbstractAType> listCoreGroup = collectListCoreGroup(listT, listSTGroup);
				Set<AbstractAType> tVarGroup = refineType(tVar, tVarSTGroup);
				
				if(tVarGroup != null && listCoreGroup != null) {
					if(tVarGroup.removeAll(listCoreGroup)) {
						//two group has intersection
						tVarInListError(tVar, listT);
					}
				}else if(tVarGroup != null && listCoreGroup == null) {	
					if(tVarGroup.contains(listT.getSibling())) {
						//List's core in tVarGroup
						tVarInListError(tVar, listT);
					}
				}else if(tVarGroup == null && listCoreGroup != null) {
					if(listCoreGroup.contains(tVar)) {
						//tVar in list core's group
						tVarInListError(tVar, listT);
					}
				}
				//tVar is not in ListT
				AbstractAType listMemberInTVarGroup = null;
				if(tVarGroup != null) {
					for(AbstractAType at : tVarGroup) {
						if(at instanceof ListAType) {
							listMemberInTVarGroup = at;
							break;
						}
					}
				}
				if(listMemberInTVarGroup != null) {
					List<Substitution> listUnifyResult = unifyTListPair(listMemberInTVarGroup, tVarSTGroup, listT, listSTGroup);
					Substitution st =  new Substitution(tVar, listT);
					List<Substitution> result = new Vector<Substitution>();
					result.add(st);
					if(listUnifyResult != null) {
						result.addAll(listUnifyResult);
					}
					return result;	
				}
			}
		}
		tVar.setTypeInfo(listT.getTypeInfo());
		Substitution st =  new Substitution(tVar, listT);
		List<Substitution> result = new Vector<Substitution>();
		result.add(st);
		return result;		
	}

	private Set<AbstractAType> collectListCoreGroup(AbstractAType listT, List<Substitution> tVar2ATypeList) {
		Set<AbstractAType> collection = new HashSet<AbstractAType>();
		if(tVar2ATypeList != null) {
			collection.add(listT.getSibling());
			boolean updated = true;
			while(updated) {
				updated = false;
				for(Substitution st : tVar2ATypeList) {
					if(collection.contains(st.getLeft()) || collection.contains(st.getRight())) {
						updated |= collection.add(st.getLeft());
						updated |= collection.add(st.getRight());
						if(st.getLeft() instanceof ListAType) {
							updated |= collection.addAll(collectListCoreGroup(st.getLeft(), tVar2ATypeList));
						}
						if(st.getRight() instanceof ListAType) {
							updated |= collection.addAll(collectListCoreGroup(st.getRight(), tVar2ATypeList));
						}
					}
				}
			}
		}
		collection.remove(listT.getSibling());
		return collection;
	}
	
	private void tVarInListError(AbstractAType tVar, AbstractAType listT) {
		System.err.println("Type error: " + listT.toString() + "  containts " + tVar.toString());
		System.exit(enVersion);
	}
	
	private List<Substitution> unifyTListPair(AbstractAType leftList, List<Substitution> stLeft, AbstractAType rightList, List<Substitution> stRight) {
		if(leftList == rightList) {
			return null;
		}
		
		List<Substitution> matchResult = new Vector<Substitution>();
		List<Substitution> coreMatchResult = unifyTVarPair(leftList.getSibling(), stLeft, rightList.getSibling(), stRight); 
		if(coreMatchResult != null) {
			matchResult.addAll(coreMatchResult);
		}
		matchResult.add(new Substitution(leftList, rightList));
		return matchResult;
	}
	
	private List<Substitution> unifyTVarPair(AbstractAType left, List<Substitution> stLeft, AbstractAType right, List<Substitution> stRight) {
		if(left.getTypeInfo().equals(right.getTypeInfo())) {
			//INT <-> INT, BOOL <-> BOOL, "" <-> ""
			//List<> <-> List<>, List<INT> <-> List<INT>, List<Bool> <-> List<Bool>
			if(left != right) {
				Substitution tVar2TVar = new Substitution(left, right);
				List<Substitution> result = new Vector<Substitution>(); 
				result.add(tVar2TVar);
				return result;
			}else {
				return null;
			}
		}else {
			Set<AbstractAType> leftGroup = refineType(left, stLeft);
			Set<AbstractAType> rightGroup = refineType(right, stRight);
			if(leftGroup != null && rightGroup != null) {
				if(leftGroup.removeAll(rightGroup)) {
					//the two group has intersection
					Substitution tVar2TVar = new Substitution(left, right);
					List<Substitution> result = new Vector<Substitution>(); 
					result.add(tVar2TVar);
					return result;
				}else {
					List<Substitution> returnedResult = null;
					List<Substitution> result = new Vector<Substitution>(); 
					
					if(leftGroup.contains(IntAType.getInstance())) {
						returnedResult = unifyTVarInt(right, rightGroup);
					}else if (leftGroup.contains(BoolAType.getInstance())) {
						returnedResult = unifyTVarBool(right, rightGroup);
					}else if (rightGroup.contains(IntAType.getInstance())) {
						returnedResult = unifyTVarInt(left, leftGroup);
					}else if (rightGroup.contains(BoolAType.getInstance())) {
						returnedResult = unifyTVarBool(left, leftGroup);
					}else {
						AbstractAType listMember = null;
						if(leftGroup != null) {
							for(AbstractAType at : leftGroup) {
								if(at instanceof ListAType) {
									listMember = at;
									break;
								}
							}
						}
						if(listMember != null) {
							returnedResult = unifyTVarTList(right, stRight, listMember, stLeft);
						}else {
							if(rightGroup != null) {
								for(AbstractAType at : rightGroup) {
									if(at instanceof ListAType) {
										listMember = at;
										break;
									}
								}
							}
							if(listMember != null) {
								returnedResult = unifyTVarTList(left, stLeft, listMember, stRight);
							}
						}
					}
					//assemble the result
					if(returnedResult != null) {
						result.addAll(returnedResult);
					}
					Substitution tVar2TVar = new Substitution(left, right);
					result.add(tVar2TVar);
					return result;
				}
			}else if(leftGroup == null && rightGroup != null) {
				if(rightGroup.contains(left)) {
					//already in same group
					return null;
				}else {
					if(rightGroup.contains(IntAType.getInstance())) {
						return unifyTVarInt(left, leftGroup);
					}else if(rightGroup.contains(BoolAType.getInstance())) {
						return unifyTVarBool(left, leftGroup);
					}else {
						AbstractAType listInGroup = null;
						if(rightGroup != null) {
							for(AbstractAType at : rightGroup) {
								if(at instanceof ListAType) {
									listInGroup = at;
									break;
								}
							}
						}
						List<Substitution> result = new Vector<Substitution>();
						if(listInGroup != null) {
							List<Substitution> listUnifyResult = unifyTVarTList(left, stLeft, listInGroup, stRight);
							if(listUnifyResult != null) {
								result.addAll(listUnifyResult);
							}

						}
						result.add(new Substitution(left, right));
						return result;
					}
				}
			}else if(leftGroup != null && rightGroup == null) {
				if(leftGroup.contains(right)) {
					//already in same group
					return null;
				}else {
					if(leftGroup.contains(IntAType.getInstance())) {
						return unifyTVarInt(right, rightGroup);
					}else if(leftGroup.contains(BoolAType.getInstance())) {
						return unifyTVarBool(right, rightGroup);
					}else {
						AbstractAType listInGroup = null;
						if(leftGroup != null) {
							for(AbstractAType at : leftGroup) {
								if(at instanceof ListAType) {
									listInGroup = at;
									break;
								}
							}
						}
						List<Substitution> result = new Vector<Substitution>(); 
						if(listInGroup != null) {
							List<Substitution> listUnifyResult = unifyTVarTList(right, stRight, listInGroup, stLeft);
							if(listUnifyResult != null) {
								result.addAll(listUnifyResult);
							}
						}
						result.add(new Substitution(left, right));
						return result;
					}
				}
			}
		}
		
		return null;
	}
}
