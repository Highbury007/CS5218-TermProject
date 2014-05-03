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
	//private Set<AbstractExpr> allATypeVars;
	//private List<Substitution> allSubstitutions;
	private List<Substitution> substitutionGroup;
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
		//allATypeVars = new HashSet<AbstractExpr>();
		substitutionGroup = new Vector<Substitution>();
	}

	public boolean isTypeCheckPass(Pgm program) {
		//construct the mapping from function Name to expression
		if(program.getFunDefs() != null) {
			//System.out.println(program.getFunDefs().size());
			for(AbstractExpr ae : program.getFunDefs()) {
				funName2FunExpr.put(ae.getExprAttribute(), ae);
				//AbstractExpr value = new VariableExpr(ae.getExprAttribute());
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
					//System.out.print(env.get(var).getTypeInfo() + " ");
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
	
	//apply the named function rule
	private Entry<AbstractAType, List<Substitution>> wULFunDefExpr(AbstractExpr input) {
		methodTraceInfo();
		//if the function has been called before, it must has been analyzed
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
		//String funName = ((FunDefExpr) input).getFunName();
		AbstractExpr funTypeVar = new VariableExpr(funName);
		funName2FunVar.put(funName, funTypeVar);
		//only map the function Variable to the body type
		//the variable list type info can be got from the function name
		//using function name -> function expression -> function variable list
		env.put(funTypeVar, bodyTypeVar);
		
		AbstractExpr funBodyExpr = ((FunDefExpr) input).getFunBody();
		
		//apply the rule to function body
		Entry<AbstractAType, List<Substitution>> funBodyTs = wULMethodDecider(funBodyExpr);
		//List<Substitution> envRefinements = collectEnvRefinements(funBodyTs.getValue());
		//refine the function body type
		//refineType(env.get(funBodyExpr), funBodyTs.getValue());
		
		List<Substitution> funBodyUnification = unifyType(funBodyTs.getKey(), null, env.get(funBodyExpr), funBodyTs.getValue());
		
		//refine function body type
		//refineType(funBodyTs.getKey(), funBodyUnification);
		
		//refine the varList 
		//for(AbstractExpr ae : ((FunDefExpr) input).getFunVarList()) {
		//	refineType(env.get(ae), funBodyUnification);
		//}

		//construct result;
		List<Substitution> substitution = new Vector<Substitution>();
	
		populateSubstitution(substitution, funBodyTs.getValue());
		populateSubstitution(substitution, funBodyUnification);

		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(funBodyTs.getKey(), substitution);
		
		//System.out.println(start.getTypeInfo());
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
		//refineType(conditionTs.getKey(), trueBranchTs.getValue());
		//refineType(conditionTs.getKey(), falseBranchTs.getValue());
		List<Substitution> conditionRefinement = new Vector<Substitution>();
		populateSubstitution(conditionRefinement, conditionTs.getValue());
		populateSubstitution(conditionRefinement, trueBranchTs.getValue());
		populateSubstitution(conditionRefinement, falseBranchTs.getValue());
		//unify condition
		List<Substitution> conditionUnification = unifyType(conditionTs.getKey(), conditionRefinement, BoolAType.getInstance(), null);
		
		//refine the true branch		
		//refineType(trueBranchTs.getKey(), falseBranchTs.getValue());
		//refineType(trueBranchTs.getKey(), conditionUnification);
		List<Substitution> trueBranchRefinement = new Vector<Substitution>();
		
		populateSubstitution(trueBranchRefinement, trueBranchTs.getValue());
		populateSubstitution(trueBranchRefinement, falseBranchTs.getValue());
		populateSubstitution(trueBranchRefinement, conditionUnification);
		List<Substitution> envRefinements = collectEnvRefinements(conditionTs.getValue());
		populateSubstitution(trueBranchRefinement, envRefinements);
		//populateSubstitution(trueBranchRefinement, conditionUnification);
		
		//refine the false branch
		//refineType(falseBranchTs.getKey(), conditionUnification);
		List<Substitution> falseBranchRefinement = new Vector<Substitution>();
		populateSubstitution(falseBranchRefinement, falseBranchTs.getValue());
		populateSubstitution(falseBranchRefinement, conditionUnification);
		populateSubstitution(falseBranchRefinement, envRefinements);
		populateSubstitution(falseBranchRefinement, collectEnvRefinements(trueBranchTs.getValue()));
		
		//unify the true and false branches
		List<Substitution> trueFalseBranchUnification 
			= unifyType(trueBranchTs.getKey(), trueBranchRefinement, falseBranchTs.getKey(), falseBranchRefinement);

		//refine the result(false branch)
		//refineType(falseBranchTs.getKey(), conditionUnification);
		//refineType(falseBranchTs.getKey(), trueFalseBranchUnification);
		
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
		
		//populateSubstitution(resultTsInfo, funVarTs.getValue());
		for(int i = 0; i < argExprList.size(); i ++) {
			updateEnvironment(resultTsInfo);
			Entry<AbstractAType, List<Substitution>> argTs 
				= wULMethodDecider(argExprList.get(i));
			argListTypeInfo.add(argTs.getKey());
			populateSubstitution(resultTsInfo, argTs.getValue());
		}
		
		//unify the varList and argList
		//refineType(funVarTs.getKey(), resultTsInfo);
		
		AbstractExpr funExpr = funName2FunExpr.get(funName);
		List<AbstractExpr> varExprList = ((FunDefExpr) funExpr).getFunVarList();
		//System.out.println(funExpr.getAbstractExpressions().size());
		//System.out.println(varExprList.size());
		if(varExprList.size() != argExprList.size()) {
			System.err.println("VarList and argList does not match!");
			System.exit(enVersion);
		}
		
		List<Substitution> varArgUnification = new Vector<Substitution>();
		//put in the necessary environmen	t refinements
		populateSubstitution(varArgUnification, envRefinements);
		populateSubstitution(varArgUnification, resultTsInfo);
		
		populateSubstitution(resultTsInfo, funVarTs.getValue());
		List<Substitution> varArgRefinement = resultTsInfo;
		
		//populateSubstitution(varArgRefinement, funVarTs.getValue());
		//populateSubstitution(varArgRefinement, resultTsInfo);
		for(int i = 0; i < varExprList.size(); i ++) {
			//refineType(env.get(varExprList.get(i)), varArgUnification);
			//System.out.println(i);
			//refineType(argListTypeInfo.get(i), varArgUnification);
			List<Substitution> tVar2AType = 
				unifyType(env.get(varExprList.get(i)), varArgRefinement, argListTypeInfo.get(i), varArgUnification);
			populateSubstitution(varArgUnification, tVar2AType);
			populateSubstitution(varArgRefinement, tVar2AType);
		}
		//unify the function body
		AbstractAType funBodyTypeVar = new TVarAType();
		//refineType(funVarTs.getKey(), varArgUnification);
		
		List<Substitution> funBodyUnification = unifyType(funVarTs.getKey(), varArgRefinement, funBodyTypeVar, null);
		
		//refineType(funBodyTypeVar, funBodyUnification);
		
		//populateSubstitution(resultTsInfo, varArgUnification);
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

		//refineType(leftOpdTs.getKey(), rightOpdTs.getValue());
		
		//unify the left operand
		List<Substitution> leftOpdRefinements = new Vector<Substitution>();
		populateSubstitution(leftOpdRefinements, leftOpdTs.getValue());
		populateSubstitution(leftOpdRefinements, rightOpdTs.getValue());
		AbstractAType opdAType = getBinaryOpdType(((BinaryOpExpr) input).getOpt());
		List<Substitution> leftOpdUnification = unifyType(leftOpdTs.getKey(), leftOpdRefinements, opdAType, null);

		//unify the right operand
		//refineType(rightOpdTs.getKey(), leftOpdUnification);
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
		//AbstractAType listCoreTVar = new TVarAType();
		//AbstractAType listT = new ListAType();
		//listT.setSibling(listCoreTVar);
		AbstractAType listT = typeBuilder.getATypeInstance(input);
		//cons tail must be a list
		List<Substitution> consTailRefinements = new Vector<Substitution>();
		populateSubstitution(consTailRefinements, consTailTs.getValue());
		populateSubstitution(consTailRefinements, collectEnvRefinements(consHeadTs.getValue()));
		List<Substitution> listTypeUnification = unifyType(consTailTs.getKey(), consTailTs.getValue(), listT, null);
		
		//refineType(consHeadTs.getKey(), consTailTs.getValue());
		//refineType(consHeadTs.getKey(), listTypeUnification);
		//refineType(((ListAType) listT).getListCore(), listTypeUnification);
		//the cons's tail list core's type must be the same as the cons's head's type
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, consHeadTs.getValue());
		populateSubstitution(resultTSInfo, consTailTs.getValue());
		populateSubstitution(resultTSInfo, listTypeUnification);
		
		List<Substitution> listCoreUnification 
			= unifyType(consHeadTs.getKey(), resultTSInfo, ((ListAType) listT).getListCore(), listTypeUnification);
		
		//refineType(consHeadTs.getKey(), consTailTs.getValue());
		//refineType(consHeadTs.getKey(), listTypeUnification);
		//refineType(consHeadTs.getKey(), listCoreUnification);
		//refineType(consHeadTs.getKey(), consTailTs.getValue());
		//refineType(((ListAType) listT).getListCore(), listTypeUnification);
		//refineType(((ListAType) listT).getListCore(), listCoreUnification);


		//List<Substitution> resultTSInfo = new Vector<Substitution>();
		//populateSubstitution(resultTSInfo, consHeadTs.getValue());
		//populateSubstitution(resultTSInfo, consTailTs.getValue());
		//populateSubstitution(resultTSInfo, listTypeUnification);
		populateSubstitution(resultTSInfo, listCoreUnification);
		
		//result type must be list type
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(listT, resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULCarExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> exprTs
			= wULMethodDecider(((CarExpr) input).getEpxr());
		
		//construct a list of varT
		//AbstractAType listCoreTVar = new TVarAType();
		//AbstractAType listT = new ListAType();
		//listT.setSibling(listCoreTVar);
		AbstractAType listT = typeBuilder.getATypeInstance(input);
		
		//expression in car must be a list
		List<Substitution> listTypeUnification = unifyType(exprTs.getKey(), exprTs.getValue(), listT, null);
		//refineType(exprTs.getKey(), listTypeUnification);
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, exprTs.getValue());
		populateSubstitution(resultTSInfo, listTypeUnification);
		
		//result type must be the list core's type
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(((ListAType) listT).getListCore(), resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULCdrExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> exprTs 
			= wULMethodDecider(((CdrExpr) input).getExpr());
		
		//construct a list of varT
		//AbstractAType listCoreTVar = new TVarAType();
		//AbstractAType listT = new ListAType();
		//listT.setSibling(listCoreTVar);
		AbstractAType listT = typeBuilder.getATypeInstance(input);
		//expression in cdr must be a list
		List<Substitution> listTypeUnification = unifyType(exprTs.getKey(), exprTs.getValue(), listT, null);
	
		//refineType(((ListAType) listT).getListCore(), listTypeUnification);
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, exprTs.getValue());
		populateSubstitution(resultTSInfo, listTypeUnification);
		
		//result type must be list type
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(listT, resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULNullListTestExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> nullListTestTs 
			= wULMethodDecider(((NullListTestExpr) input).getExpr());
		
		//AbstractAType listCoreTVar = new TVarAType();
		//AbstractAType listTVar = new ListAType();
		//listTVar.setSibling(listCoreTVar);
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
				//return null;
			} else {
				//collection all the information in the list core
				Set<AbstractAType> listCoreGroup = collectListCoreGroup(listT, listSTGroup);
				Set<AbstractAType> tVarGroup = refineType(tVar, tVarSTGroup);
				
				if(tVarGroup != null && listCoreGroup != null) {
					if(tVarGroup.removeAll(listCoreGroup)) {
						//two group has intersection
						tVarInListError(tVar, listT);
						//return null;
					}
				}else if(tVarGroup != null && listCoreGroup == null) {	
					if(tVarGroup.contains(listT.getSibling())) {
						//List's core in tVarGroup
						tVarInListError(tVar, listT);
						//return null;
					}
				}else if(tVarGroup == null && listCoreGroup != null) {
					if(listCoreGroup.contains(tVar)) {
						//tVar in list core's group
						tVarInListError(tVar, listT);
						//return null;
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
			for(Substitution st : tVar2ATypeList) {
				if(st.getLeft().equals(listT.getSibling()) 
				|| st.getRight().equals(listT.getSibling())) {
					collection.add(st.getLeft());
					collection.add(st.getRight());
					if(st.getLeft() instanceof ListAType) {
						collection.addAll(collectListCoreGroup(st.getLeft(), tVar2ATypeList));
					}
					if(st.getRight() instanceof ListAType) {
						collection.addAll(collectListCoreGroup(st.getRight(), tVar2ATypeList));
					}
				}
			}
		}
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
	
	private List<Substitution> collectEnvRefinements(List<Substitution> refinements) {
		if(refinements != null && refinements.size() != 0) {
			Set<AbstractAType> envTVarSet = new HashSet<AbstractAType>(env.getValueList());
			List<Substitution> result = new Vector<Substitution>();
			for(Substitution st : refinements) {
				if(envTVarSet.contains(st.getLeft()) || envTVarSet.contains(st.getRight())) {
					result.add(st);
					envTVarSet.add(st.getLeft());
					envTVarSet.add(st.getRight());
				}
			}
			return result;
		}
		return null;
	}
	
	/*
	private List<Substitution> unifyType(AbstractAType left, AbstractAType right) {
		//Substitution var2Type = new Substitution();
		//all int type
		//String questionMark = "?";
		if((left != null && right == null) 
			|| (left == null && right != null)) {
			System.err.println("Type error!");
			System.exit(enVersion);
			return null;
		}
		if((left instanceof IntAType && right instanceof IntAType) 
			|| (left instanceof BoolAType && right instanceof BoolAType)
			|| (left == right)) {
			//do nothing;
			return null;
		}else if(left instanceof ListAType && right instanceof ListAType) {

			List<Substitution> st = null;
			if(left.getSibling() instanceof ListAType && right.getSibling() instanceof ListAType) {
				st = unifyType(left.getSibling(), right.getSibling());
			}
			Substitution tVar2AType = new Substitution(left, right);
			List<Substitution> tVar2ATypeList = new Vector<Substitution>();
			tVar2ATypeList.add(tVar2AType);
			if(st != null)
				tVar2ATypeList.addAll(st);
			return tVar2ATypeList;
			
		}else if((left instanceof ListAType && right instanceof TVarAType)
				|| (left instanceof IntAType && right instanceof TVarAType)
				|| (left instanceof BoolAType && right instanceof TVarAType)) {
			if(right.getTypeInfo() != "" && !right.getTypeInfo().equals(left.getTypeInfo())){
				System.err.println("Type Error: " + left.getTypeInfo() + " ---- " + right.getTypeInfo());
				System.exit(enVersion);
			}
			
			if(left instanceof ListAType) {
				if(isListTContainsTVar(left, right)){
					System.err.println("Error: The list contains the variable!");
					System.exit(enVersion);
				}
			}
			right.setSibling(left);
			Substitution tVar2AType = new Substitution(right, left);
			List<Substitution> tVar2ATypeList = new Vector<Substitution>();
			tVar2ATypeList.add(tVar2AType);
			return tVar2ATypeList;
		}else if((left instanceof TVarAType && right instanceof ListAType)
				|| (left instanceof TVarAType && right instanceof IntAType)
				|| (left instanceof TVarAType && right instanceof BoolAType)){
			if(left.getTypeInfo() != "" && !right.getTypeInfo().equals(left.getTypeInfo())){
				System.err.println("Type Error: " + left.getTypeInfo() + " ---- " + right.getTypeInfo());
				System.exit(enVersion);
			}
			
			if(right instanceof ListAType) {
				if(isListTContainsTVar(right, left)){
					System.err.println("Error: The list contains the variable!");
					System.exit(enVersion);
				}
			}
			left.setSibling(right);
			Substitution tVar2AType = new Substitution(left, right);
			List<Substitution> tVar2ATypeList = new Vector<Substitution>();
			tVar2ATypeList.add(tVar2AType);
			return tVar2ATypeList;

		}else if(left instanceof TVarAType && right instanceof TVarAType) {
			if(left == right) {
				return null;
			}else if(left != right){
				if(left.getTypeInfo() == "" && right.getTypeInfo() == "") {
					Substitution tVar2TVar = new Substitution(left, right);
					List<Substitution> tVar2TVarList = new Vector<Substitution>();
					tVar2TVarList.add(tVar2TVar);
					return tVar2TVarList;
				}
			}else if(left.getTypeInfo().equals(right.getTypeInfo())){
				return null;
			}else {
				System.err.println("Type error: " + left.getTypeInfo() + " ---- " + right.getTypeInfo());
				System.exit(enVersion);
				return null;
			}
		}else {
			System.err.println("Type error: " + left.getTypeInfo() + " ---- " + right.getTypeInfo());
			System.exit(enVersion);
		}
		return null;
	} 
	*/
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
			Set<AbstractAType> collection = new HashSet<AbstractAType>();
			for(Substitution st : substitutionList) {
				if(st.getLeft() == key && st.getRight() != key) {
					if(st.getRight().getTypeInfo().length() > typeInfo.length()) {
						typeInfo = st.getRight().getTypeInfo();
					}
					collection.add(st.getRight());
				}else if(st.getRight() == key && st.getLeft() != key) {
					collection.add(st.getLeft());
					if(st.getLeft().getTypeInfo().length() > typeInfo.length()) {
						typeInfo = st.getLeft().getTypeInfo();
					}
				}
			}
			//make the information more concrete
			for(AbstractAType at : collection) {
				if(at instanceof TVarAType) {
					at.setTypeInfo(typeInfo);
				}
			}
			if(collection.size() == 0)
				return null;
			else 
				return collection;
		}
		return null;
	}
	/*
	private void refineType(AbstractAType key, List<Substitution> tVar2TypeList) {
		if(null != tVar2TypeList && tVar2TypeList.size() != 0){
			//collect all related type variable
			/*
			for(Substitution st : tVar2TypeList) {
				//only refine when the right hand is more accurate
				
				if(st.getLeft().equals(key) && st.getRight().getTypeInfo() != "") {
					if(key.getTypeInfo() == "" ||
					   (key instanceof ListAType && key.getTypeInfo() == "List<>")) {
						key.setSibling(st.getRight());
						System.out.println(key.getTypeInfo());
					}
				}else if(st.getRight().equals(key) && st.getLeft().getTypeInfo() != "") {
					if(key.getTypeInfo() == "" ||
					   (key instanceof ListAType && key.getTypeInfo() == "List<>")) {
						key.setSibling(st.getLeft());
						System.out.println(key.getTypeInfo());
					}
				}
			}
			
			//key has no information
			if(!isTypeConcrete(key)) {
				Set<AbstractAType> group = new HashSet<AbstractAType>();
				group.add(key);
				//collect all the variable needed to be refined.
				for(Substitution st : tVar2TypeList) {
					if(group.contains(st.getLeft())){
						group.add(st.getRight());
						if(isTypeConcrete(st.getRight())) {
							
						}
					}else if(group.contains(st.getRight())) {
						group.add(st.getLeft());
						if(isTypeConcrete(st.getLeft())) {
							
						}
					}
				}
				if(isTypeConcrete(key)){
					//update all the group member;
					for(AbstractAType at : group) {
						
					}
				}
			}
			
			System.out.println(key.getTypeInfo());
		}
	}
	private boolean isTypeConcrete(AbstractAType at) {
		//methodTraceInfo();
		if(null == at) {
			System.err.println("Error in isTypeConcrete()");
		}else {
			if(at.getTypeInfo() == "") {
				return false;
			}else {
				if(at instanceof ListAType) {
					AbstractAType temp = at;
					while(temp != null && !(temp instanceof ListAType)) {
						temp = temp.getSibling();
					}
					if(temp == null || temp.getTypeInfo() == "") {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	*/

	/*
	private void updateType(AbstractAType target, AbstractAType source) {
		if(target instanceof IntAType ||
		   target instanceof BoolAType) {
			
		}else if(target instanceof TVarAType) {
			target.setSibling(source);
		}else if(target instanceof ListAType) {
			
		}
	}
	
	private boolean isListTContainsTVar(AbstractAType listT, AbstractAType tVar){
		
		AbstractAType core = listT;
		while(core.getSibling() != null) {
			core = core.getSibling();
		}
		
		if(core == tVar && core.getTypeInfo() == "" && tVar.getTypeInfo() == "") {
			return true;
		}
		
		return false;
	}
	*/
	private void methodTraceInfo() {
		System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
	}
}
