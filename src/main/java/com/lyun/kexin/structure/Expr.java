package com.lyun.kexin.structure;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.lyun.kexin.utils.NumberText;
import com.lyun.kexin.utils.StringUtils;
import com.lyun.kexin.utils.SymbolUtil;
import com.lyun.kexin.utils.TypeUtils;

import java.util.*;

public class Expr {
    /**
     * 分析表达式
     * @param expression 表达式
     * @return string
     */
    public static String analysisExpr(Expression expression){

        if (expression instanceof IntegerLiteralExpr){
            //将数值转化为英文数值
            String num = NumberText.getInstance(NumberText.Lang.English).getText(Integer.parseInt(((IntegerLiteralExpr) expression).getValue()));
            return "int " + num + "\n";
        }else if (expression instanceof ArrayCreationExpr){
            //新建数组
            if (!((ArrayCreationExpr) expression).getLevels().get(0).getDimension().isPresent()){
                return "";
            }
            ArrayCreationExpr arrayAccessExpr = ((ArrayCreationExpr) expression);
            StringBuilder sizeExpr = new StringBuilder();
            for (ArrayCreationLevel level : arrayAccessExpr.getLevels()) {
                if (level.getDimension().isPresent()){
                    sizeExpr.append(Expr.analysisExpr(level.getDimension().get()));
                }
            }
            return "new list " + ((ArrayCreationExpr) expression).getElementType().toString() + " size " + "expression\n" +sizeExpr +"\n";
        }else if (expression instanceof StringLiteralExpr){
            //字符串
            return "string " + ((StringLiteralExpr) expression).getValue() + "\n";
        }else if (expression instanceof BooleanLiteralExpr){
            //布尔值
            return "boolean " + (((BooleanLiteralExpr) expression).getValue() ? "true":"false") + "\n";
        }else if (expression instanceof ArrayAccessExpr){
            //索引数组
            return "variable " + ((ArrayAccessExpr) expression).getName().toString() + " index " + ((ArrayAccessExpr) expression).getIndex().toString() + "\n";
        }else if (expression instanceof NameExpr){
            //变量名
            return "variable " + ((NameExpr) expression).getName()+"\n";
        }else if(expression instanceof ObjectCreationExpr){
            //新建实体
            if (expression.getChildNodes().size() == 1){
                ClassOrInterfaceType tmp = (ClassOrInterfaceType) expression.getChildNodes().get(0);
                StringBuilder res = new StringBuilder("new instance " + StringUtils.wordSplit(tmp.getName().getIdentifier()));
                if (tmp.getTypeArguments().isPresent()){
                    res.append(" with ");
                    for (int i = 0; i < tmp.getTypeArguments().get().size(); i++) {
                        if (i != tmp.getTypeArguments().get().size() - 1){
                            res.append(tmp.getTypeArguments().get().get(i)).append(" and ");
                        }else {
                            res.append(tmp.getTypeArguments().get().get(i));
                        }
                    }
                }
                return res.append("\n").toString();
            }else {
                ClassOrInterfaceType tmp = (ClassOrInterfaceType) expression.getChildNodes().get(0);
                StringBuilder res = new StringBuilder("new instance " + StringUtils.wordSplit(tmp.getName().getIdentifier()));
                if (tmp.getTypeArguments().isPresent()){
                    res.append(" with ");
                    for (int i = 0; i < tmp.getTypeArguments().get().size(); i++) {
                        if (i != tmp.getTypeArguments().get().size() - 1){
                            res.append(tmp.getTypeArguments().get().get(i)).append(" and ");
                        }else {
                            res.append(tmp.getTypeArguments().get().get(i));
                        }
                    }
                }
                res.append("\n");
                for (int i = 1;i<expression.getChildNodes().size();i++){
                    res.append(analysisExpr((Expression) expression.getChildNodes().get(i)));
                }
                res.append("move next\n");
                return res.toString();
            }
        }else if (expression instanceof EnclosedExpr){
            //括号
            return "subexpression\n"+analysisExpr(((EnclosedExpr) expression).getInner());
        }else if (expression instanceof BinaryExpr){
            //二分表达式
            return "expression " + SymbolUtil.getBinarySymbol(((BinaryExpr) expression).getOperator()) + " expression\n" +
                    analysisExpr(((BinaryExpr) expression).getLeft()) + analysisExpr(((BinaryExpr) expression).getRight());
        }else if (expression instanceof MethodCallExpr){
            //方法调用
            MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
            StringBuilder res = new StringBuilder("call ");
//            StringBuilder tmpStr = new StringBuilder();
            Optional<Expression> tmpExpr = methodCallExpr.getScope();
            if (tmpExpr.isPresent()){
                Expression scopeExpr = tmpExpr.get();
                if (scopeExpr instanceof NameExpr){
                    res.append(((NameExpr) scopeExpr).getName()).append(" dot ");
                }else {
                    res.insert(0,analysisExpr(scopeExpr));
                    //res.append("\n");
                }
            }
            res.append(StringUtils.wordSplit(methodCallExpr.getNameAsString())).append("\n");
            if (methodCallExpr.getArguments().size() > 0){
                for (int i = 0; i < methodCallExpr.getArguments().size(); i++) {
                    Expression argExpr = methodCallExpr.getArguments().get(i);
                    res.append(analysisExpr(argExpr));
                }
            }
            //跳出参数
            res.append("move next\n");


            return res.toString();
        }else if(expression instanceof FieldAccessExpr){
            StringBuilder res = new StringBuilder();
            Optional<Expression> tmpExpr = Optional.of(expression);
            while (tmpExpr.isPresent()){
                if (tmpExpr.get() instanceof FieldAccessExpr){
                    FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) tmpExpr.get();
                    res.insert(0," dot " + StringUtils.wordSplit(fieldAccessExpr.getName().getIdentifier()));
                    tmpExpr = Optional.ofNullable(fieldAccessExpr.getScope());
                }else if (tmpExpr.get() instanceof NameExpr){
                    NameExpr nameExpr = (NameExpr) tmpExpr.get();
                    res.insert(0,StringUtils.wordSplit(nameExpr.getName().getIdentifier()));
                    break;
                }
            }
            res.append("\n");
            return res.toString();
        }else if (expression instanceof UnaryExpr){
            UnaryExpr unaryExpr = ((UnaryExpr) expression);
            if (unaryExpr.getOperator().isPostfix()){
                return analysisExpr(unaryExpr.getExpression()).replace('\n',' ') + SymbolUtil.getUnarySymbol(unaryExpr.getOperator()) + "\n";
            }else if (unaryExpr.getOperator().isPrefix()){
                return SymbolUtil.getUnarySymbol(unaryExpr.getOperator()).replace('\n',' ') + " " + analysisExpr(unaryExpr.getExpression());
            }else return "";
        }else if (expression instanceof VariableDeclarationExpr){
            VariableDeclarationExpr variableDeclarationExpr = ((VariableDeclarationExpr) expression);
            StringBuilder res = new StringBuilder();
            for (VariableDeclarator variable : variableDeclarationExpr.getVariables()) {
                res.append(TypeUtils.analysisVariableType(
                        variable.getType(),false,false,false,false,false,
                        StringUtils.wordSplit(variable.getName().getIdentifier()))
                );
                if (variable.getInitializer().isPresent()){
                    res.append(analysisExpr(variable.getInitializer().get()));
                }else res.append("move next\n");
            }
            return res.toString();
        }else if(expression instanceof InstanceOfExpr){
            InstanceOfExpr instanceOfExpr = ((InstanceOfExpr) expression);
            String res = "";
            res += StringUtils.wordSplit(instanceOfExpr.getExpression().toString());
            res += " instance of\ntype ";
            res += StringUtils.wordSplit(instanceOfExpr.getType().toString()) +"\n";
            return res;
        }else if (expression instanceof CastExpr){
            CastExpr castExpr = ((CastExpr) expression);
            String res = "cast expression\n";
            res+= TypeUtils.getType(castExpr.getType());
            res+= Expr.analysisExpr(castExpr.getExpression());
            return res;
        }else if (expression instanceof LambdaExpr){
            //Lambda表达式
            StringBuilder res = new StringBuilder();
            res.append("lambda expression\n");
            LambdaExpr lambdaExpr = ((LambdaExpr) expression);
            for (int i = 0; i < lambdaExpr.getParameters().size(); i++) {
                Parameter parameter = lambdaExpr.getParameters().get(i);
                res.append("variable ").append(parameter.getName().getIdentifier()).append("\n");
            }
            res.append("move next\n");
            Block.analysisStmt(lambdaExpr.getBody(),res);
            res.append("move next\n");
            return res.toString();
        }else return "";
    }
}
