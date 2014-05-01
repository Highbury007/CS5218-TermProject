package dataStructure;

import dataStructure.Program.*;
import dataStructure.Type.*;

/**
 * @author WuJun A0106507M
 *
 */
public class FactoryHelper {

	/**
	 * 
	 */
	//private static final String programPackage = "dataStructure.Program.";
	//private static final String typePackage = "dataStructure.Type.";
	public static final String endFlag = "#";
	private static final String regex = " ";
	public static final String splitStr = "@";
	public static final String[] elements = {
		"definition", "number", "nil", "boolean", "variable", "if",
		"call", "binary", "cons", "car", "cdr", "null-test", "bracket",
		endFlag
	};
	
	public static final String[] exprClassNames = {
		/*
		programPackage + "FunDefExpr",
		programPackage + "NumConstantExpr", 
		programPackage + "NullListExpr", 
		programPackage + "BoolConstantExpr", 
		programPackage + "VariableExpr",
		programPackage + "IfExpr",
		programPackage + "FunCallExpr",
		programPackage + "BinaryOpExpr",
		programPackage + "ConsExpr",
		//"dataStructure.CarExpr",
		//"dataStructure.CdrExpr",
		programPackage + "DataDestructExpr",
		programPackage + "DataDestructExpr",
		programPackage + "NullListTestExpr",
		programPackage + "BracketedExpr",
		*/
		
		FunDefExpr.class.toString().split(regex)[1],
		NumConstantExpr.class.toString().split(regex)[1],
		NullListExpr.class.toString().split(regex)[1],
		BoolConstantExpr.class.toString().split(regex)[1],
		VariableExpr.class.toString().split(regex)[1],
		IfExpr.class.toString().split(regex)[1],
		FunCallExpr.class.toString().split(regex)[1],
		BinaryOpExpr.class.toString().split(regex)[1],
		ConsExpr.class.toString().split(regex)[1],
		CarExpr.class.toString().split(regex)[1],
		CdrExpr.class.toString().split(regex)[1],
		NullListTestExpr.class.toString().split(regex)[1],
		BracketedExpr.class.toString().split(regex)[1],
				
		endFlag 
	};
	
	public static final String[] typeClassNames = {
		TVarAType.class.toString().split(regex)[1],		//FunDef
		IntAType.class.toString().split(regex)[1],		//num
		ListAType.class.toString().split(regex)[1],		//Nil
		BoolAType.class.toString().split(regex)[1], 	//bool
		TVarAType.class.toString().split(regex)[1],		//Var
		TVarAType.class.toString().split(regex)[1], 	//If
		TVarAType.class.toString().split(regex)[1], 	//FunCall
		TVarAType.class.toString().split(regex)[1], 	//BinaryOp
		ListAType.class.toString().split(regex)[1], 	//Cons
		TVarAType.class.toString().split(regex)[1], 	//Car
		ListAType.class.toString().split(regex)[1], 	//Cdr
		BoolAType.class.toString().split(regex)[1], 	//NullTest
		TVarAType.class.toString().split(regex)[1], 	//Bracketed
		
		endFlag	
	};
	
	public static final String[] wulClassName = {
		
	};
	public FactoryHelper() {
		// TODO Auto-generated constructor stub
	}

}
