package com.anonymous.kexin.structure;

import com.anonymous.kexin.utils.StringUtils;
import com.anonymous.kexin.utils.TypeUtils;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class Block {

    public static void analysisStmt(Statement statement, StringBuilder res) {
        if (statement instanceof ExpressionStmt) {
            //
            res.append(analysisExpressionStmt((ExpressionStmt) statement));
        } else if (statement instanceof ReturnStmt) {
            //返回块
            ReturnStmt returnStmt = (ReturnStmt) statement;
            res.append("return expression\n");
            Optional<Expression> expression = returnStmt.getExpression();
            if (expression.isPresent()) {
                res.append(Expr.analysisExpr(expression.get()));
            } else {
                res.append("null\n");
            }
        } else if (statement instanceof IfStmt) {
            //if块
            res.append("define if\n");
            IfStmt ifStmt = ((IfStmt) statement);
            res.append(Expr.analysisExpr(ifStmt.getCondition()));
            if (ifStmt.getThenStmt() instanceof BlockStmt) {
                analysisBlock(res, ((BlockStmt) ifStmt.getThenStmt()));
//                if(res.substring(res.length() - "move next\n".length(),res.length()).equals("move next\n")){
//                    res.replace(res.length() - "move next\n".length(), res.length() - 1, "");
//                }
            } else if (ifStmt.getThenStmt() instanceof ExpressionStmt) {
                res.append(analysisExpressionStmt(((ExpressionStmt) ifStmt.getThenStmt())));
            }
            if (((IfStmt) statement).hasElseBranch()) {
                Optional<Statement> elseStmtOptional = ifStmt.getElseStmt();
                if (elseStmtOptional.isPresent()) {
                    Statement elseStmt = elseStmtOptional.get();
                    if (elseStmt instanceof IfStmt){
                        StringBuilder newRes = new StringBuilder();
                        analysisStmt(elseStmt, newRes);
                        res.append(newRes.substring("define if\n".length(), newRes.length()));
                    } else if (elseStmt instanceof BlockStmt) {
                        res.append("move next\n");
                        analysisBlock(res, ((BlockStmt) ifStmt.getElseStmt().get()));
                    } else if (elseStmt instanceof ExpressionStmt) {
                        res.append("move next\n");
                        res.append(analysisExpressionStmt(((ExpressionStmt) elseStmt)));
                    }
                }
            } else {
                res.append("move next\n");
                res.append("move next\n");
            }
        } else if (statement instanceof ForStmt) {
            //for循环
            ForStmt forStmt = ((ForStmt) statement);
            res.append("define for\n");
            if (forStmt.getInitialization().size() > 0) {
                for (Expression expression : forStmt.getInitialization()) {
                    res.append(Expr.analysisExpr(expression));
                }
            } else res.append("move next\n");
            if (forStmt.getCompare().isPresent()) {
                res.append(Expr.analysisExpr(forStmt.getCompare().get()));
            } else res.append("move next\n");
            if (forStmt.getUpdate().size() > 0) {
                for (Expression expression : forStmt.getUpdate()) {
                    res.append(Expr.analysisExpr(expression));
                }
            } else res.append("move next\n");
//                    res.append(analysisBlock();)
            if (forStmt.getBody().isBlockStmt()) {
                BlockStmt forBody = forStmt.getBody().asBlockStmt();
                analysisBlock(res, forBody);
            } else if (forStmt.getBody().isExpressionStmt()) {
                ExpressionStmt expressionStmt = forStmt.getBody().asExpressionStmt();
                res.append(Expr.analysisExpr(expressionStmt.getExpression()));
            }
        } else if (statement instanceof BreakStmt) {
            res.append("break\n");
        } else if (statement instanceof ContinueStmt) {
            res.append("continue\n");
        } else if (statement instanceof WhileStmt) {
            WhileStmt whileStmt = ((WhileStmt) statement);
            res.append("define while\n");
            res.append(Expr.analysisExpr(whileStmt.getCondition()));
            if (whileStmt.getBody().isBlockStmt()) {
                BlockStmt bodyBlock = whileStmt.getBody().asBlockStmt();
                analysisBlock(res, bodyBlock);
            } else if (whileStmt.getBody().isExpressionStmt()) {
                ExpressionStmt expressionStmt = whileStmt.getBody().asExpressionStmt();
                res.append(Expr.analysisExpr(expressionStmt.getExpression()));
            }
//            res.append("move next\n");
        } else if (statement instanceof SwitchStmt) {
            res.append("define switch\n");
            //value
            res.append(Expr.analysisExpr(((SwitchStmt) statement).getSelector()));
            //entries
            for (SwitchEntry entry : ((SwitchStmt) statement).getEntries()) {
                if (entry.getLabels().size() > 0) res.append(Expr.analysisExpr(entry.getLabels().get(0)));
                if (entry.getStatements().size() > 0) {
                    for (Statement statement1 : entry.getStatements()) {
                        analysisStmt(statement1, res);
                    }
                } else res.append("move next\n");
            }
        } else if (statement instanceof TryStmt) {
            TryStmt tryStmt = ((TryStmt) statement);
            res.append("define try\n");
            analysisBlock(res, tryStmt.getTryBlock());
            for (int i = 0; i < tryStmt.getCatchClauses().size(); i++) {
                CatchClause catchClause = tryStmt.getCatchClauses().get(i);
                res.append("define catch\n");
                res.append(Param.analysisParameter(catchClause.getParameter()));
                analysisBlock(res, catchClause.getBody());
            }


        }
    }


    //分析代码块
    public static void analysisBlock(StringBuilder res, BlockStmt blockStmt) {
        List<Node> nodeList = blockStmt.getChildNodes();
        if (nodeList.size() > 0) {
            for (Node childNode : nodeList) {
                if (childNode instanceof LineComment) {
                    continue;
                }
                analysisStmt(((Statement) childNode), res);
            }
            int index = nodeList.size()-1;
            Node lastNode =nodeList.get(index);
            while(index >0 && lastNode instanceof LineComment){
                lastNode =nodeList.get(--index);
            }
            // If ReturnStmt, not move next needed.
            if (!(lastNode instanceof ReturnStmt) && !(lastNode instanceof  ContinueStmt) && !(lastNode instanceof  BreakStmt)) {
                res.append("move next\n");
            }
        } else {
            res.append("move next\n");
        }
    }

    //分析代码块中expression
    public static String analysisExpressionStmt(ExpressionStmt expressionStmt) {
        StringBuilder res = new StringBuilder();
        Expression expression = expressionStmt.getExpression();
        if (expression instanceof VariableDeclarationExpr) {
            //变量
            VariableDeclarationExpr variableDeclarationExpr = (VariableDeclarationExpr) expression;
            for (VariableDeclarator variable : variableDeclarationExpr.getVariables()) {
                res.append(TypeUtils.analysisVariableType(
                        variable.getType(),
                        false, false, false, false, variableDeclarationExpr.isFinal(), StringUtils.wordSplit(variable.getName().getIdentifier())
                ));
                Optional<Expression> initializer = variable.getInitializer();
                if (initializer.isPresent()) {
                    res.append(Expr.analysisExpr(initializer.get()));
                } else {
                    res.append("move next\n");
                }

            }
        } else if (expression instanceof MethodCallExpr) {
            //调用方法
            res.append(Expr.analysisExpr(expression));
//            res.append("move next\n");
//            String[] strings = res.toString().split("\n");
//            int i = 0;
//            while (strings[strings.length - 1 - i].equals("move next")) {
//                i++;
//            }
//            if (i > 1) {
//                StringBuilder tmp = new StringBuilder();
//                for (int l = 0; l < strings.length - i; l++) {
//                    tmp.append(strings[l]).append("\n");
//                }
//                res = new StringBuilder(tmp);
//                res.append("move next statement\n");
//            }
        } else if (expression instanceof AssignExpr) {
            //赋值块
            res.append("let ");
            String variableName =Expr.analysisExpr(((AssignExpr) expression).getTarget()).replace('\n', ' '); //.substring(9);
            if(variableName.startsWith("variable")){
                variableName = variableName.substring(9);
            }
            res.append(variableName);
            res.append("equal expression\n");
            res.append(Expr.analysisExpr(((AssignExpr) expression).getValue()));
            res.append("\n");
        } else if (expression instanceof UnaryExpr) {
            res.append(Expr.analysisExpr(expression)).append("\n");
        } else {
            res.append(Expr.analysisExpr(expression));
        }
        return res.toString();
    }
}
