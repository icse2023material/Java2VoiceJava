package com.lyun.kexin.utils;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;

public class SymbolUtil {
    /**
     * 符号转字符串
     * @param operator 操作符号
     * @return 符号表示的字符串英文
     */
    public static String getBinarySymbol(BinaryExpr.Operator operator){
        switch (operator.asString()){
            case "+":
                return "plus";
            case "-":
                return "minus";
            case "*":
                return "times";
            case "/":
                return "divide";
            case "%":
                return "mod";
            case "<":
                return "less than";
            case "<=":
                return "less equal";
            case ">":
                return "greater than";
            case ">=":
                return "greater equal";
            case "==":
                return "double equal";
            case "&&":
                return "double and";
            case "&":
                return "single and";
            case "|":
                return "single or";
            case "||":
                return "double or";
            case "^":
                return "xor";
            case "!=":
                return "not equal";
            case "<<":
                return "left shift";
            case "<<<":
                return "unsigned left shift";
            case ">>":
                return "right shift";
            case ">>>":
                return "unsigned right shift";
            default:
                return "";
        }
    }

    public static String getUnarySymbol(UnaryExpr.Operator operator){
        switch (operator.asString()){
            case "--":
                return "minus minus";
            case "++":
                return "plus plus";
            default:
                return "";
        }
    }
}
