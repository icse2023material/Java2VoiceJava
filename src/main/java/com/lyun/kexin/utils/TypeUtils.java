package com.lyun.kexin.utils;

import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

public class TypeUtils {

    /**
     * 分析变量的类型
     * @return voiceJava
     */
    public static String analysisVariableType(Type varType, boolean isPublic, boolean isPrivate, boolean isProtected, boolean isStatic, boolean isFinal, String name){

        String res = "define ";
        if (isPublic){
            res += "public ";
        }else if (isPrivate){
            res += "private ";
        }else if (isProtected){
            res += "protected ";
        }
        if (isFinal)res += "final ";
        if (isStatic)res += "static ";


        if (varType instanceof ArrayType){
            //数组
            ArrayType type = (ArrayType) varType;
            res += "list of " + type.getComponentType() + " variable " + name + "\n";
        }else if (varType instanceof ClassOrInterfaceType){
            //类类型
            ClassOrInterfaceType type = (ClassOrInterfaceType) varType;
            int n = type.getChildNodes().size();
            if (n == 1){
                res += type + " variable " + name + "\n";
            }else if (n == 2){
                res += StringUtils.wordSplit(type.getName().getIdentifier()) + " with " + StringUtils.wordSplit(((ClassOrInterfaceType)type.getChildNodes().get(1)).getName().getIdentifier()) +
                        " variable " + name +"\n";
            }else if (n == 3){
                res += StringUtils.wordSplit(type.getName().getIdentifier()) + " with " + StringUtils.wordSplit(((ClassOrInterfaceType)type.getChildNodes().get(1)).getName().getIdentifier()) +
                        " and " + StringUtils.wordSplit(((ClassOrInterfaceType)type.getChildNodes().get(2)).getName().getIdentifier()) +
                        " variable " + name +"\n";
            }
        }else if (varType instanceof PrimitiveType){
            //基本类型
            PrimitiveType type = (PrimitiveType) varType;
            res += type + " variable " + name + "\n";
        }
        return res;
    }
}
