package com.lyun.kexin.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.lyun.kexin.structure.Block;
import com.lyun.kexin.structure.Expr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public class FileAnalysis {


    private static String toVoiceJava(File file){
        JavaParser javaParser = new JavaParser();
        try {
            ParseResult<CompilationUnit> parse = javaParser.parse(file);
            if (parse.isSuccessful()){
                Optional<CompilationUnit> result = parse.getResult();
                if (result.isPresent()){
                    CompilationUnit compilationUnit = result.get();
                    List<Node> childNodes = compilationUnit.getChildNodes();
                    for (Node node : childNodes) {
                        if(node instanceof ImportDeclaration) {
                            //导包
                            String res = analysisImportNode(node);
                            System.out.println(res);
                        }else if(node instanceof PackageDeclaration) {
                            //package
                            String res = analysisPackageNode(node);
                            System.out.println(res);
                        }else if (node instanceof ClassOrInterfaceDeclaration){
                            //class or interface
                            String res = analysisClassOrInterface(node);
                            System.out.println(res);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解析package节点
     * @param node 节点
     * @return 解析成VoiceJava
     */
    private static String analysisPackageNode(Node node){
        Name tmp = ((PackageDeclaration) node).getName();
        StringBuilder str = new StringBuilder();
        while (true){
            str.insert(0, tmp.getIdentifier());
            if (tmp.getChildNodes().size() > 0){
                str.insert(0, " dot ");
            }else break;
            tmp = (Name) tmp.getChildNodes().get(0);
        }
        str.insert(0, "define package ");
        return str.toString();
    }

    /**
     * 解析import节点
     * @param node 节点
     * @return 解析成VoiceJava
     */
    private static String analysisImportNode(Node node){
        ImportDeclaration importDeclaration = (ImportDeclaration) node;
        Name tmp = ((ImportDeclaration) node).getName();
        StringBuilder str = new StringBuilder();
        while (true){
            str.insert(0, tmp.getIdentifier());
            if (tmp.getChildNodes().size() > 0){
                str.insert(0, " dot ");
            }else break;
            tmp = (Name) tmp.getChildNodes().get(0);
        }
        if (importDeclaration.isStatic()){
            str.insert(0, "import static ");
        }else str.insert(0,"import ");
        return str.toString();
    }

    private static String analysisClassOrInterface(Node node){
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
        if (classOrInterfaceDeclaration.isInterface()){
            // interface
            StringBuilder res = new StringBuilder("define " + (classOrInterfaceDeclaration.isPublic() ? "public interface" : "interface ") +
                    classOrInterfaceDeclaration.getName().getIdentifier() + "\n");
            for (BodyDeclaration<?> member : classOrInterfaceDeclaration.getMembers()) {
                if (member instanceof FieldDeclaration){
                    //变量
                    analysisField(res, (FieldDeclaration) member);
                }else if (member instanceof MethodDeclaration){
                    res.append("");
                }
            }
            return res.toString();
        }else {
            // class
            StringBuilder res = new StringBuilder("define ");
            if (classOrInterfaceDeclaration.isPublic())res.append("public ");
            else if (classOrInterfaceDeclaration.isPrivate())res.append("private ");
            else if (classOrInterfaceDeclaration.isProtected())res.append("protected ");
            if (classOrInterfaceDeclaration.isAbstract())res.append("abstract ");
            if (classOrInterfaceDeclaration.isFinal())res.append("final ");
            if (classOrInterfaceDeclaration.isStatic())res.append("static ");
            res.append("class ").append(classOrInterfaceDeclaration.getName().getIdentifier()).append("\n");
            for (BodyDeclaration<?> member : classOrInterfaceDeclaration.getMembers()) {
                if (member instanceof FieldDeclaration){
                    //变量
                    analysisField(res, (FieldDeclaration) member);
                }else if (member instanceof MethodDeclaration){
                    //方法
                    MethodDeclaration methodDeclaration = (MethodDeclaration) member;
                    res.append("define ");
                    if (methodDeclaration.isPublic())res.append("public ");
                    else if (methodDeclaration.isPrivate())res.append("private ");
                    else if (methodDeclaration.isProtected())res.append("protected ");
                    if (methodDeclaration.isStatic())res.append("static ");
                    res.append("function ");
                    res.append(methodDeclaration.getName().getIdentifier()).append(" \n");
                    res.append("type ").append(methodDeclaration.getType()).append(" \n");
                    if (methodDeclaration.getParameters().size()>0){
                        for (Parameter parameter : methodDeclaration.getParameters()) {
                            res.append("type ").append(
                                    TypeUtils.analysisVariableType(
                                            parameter.getType(),
                                            false,false,false,false,false,parameter.getName().getIdentifier()
                                    ).replace("define ","")
                            );
                        }
                        res.append("move next\n");
                    }else {
                        res.append("move next\n");
                    }
                    //以上完成方法创建的返回值以及参数
                    //以下完成方法结构体
                    Optional<BlockStmt> optionalBlockStmt = methodDeclaration.getBody();
                    if (optionalBlockStmt.isPresent()){
                        BlockStmt blockStmt = optionalBlockStmt.get();
                        Block.analysisBlock(res,blockStmt);
                    }else {
                        res.append("move next\n");
                    }
                }
            }
            return res.toString();
        }
    }

    public static void xx(){}



    //分析变量类型
    private static void analysisField(StringBuilder res, FieldDeclaration member) {
        for (VariableDeclarator variable : member.getVariables()) {
            //获取变量类型
            res.append(TypeUtils.analysisVariableType(variable.getType(),member.isPublic(),member.isPrivate(),member.isProtected(), member.isStatic(), member.isFinal(),variable.getName().getIdentifier()));
            //判断是否初始化
            if (variable.getInitializer().isPresent()){
                String str = analysisInitializer(variable);
                res.append(str);
            }else {
                res.append("move next\n");
            }
        }
    }





    private static String analysisInitializer(VariableDeclarator variableDeclarator){
        if (variableDeclarator.getInitializer().isPresent()){
            Expression expression = variableDeclarator.getInitializer().get();
            return Expr.analysisExpr(expression);
        }else return "";

    }






    /**
     * 将单独的java文件解析为VoiceJava
     * @param path 文件路径
     */
    public static void singleFile(String path){
        File file = new File(path);
        if (file.getName().split("\\.").length == 1 || !file.getName().split("\\.")[1].equals("java")){
            System.out.println("解析错误,未知的文件格式");
            return;
        }
        String res = toVoiceJava(file);
    }
}
