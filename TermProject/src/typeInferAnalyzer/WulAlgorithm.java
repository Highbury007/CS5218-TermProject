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
	private Set<AbstractExpr> allATypeVars;
	private List<Substitution> allTVar2ATypes;
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
		allATypeVars = new HashSet<AbstractExpr>();
		allTVar2ATypes = new Vector<Substitution>();
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
		
		//refine the function body type
		refineType(env.get(funBodyExpr), funBodyTs.getValue());
		
		List<Substitution> unifyFunBody = unifyType(funBodyTs.getKey(), env.get(funBodyExpr));
		
		//refine function body type
		refineType(funBodyTs.getKey(), unifyFunBody);
			
		//refine the varList 
		for(AbstractExpr ae : ((FunDefExpr) input).getFunVarList()) {
			refineType(env.get(ae), unifyFunBody);
		}

		//construct result;
		List<Substitution> substitution = new Vector<Substitution>();
	
		populateSubstitution(substitution, funBodyTs.getValue());
		populateSubstitution(substitution, unifyFunBody);

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
		AbstractAType start = null;
		start = typeBuilder.getATypeInstance(input);
		
		Entry<AbstractAType, List<Substitution>> result 
		= new SimpleEntry<AbstractAType, List<Substitution>>(start, null);
		
		return result;
	} 
	
	private Entry<AbstractAType, List<Substitution>> wULNullListExpr(AbstractExpr input) {
		methodTraceInfo();
		//denote Null list as a List<?> first
		AbstractAType listCoreTVar = new TVarAType();
		AbstractAType listT = new ListAType();
		listT.setSibling(listCoreTVar);
		//AbstractAType resultTypeInfo = null;	
		//resultTypeInfo = typeBuilder.getATypeInstance(input);

		
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
		List<Substitution> resultTsInfo 
			= new Vector<Substitution>();
		
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
		refineType(conditionTs.getKey(), trueBranchTs.getValue());
		refineType(conditionTs.getKey(), falseBranchTs.getValue());
		//unify condition
		List<Substitution> unifyCondition  
			= unifyType(conditionTs.getKey(), BoolAType.getInstance());
		
		//refine the true branch		
		refineType(trueBranchTs.getKey(), falseBranchTs.getValue());
		refineType(trueBranchTs.getKey(), unifyCondition);
		//refine the false branch
		refineType(falseBranchTs.getKey(), unifyCondition);
		
		//unify the true and false branches
		List<Substitution> unifyTrueFalse 
			= unifyType(trueBranchTs.getKey(), falseBranchTs.getKey());

		//refine the result(false branch)
		refineType(falseBranchTs.getKey(), unifyCondition);
		refineType(falseBranchTs.getKey(), unifyTrueFalse);
		
		populateSubstitution(resultTsInfo, trueBranchTs.getValue());
		populateSubstitution(resultTsInfo, falseBranchTs.getValue());
		populateSubstitution(resultTsInfo, unifyCondition);
		populateSubstitution(resultTsInfo, unifyTrueFalse);

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
		System.out.println(argExprList.size());
		populateSubstitution(resultTsInfo, funVarTs.getValue());
		for(int i = 0; i < argExprList.size(); i ++) {
			updateEnvironment(resultTsInfo);
			Entry<AbstractAType, List<Substitution>> argTs 
				= wULMethodDecider(argExprList.get(i));
			argListTypeInfo.add(argTs.getKey());
			populateSubstitution(resultTsInfo, argTs.getValue());
		}
		
		//unify the varList and argList
		refineType(funVarTs.getKey(), resultTsInfo);
		
		AbstractExpr funExpr = funName2FunExpr.get(funName);
		List<AbstractExpr> varExprList = ((FunDefExpr) funExpr).getFunVarList();
		System.out.println(funExpr.getAbstractExpressions().size());
		System.out.println(varExprList.size());
		if(varExprList.size() != argExprList.size()) {
			System.err.println("VarList and argList does not match!");
			System.exit(enVersion);
		}
		
		List<Substitution> varArgUnification = new Vector<Substitution>();
		for(int i = 0; i < varExprList.size(); i ++) {
			refineType(env.get(varExprList.get(i)), varArgUnification);
			//System.out.println(i);
			refineType(argListTypeInfo.get(i), varArgUnification);
			List<Substitution> tVar2AType = 
				unifyType(env.get(varExprList.get(i)), argListTypeInfo.get(i));
			populateSubstitution(varArgUnification, tVar2AType);
		}
		//unify the function body
		AbstractAType funBodyTypeVar = new TVarAType();
		refineType(funVarTs.getKey(), varArgUnification);
		List<Substitution> funBodyUnification 
			= unifyType(funVarTs.getKey(), funBodyTypeVar);
		
		refineType(funBodyTypeVar, funBodyUnification);
		
		populateSubstitution(resultTsInfo, varArgUnification);
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

		refineType(leftOpdTs.getKey(), rightOpdTs.getValue());
		
		//unify the left operand
		AbstractAType opdAType = getBinaryOpdType(((BinaryOpExpr) input).getOpt());
		List<Substitution> leftOpdUnification = unifyType(leftOpdTs.getKey(), opdAType);

		//unify the right operand
		refineType(rightOpdTs.getKey(), leftOpdUnification);
		List<Substitution> rightOpdUnification = unifyType(rightOpdTs.getKey(), opdAType);
		//get the opt corresponding result
		AbstractAType optAType = getBinaryOptType(((BinaryOpExpr) input).getOpt());
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, leftOpdTs.getValue());
		populateSubstitution(resultTSInfo, rightOpdTs.getValue());
		populateSubstitution(resultTSInfo, leftOpdUnification);
		populateSubstitution(resultTSInfo, rightOpdUnification);
		
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
		AbstractAType listCoreTVar = new TVarAType();
		AbstractAType listT = new ListAType();
		listT.setSibling(listCoreTVar);
		
		List<Substitution> listTypeUnification = unifyType(consTailTs.getKey(), listT);
		
		refineType(consHeadTs.getKey(), consTailTs.getValue());
		refineType(consHeadTs.getKey(), listTypeUnification);
		refineType(listCoreTVar, listTypeUnification);
		List<Substitution> listCoreUnification = unifyType(consHeadTs.getKey(), listCoreTVar);
		
		//refineType(consHeadTs.getKey(), consTailTs.getValue());
		//refineType(consHeadTs.getKey(), listTypeUnification);
		//refineType(consHeadTs.getKey(), listCoreUnification);
		//refineType(consHeadTs.getKey(), consTailTs.getValue());
		refineType(consTailTs.getKey(), listTypeUnification);
		refineType(consTailTs.getKey(), listCoreUnification);


		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, consHeadTs.getValue());
		populateSubstitution(resultTSInfo, consTailTs.getValue());
		populateSubstitution(resultTSInfo, listTypeUnification);
		populateSubstitution(resultTSInfo, listCoreUnification);
		
		//AbstractAType resultList = new ListAType();
		//resultList.setSibling(consHeadTs.getKey());
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(consTailTs.getKey(), resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULCarExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> exprTs
			= wULMethodDecider(((CarExpr) input).getEpxr());
		
		//construct a list of varT
		AbstractAType listCoreTVar = new TVarAType();
		AbstractAType listT = new ListAType();
		listT.setSibling(listCoreTVar);
		
		List<Substitution> listCoreUnification = unifyType(exprTs.getKey(), listT);
		refineType(exprTs.getKey(), listCoreUnification);
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, exprTs.getValue());
		populateSubstitution(resultTSInfo, listCoreUnification);
		
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(exprTs.getKey(), resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULCdrExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> exprTs 
			= wULMethodDecider(((CdrExpr) input).getExpr());
		
		//construct a list of varT
		AbstractAType listCoreTVar = new TVarAType();
		AbstractAType listT = new ListAType();
		listT.setSibling(listCoreTVar);
		
		List<Substitution> listCoreUnification = unifyType(exprTs.getKey(), listT);
		refineType(exprTs.getKey(), listCoreUnification);
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, exprTs.getValue());
		populateSubstitution(resultTSInfo, listCoreUnification);
		
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(listT, resultTSInfo);
		
		return result;
	} 

	private Entry<AbstractAType, List<Substitution>> wULNullListTestExpr(AbstractExpr input) {
		methodTraceInfo();
		Entry<AbstractAType, List<Substitution>> nullListTestTs 
			= wULMethodDecider(((NullListTestExpr) input).getExpr());
		
		AbstractAType listCoreTVar = new TVarAType();
		AbstractAType listTVar = new ListAType();
		listTVar.setSibling(listCoreTVar);
		List<Substitution> listCoreUnification = unifyType(nullListTestTs.getKey(), listTVar);
		
		//result must be a boolean
		AbstractAType resultType = BoolAType.getInstance();
		
		List<Substitution> resultTSInfo = new Vector<Substitution>();
		populateSubstitution(resultTSInfo, nullListTestTs.getValue());
		populateSubstitution(resultTSInfo, listCoreUnification);
		
		Entry<AbstractAType, List<Substitution>> result 
			= new SimpleEntry<AbstractAType, List<Substitution>>(resultType, resultTSInfo);
		return result;
	}
	
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
			//unify the list core
			//two null list
			//first judge the recursion
			//AbstractAType leftCore = left;
			//AbstractAType rightCore = right;
			//while(leftCore.getSibling() instanceof ListAType){
			//	leftCore = leftCore.getSibling();
			//}
			/*
			if(left.getSibling() != null && right.getSibling() != null) {
				//return unifyType(left.getSibling(), right.getSibling());
			}else {
				if(left.getSibling() == null && right.getSibling() == null){
					
				}
				else if(left.getSibling() == null && right.getSibling() != null) {
					left.setSibling(right.getSibling());
				}else {
					right.setSibling(left.getSibling());
				}
			}
			*/
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
						if(env.get(ae).getTypeInfo() == "")
							env.setValue(ae, value);
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
		/*
		if(null != source && null != target && target.size() == 0) {
			target.addAll(source);
		}else if(null != source && null != target && target.size() != 0) {
			for(int i = 0; i < source.size(); i ++) {
				boolean isMerged = false;
				for(AbstractAType at : source.get(i).getSameTypeSet()) {
					for(int j = 0; j < target.size(); j ++) {
						if(target.get(j).getSameTypeSet().contains(at)) {
							target.get(j).getSameTypeSet().addAll(source.get(i).getSameTypeSet());
							isMerged = true;
							break;
						}
					}
				}
				
				if(!isMerged) {
					target.add(source.get(i));
				}
				
			}
		}
		*/
	}
	
	private void refineType(AbstractAType key, List<Substitution> tVar2TypeList) {
		if(null != tVar2TypeList && tVar2TypeList.size() != 0){
			for(Substitution st : tVar2TypeList) {
				//only refine when the right hand is more accurate
				
				if(st.getLeft().equals(key) && st.getRight().getTypeInfo() != "") {
					if(key.getTypeInfo() == "") {
						key.setSibling(st.getRight());
					}
				}else if(st.getRight().equals(key) && st.getLeft().getTypeInfo() != "") {
					if(key.getTypeInfo() == "") {
						key.setSibling(st.getLeft());
					}
				}
			}
			System.out.println(key.getTypeInfo());
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
	
	private void methodTraceInfo() {
		System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
	}
}
