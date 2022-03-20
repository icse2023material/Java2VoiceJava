package com.lyun.kexin.structure;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.lyun.kexin.utils.StringUtils;
import com.lyun.kexin.utils.TypeUtils;

import java.util.Optional;

public class Block {

    public static void analysisStmt(Statement statement,StringBuilder res){
        if (statement instanceof ExpressionStmt){
            //
            res.append(analysisExpressionStmt((ExpressionStmt) statement));
        }else if (statement instanceof ReturnStmt){
            //返回块
            ReturnStmt returnStmt = (ReturnStmt) statement;
            res.append("return ");
            Optional<Expression> expression = returnStmt.getExpression();
            if (expression.isPresent()){
                res.append(Expr.analysisExpr(expression.get()));
            }else {
                res.append("null\n");
            }
        }else if (statement instanceof IfStmt){
            //if块
            res.append("define if\n");
            IfStmt ifStmt = ((IfStmt) statement);
            res.append(Expr.analysisExpr(ifStmt.getCondition()));
            res.append("move next\n");
            if (ifStmt.getThenStmt() instanceof BlockStmt){
                analysisBlock(res, ((BlockStmt) ifStmt.getThenStmt()));
                res.replace(res.length() - "move next\n".length(),res.length()-1,"");
            }else if (ifStmt.getThenStmt() instanceof ExpressionStmt){
                res.append(analysisExpressionStmt(((ExpressionStmt) ifStmt.getThenStmt())));
            }
            if (((IfStmt) statement).hasElseBlock()){
                res.append("move next\n");
                if (ifStmt.getElseStmt().isPresent()){
                    if (ifStmt.getElseStmt().get() instanceof BlockStmt){
                        analysisBlock(res, ((BlockStmt) ifStmt.getElseStmt().get()));
                        res.replace(res.length() - "move next\n".length(),res.length()-1,"");
                    }else if (ifStmt.getElseStmt().get() instanceof ExpressionStmt){
                        res.append(analysisExpressionStmt(((ExpressionStmt) ifStmt.getElseStmt().get())));
                    }
                }
            }
        }else if (statement instanceof ForStmt){
            //for循环
            ForStmt forStmt = ((ForStmt) statement);
            res.append("define for\n");
            if (forStmt.getInitialization().size() > 0){
                for (Expression expression : forStmt.getInitialization()) {
                    res.append(Expr.analysisExpr(expression));
                }
            }else res.append("move next\n");
            if (forStmt.getCompare().isPresent()){
                res.append(Expr.analysisExpr(forStmt.getCompare().get()));
            }else res.append("move next\n");
            if (forStmt.getUpdate().size() > 0){
                for (Expression expression : forStmt.getUpdate()) {
                    res.append(Expr.analysisExpr(expression));
                }
            }else res.append("move next\n");
//                    res.append(analysisBlock();)
            if (forStmt.getBody().isBlockStmt()){
                BlockStmt forBody = forStmt.getBody().asBlockStmt();
                analysisBlock(res,forBody);
            }else if (forStmt.getBody().isExpressionStmt()){
                ExpressionStmt expressionStmt = forStmt.getBody().asExpressionStmt();
                res.append(Expr.analysisExpr(expressionStmt.getExpression()));
            }
        }else if (statement instanceof BreakStmt){
            res.append("break\n");
        }else if (statement instanceof WhileStmt){
            WhileStmt whileStmt = ((WhileStmt) statement);
            res.append("define while\n");
            res.append(Expr.analysisExpr(whileStmt.getCondition()));
            if (whileStmt.getBody().isBlockStmt()){
                BlockStmt bodyBlock = whileStmt.getBody().asBlockStmt();
                analysisBlock(res,bodyBlock);
            }else if (whileStmt.getBody().isExpressionStmt()){
                ExpressionStmt expressionStmt = whileStmt.getBody().asExpressionStmt();
                res.append(Expr.analysisExpr(expressionStmt.getExpression()));
            }
            res.append("move next\n");
        }else if (statement instanceof SwitchStmt){
            res.append("define switch\b");
            //value
            res.append(Expr.analysisExpr(((SwitchStmt) statement).getSelector()));
            //entries
            for (SwitchEntry entry : ((SwitchStmt) statement).getEntries()) {
                if (entry.getLabels().size()>0)res.append(Expr.analysisExpr(entry.getLabels().get(0)));
                if (entry.getStatements().size()>0){
                    for (Statement statement1 : entry.getStatements()) {
                        analysisStmt(statement1,res);
                    }
                }else res.append("move next\n");
            }
        }
    }


    //分析代码块
    public static void analysisBlock(StringBuilder res, BlockStmt blockStmt){
        if (blockStmt.getChildNodes().size()>0){
            for (Node childNode : blockStmt.getChildNodes()) {
                analysisStmt(((Statement) childNode),res);
            }
        }
        res.append("move next\n");
    }

    //分析代码块中expression
    public static String analysisExpressionStmt(ExpressionStmt expressionStmt){
        StringBuilder res = new StringBuilder();
        Expression expression = expressionStmt.getExpression();
        if (expression instanceof VariableDeclarationExpr){
            //变量
            VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) expression;
            for (VariableDeclarator variable : variableDeclarationExpr.getVariables()) {
                res.append(TypeUtils.analysisVariableType(
                        variable.getType(),
                        false,false,false,false,variableDeclarationExpr.isFinal(), StringUtils.wordSplit(variable.getName().getIdentifier())
                ));
                Optional<Expression> initializer = variable.getInitializer();
                if (initializer.isPresent()){
                    res.append(Expr.analysisExpr(initializer.get()));
                }else {
                    res.append("move next\n");
                }

            }
        }else if (expression instanceof MethodCallExpr){
            //调用方法
            res.append(Expr.analysisExpr(expression));
        }else if (expression instanceof AssignExpr){
            //赋值块
            res.append("let ");
            res.append(Expr.analysisExpr(((AssignExpr) expression).getTarget()).replace('\n',' '));
            res.append("equal ");
            res.append(Expr.analysisExpr(((AssignExpr) expression).getValue()));
        }else if (expression instanceof UnaryExpr){
            res.append(Expr.analysisExpr(expression)).append("\n");
        }
        return res.toString();
    }
}
