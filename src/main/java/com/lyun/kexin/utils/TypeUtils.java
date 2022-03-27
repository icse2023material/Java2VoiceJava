package com.lyun.kexin.utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
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

        StringBuilder res = new StringBuilder("define ");
        if (isPublic){
            res.append("public ");
        }else if (isPrivate){
            res.append("private ");
        }else if (isProtected){
            res.append("protected ");
        }
        if (isFinal) res.append("final ");
        if (isStatic) res.append("static ");


        if (varType instanceof ArrayType){
            //数组
            ArrayType type = (ArrayType) varType;
            res.append("list of ").append(type.getComponentType()).append(" variable ").append(name).append("\n");
        }else if (varType instanceof ClassOrInterfaceType){
            //类类型
            ClassOrInterfaceType type = (ClassOrInterfaceType) varType;
            int n = type.getChildNodes().size();
            StringBuilder typeName = new StringBuilder();
            ClassOrInterfaceType tmp = ((ClassOrInterfaceType) varType);
            while (true){
                if (tmp.getScope().isPresent()){
                    typeName.insert(0, " dot " + StringUtils.wordSplit(tmp.getName().getIdentifier()));
                    tmp = tmp.getScope().get();
                }else {
                    typeName.insert(0, StringUtils.wordSplit(tmp.getName().getIdentifier()));
                    break;
                }
            }
            typeName.append(" dot ").append(StringUtils.wordSplit(type.getName().getIdentifier()));
            if (type.getTypeArguments().isPresent()){
                NodeList<Type> types = type.getTypeArguments().get();
                res.append(typeName).append(" with ");
                for (int i = 0; i < types.size(); i++) {
                    ClassOrInterfaceType cor = ((ClassOrInterfaceType) types.get(i));
                    if (i != types.size() -1){
                        res.append(cor.getName().getIdentifier()).append(" and ");
                    }else {
                        res.append(cor.getName().getIdentifier());
                    }
                }
                res.append(type).append(" variable ").append(name).append("\n");
            }else {
                res.append(typeName).append(" variable ").append(name).append("\n");
            }

        }else if (varType instanceof PrimitiveType){
            //基本类型
            PrimitiveType type = (PrimitiveType) varType;
            res.append(type).append(" variable ").append(name).append("\n");
        }
        return res.toString();
    }
}
