package com.anonymous.kexin.structure;

import com.anonymous.kexin.utils.TypeUtils;
import com.github.javaparser.ast.type.Type;

import java.lang.reflect.Parameter;

public class Param {

    public static String analysisParameter(Parameter parameter){
        return TypeUtils.getType((Type) parameter.getParameterizedType()) +
                "variable " + parameter.getName() + "\n";
    }

    public static String analysisParameter(com.github.javaparser.ast.body.Parameter parameter) {
        return TypeUtils.getType(parameter.getType()) +
                "variable " + parameter.getName() + "\n";
    }
}
