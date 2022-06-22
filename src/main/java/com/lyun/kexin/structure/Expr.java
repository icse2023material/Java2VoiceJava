package com.lyun.kexin.structure;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
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
     *
     * @param expression 表达式
     * @return string
     */
    public static String analysisExpr(Expression expression) {

        if (expression instanceof IntegerLiteralExpr) {
            //将数值转化为英文数值
//            long n;
//            n = Integer.parseInt(((IntegerLiteralExpr) expression).getValue());

            int r = 10;
            if (((IntegerLiteralExpr) expression).getValue().startsWith("0x"))
                r = 16;
            else if (((IntegerLiteralExpr) expression).getValue().startsWith("0b"))
                r = 2;
            else if (((IntegerLiteralExpr) expression).getValue().startsWith("0"))
                r = 8;
            if (r != 10) {
                String value = ((IntegerLiteralExpr) expression).getValue();
                if(value.equals("0")){
                    value = "zero";
                }
                return "int " + value + "\n";
            }
            String tmp = ((IntegerLiteralExpr) expression).getValue();
            tmp = String.valueOf(Integer.valueOf(tmp, r));
            String num = NumberText.getInstance(NumberText.Lang.English).getText(tmp);
            return "int " + num + "\n";
        }
        else if (expression instanceof ArrayCreationExpr) {
            //新建数组
            if (!((ArrayCreationExpr) expression).getLevels().get(0).getDimension().isPresent()) {
                return "";
            }
            ArrayCreationExpr arrayAccessExpr = ((ArrayCreationExpr) expression);
            StringBuilder sizeExpr = new StringBuilder();
            for (ArrayCreationLevel level : arrayAccessExpr.getLevels()) {
                if (level.getDimension().isPresent()) {
                    sizeExpr.append(Expr.analysisExpr(level.getDimension().get()));
                }
            }
            return "new list " + ((ArrayCreationExpr) expression).getElementType().toString() + " size " + "expression\n" + sizeExpr + "\n";
        }
        else if (expression instanceof StringLiteralExpr) {
            //字符串
            return "string " + ((StringLiteralExpr) expression).getValue() + "\n";
        }
        else if (expression instanceof BooleanLiteralExpr) {
            //布尔值
            return "boolean " + (((BooleanLiteralExpr) expression).getValue() ? "true" : "false") + "\n";
        }
        else if (expression instanceof ArrayAccessExpr) {
            //索引数组
            ArrayAccessExpr arrayAccessExpr = ((ArrayAccessExpr) expression);
            String res = "variable " + StringUtils.wordSplit(arrayAccessExpr.getName().toString()) + " index expression\n";
            Expression index = arrayAccessExpr.getIndex();
            res += Expr.analysisExpr(index);
            return res;
        }
        else if (expression instanceof NameExpr) {
            //变量名
//            NameExpr nameExpr = ((NameExpr) expression);
//            if (nameExpr.)
            return "variable " + StringUtils.wordSplit(((NameExpr) expression).getName().getIdentifier()) + "\n";
        }
        else if (expression instanceof ObjectCreationExpr) {
            //新建实体
            if (expression.getChildNodes().size() == 1) {
                ClassOrInterfaceType tmp = (ClassOrInterfaceType) expression.getChildNodes().get(0);
                StringBuilder res = new StringBuilder("new instance " + StringUtils.wordSplit(tmp.getName().getIdentifier()));
                if (tmp.getTypeArguments().isPresent()) {
                    res.append(" with ");
                    for (int i = 0; i < tmp.getTypeArguments().get().size(); i++) {
                        if (i != tmp.getTypeArguments().get().size() - 1) {
                            res.append(tmp.getTypeArguments().get().get(i)).append(" and ");
                        } else {
                            res.append(tmp.getTypeArguments().get().get(i));
                        }
                    }

                    if (((ObjectCreationExpr) expression).getArguments().size() == 0) {
                        res.append("\nmove next");
                    }
                } else {
                    res.append("\nmove next");
                }
                return res.append("\n").toString();
            } else {
                ClassOrInterfaceType tmp = (ClassOrInterfaceType) expression.getChildNodes().get(0);
                StringBuilder res = new StringBuilder("new instance " + StringUtils.wordSplit(tmp.getName().getIdentifier()));
                if (tmp.getTypeArguments().isPresent()) {
                    res.append(" with ");
                    for (int i = 0; i < tmp.getTypeArguments().get().size(); i++) {
                        if (i != tmp.getTypeArguments().get().size() - 1) {
                            res.append(tmp.getTypeArguments().get().get(i)).append(" and ");
                        } else {
                            res.append(tmp.getTypeArguments().get().get(i));
                        }
                    }
                }
                res.append("\n");
                for (int i = 1; i < expression.getChildNodes().size(); i++) {
                    if (expression.getChildNodes().get(i) instanceof InitializerDeclaration) {
//                        res.append("init function\n");
                        // skip this case
                        continue;
                    } else if (expression.getChildNodes().get(i) instanceof ClassOrInterfaceDeclaration) {
                        // skip this case
                        continue;
                    } else if (expression.getChildNodes().get(i) instanceof MethodDeclaration) {
                        continue;
                    }
                    res.append(analysisExpr((Expression) expression.getChildNodes().get(i)));
                }
                res.append("move next\n");
                return res.toString();
            }
        }
        else if (expression instanceof EnclosedExpr) {
            //括号
            return "subexpression\n" + analysisExpr(((EnclosedExpr) expression).getInner());
        }
        else if (expression instanceof BinaryExpr) {
            //二分表达式
            return "expression " + SymbolUtil.getBinarySymbol(((BinaryExpr) expression).getOperator()) + " expression\n" +
                    analysisExpr(((BinaryExpr) expression).getLeft()) + analysisExpr(((BinaryExpr) expression).getRight());
        }
        else if (expression instanceof MethodCallExpr) {
            //方法调用
            MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
            StringBuilder res = new StringBuilder("");
            Optional<Expression> tmpExpr = methodCallExpr.getScope();
            if (tmpExpr.isPresent()) {
                Expression scopeExpr = tmpExpr.get();
                if (scopeExpr instanceof NameExpr) {
                    res.append(((NameExpr) scopeExpr).getName()).append(" ");
                } else {
                    res.insert(0, analysisExpr(scopeExpr));
                    //res.append("\n");
                    // workaround for function chain call.
                    int lastIndexOfMoveNext = res.lastIndexOf("move next");
                    if(lastIndexOfMoveNext>0){
                        res.replace(lastIndexOfMoveNext,res.length(),"");
                    }
                }
            }
            res.append("call ")
                    .append(StringUtils.wordSplit(methodCallExpr.getNameAsString()))
                    .append("\n");
            if (methodCallExpr.getArguments().size() > 0) {
                for (int i = 0; i < methodCallExpr.getArguments().size(); i++) {
                    Expression argExpr = methodCallExpr.getArguments().get(i);
                    res.append(analysisExpr(argExpr));
                }
            }
            //跳出参数
            res.append("move next\n");
            //跳出call chain,因为涉及到递归，只有最外层才能跳出call chain
            // 内部递归多加的move next需要去掉
             res.append("move next\n");


            return res.toString();
        }
        else if (expression instanceof FieldAccessExpr) {
            StringBuilder res = new StringBuilder();
            Optional<Expression> tmpExpr = Optional.of(expression);
            while (tmpExpr.isPresent()) {
                if (tmpExpr.get() instanceof FieldAccessExpr) {
                    FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) tmpExpr.get();
                    res.insert(0, " dot " + StringUtils.wordSplit(fieldAccessExpr.getName().getIdentifier()));
                    tmpExpr = Optional.ofNullable(fieldAccessExpr.getScope());
                } else if (tmpExpr.get() instanceof NameExpr) {
                    NameExpr nameExpr = (NameExpr) tmpExpr.get();
                    res.insert(0, StringUtils.wordSplit(nameExpr.getName().getIdentifier()));
                    break;
                } else if (tmpExpr.get() instanceof ThisExpr) {
                    res.insert(0, "this");
                    break;
                } else if (tmpExpr.get() instanceof MethodCallExpr) {
                    MethodCallExpr methodCallExpr = ((MethodCallExpr) tmpExpr.get());
                    res.insert(0, analysisExpr(methodCallExpr));
                    if (methodCallExpr.getScope().isPresent()) {
                        res.insert(0, " dot ");
                        tmpExpr = methodCallExpr.getScope();
                    } else break;
                } else if (tmpExpr.get() instanceof ArrayAccessExpr) {
                    ArrayAccessExpr arrayAccessExpr = (ArrayAccessExpr) tmpExpr.get();
                    res.insert(0, analysisExpr(arrayAccessExpr));
                    break;
                }
            }
            res.append("\n");
            return res.toString();
        }
        else if (expression instanceof UnaryExpr) {
            UnaryExpr unaryExpr = ((UnaryExpr) expression);
            String unaryExprInVoiceJava = analysisExpr(unaryExpr.getExpression());
            // i++, ++i case
            if(unaryExprInVoiceJava.startsWith("variable")){
                unaryExprInVoiceJava = unaryExprInVoiceJava.substring(8);
            }
            UnaryExpr.Operator operator = unaryExpr.getOperator();
            if (operator.isPostfix()) {
                return unaryExprInVoiceJava.replace('\n', ' ') + SymbolUtil.getUnarySymbol(operator) + "\n";
            } else if (operator.isPrefix()) {
                if(operator.asString().equals("!")){
                    return SymbolUtil.getUnarySymbol(operator) + "\n" + unaryExprInVoiceJava;
                } else {
                    return SymbolUtil.getUnarySymbol(operator).replace('\n', ' ') + " " + unaryExprInVoiceJava;
                }
            } else return "";
        }
        else if (expression instanceof VariableDeclarationExpr) {
            VariableDeclarationExpr variableDeclarationExpr = ((VariableDeclarationExpr) expression);
            StringBuilder res = new StringBuilder();
            for (VariableDeclarator variable : variableDeclarationExpr.getVariables()) {
                res.append(TypeUtils.analysisVariableType(
                        variable.getType(), false, false, false, false, false,
                        StringUtils.wordSplit(variable.getName().getIdentifier()))
                );
                if (variable.getInitializer().isPresent()) {
                    res.append(analysisExpr(variable.getInitializer().get()));
                } else res.append("move next\n");
            }
            return res.toString();
        }
        else if (expression instanceof InstanceOfExpr) {
            InstanceOfExpr instanceOfExpr = ((InstanceOfExpr) expression);
            String res = "";
            res += StringUtils.wordSplit(instanceOfExpr.getExpression().toString());
            res += " instance of\ntype ";
            res += StringUtils.wordSplit(instanceOfExpr.getType().toString()) + "\n";
            return res;
        }
        else if (expression instanceof CastExpr) {
            CastExpr castExpr = ((CastExpr) expression);
            String res = "cast expression\n";
            res += TypeUtils.getType(castExpr.getType());
            res += Expr.analysisExpr(castExpr.getExpression());
            return res;
        }
        else if (expression instanceof LambdaExpr) {
            //Lambda表达式
            StringBuilder res = new StringBuilder();
            res.append("lambda expression\n");
            LambdaExpr lambdaExpr = ((LambdaExpr) expression);
            for (int i = 0; i < lambdaExpr.getParameters().size(); i++) {
                Parameter parameter = lambdaExpr.getParameters().get(i);
                res.append("variable ").append(parameter.getName().getIdentifier()).append("\n");
            }
            res.append("move next\n");
            Block.analysisStmt(lambdaExpr.getBody(), res);
            res.append("move next\n");
            return res.toString();
        }
        else if (expression instanceof ThisExpr) {
            return "variable this\n";
        }
        else if (expression instanceof ClassExpr) {
            String typeCommand = TypeUtils.getType(((ClassExpr) expression).getType());
            typeCommand = typeCommand.substring(5, typeCommand.length() - 2); // remove "type " and "\n"
            // TODO: Class name must be Capitalized
            return typeCommand + " dot class\n";
        } else if (expression instanceof ConditionalExpr){
            StringBuilder res = new StringBuilder("conditional expression\n");
            Expression condition = ((ConditionalExpr) expression).getCondition();
            res.append(analysisExpr(condition));
            Expression thenExpression = ((ConditionalExpr) expression).getThenExpr();
            res.append(analysisExpr(thenExpression));
            Expression elseExpression = ((ConditionalExpr) expression).getElseExpr();
            res.append(analysisExpr(elseExpression));
            return res.toString();
        } else if (expression instanceof NullLiteralExpr){
            // TODO: support later
            return "string null\n";
        }
        else return "";
    }
}
