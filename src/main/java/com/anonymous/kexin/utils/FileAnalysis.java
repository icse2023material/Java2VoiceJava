package com.anonymous.kexin.utils;

import com.anonymous.kexin.structure.Block;
import com.anonymous.kexin.structure.Expr;
import com.anonymous.kexin.utils.TypeUtils;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public class FileAnalysis {

  private static String toVoiceJava(File file) {
    JavaParser javaParser = new JavaParser();
    StringBuilder res = new StringBuilder();
    try {
      ParseResult<CompilationUnit> parse = javaParser.parse(file);
      if (parse.isSuccessful()) {
        Optional<CompilationUnit> result = parse.getResult();
        if (result.isPresent()) {
          CompilationUnit compilationUnit = result.get();
          Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
          if (packageDeclaration.isPresent()) {
            res.append(analysisPackageNode(packageDeclaration.get()));
          }
          NodeList<ImportDeclaration> importDeclarations = compilationUnit.getImports();
          if (importDeclarations.size() != 0) {
            for (ImportDeclaration importDeclaration : importDeclarations) {
              res.append(analysisImportNode(importDeclaration));
            }
            res.append("move next\n");
          }

          List<Node> childNodes = compilationUnit.getChildNodes();
          for (Node node : childNodes) {
            if (node instanceof ClassOrInterfaceDeclaration) {
              // class or interface
              res.append(analysisClassOrInterface(node));
              // System.out.println(res);
            }
          }
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return res.toString();
  }

  /**
   * 解析package节点
   * 
   * @param node 节点
   * @return 解析成VoiceJava
   */
  private static String analysisPackageNode(Node node) {
    Name tmp = ((PackageDeclaration) node).getName();
    StringBuilder str = new StringBuilder();
    while (true) {
      str.insert(0, StringUtils.wordSplit(tmp.getIdentifier()));
      if (tmp.getChildNodes().size() > 0) {
        str.insert(0, " dot ");
      } else
        break;
      tmp = (Name) tmp.getChildNodes().get(0);
    }
    str.insert(0, "define package ");
    return str.toString() + "\n";
  }

  /**
   * 解析import节点
   * 
   * @param node 节点
   * @return 解析成VoiceJava
   */
  private static String analysisImportNode(Node node) {
    ImportDeclaration importDeclaration = (ImportDeclaration) node;
    Name tmp = ((ImportDeclaration) node).getName();
    StringBuilder str = new StringBuilder();
    while (true) {
      str.insert(0, StringUtils.wordSplit(tmp.getIdentifier()));
      if (tmp.getChildNodes().size() > 0) {
        str.insert(0, " dot ");
      } else
        break;
      tmp = (Name) tmp.getChildNodes().get(0);
    }
    if (importDeclaration.isStatic()) {
      str.insert(0, "import static ");
    } else
      str.insert(0, "import ");
    if (importDeclaration.isAsterisk()) {
      str.append(" dot star");
    }
    str.append("\n");
    return str.toString();
  }

  private static String analysisClassOrInterface(Node node) {
    ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
    if (classOrInterfaceDeclaration.isInterface()) {
      // interface
      StringBuilder res = new StringBuilder(
          "define " + (classOrInterfaceDeclaration.isPublic() ? "public interface " : "interface ") +
              StringUtils.wordSplit(classOrInterfaceDeclaration.getName().getIdentifier()) + "\n");
      for (BodyDeclaration<?> member : classOrInterfaceDeclaration.getMembers()) {
        if (member instanceof FieldDeclaration) {
          // 变量
          analysisField(res, (FieldDeclaration) member);
        } else if (member instanceof MethodDeclaration) {
          res.append("");
        }
      }
      return res.toString();
    } else {
      // class
      StringBuilder res = new StringBuilder("define ");
      if (classOrInterfaceDeclaration.isPublic())
        res.append("public ");
      else if (classOrInterfaceDeclaration.isPrivate())
        res.append("private ");
      else if (classOrInterfaceDeclaration.isProtected())
        res.append("protected ");
      if (classOrInterfaceDeclaration.isAbstract())
        res.append("abstract ");
      if (classOrInterfaceDeclaration.isFinal())
        res.append("final ");
      if (classOrInterfaceDeclaration.isStatic())
        res.append("static ");
      res.append("class ").append(StringUtils.wordSplit(classOrInterfaceDeclaration.getName().getIdentifier()))
          .append("\n");

      // Type Parameter
      if(classOrInterfaceDeclaration.getTypeParameters().size()>0){
        for (int i = 0; i < classOrInterfaceDeclaration.getTypeParameters().size(); i++) {
          res.append("type ")
              .append(StringUtils
                  .wordSplit(classOrInterfaceDeclaration.getTypeParameters().get(i).getName().getIdentifier()))
              .append("\n");
        }
        res.append("move next\n");
        for (int i = 0; i < classOrInterfaceDeclaration.getExtendedTypes().size(); i++) {
          res.append(TypeUtils.getType(classOrInterfaceDeclaration.getExtendedTypes().get(i)));
        }
        res.append("move next\n");
        for (int i = 0; i < classOrInterfaceDeclaration.getImplementedTypes().size(); i++) {
          res.append(TypeUtils.getType(classOrInterfaceDeclaration.getImplementedTypes().get(i)));
        }
        res.append("move next\n");
      } else {
        res.append("move next body\n");
      }

      for (BodyDeclaration<?> member : classOrInterfaceDeclaration.getMembers()) {
        if (member instanceof FieldDeclaration) {
          // 变量
          analysisField(res, (FieldDeclaration) member);
        } else if (member instanceof MethodDeclaration) {
          // 方法
          MethodDeclaration methodDeclaration = (MethodDeclaration) member;
          res.append("define ");
          if (methodDeclaration.isPublic())
            res.append("public ");
          else if (methodDeclaration.isPrivate())
            res.append("private ");
          else if (methodDeclaration.isProtected())
            res.append("protected ");
          if (methodDeclaration.isStatic())
            res.append("static ");
          res.append("function ");
          res.append(StringUtils.wordSplit(methodDeclaration.getName().getIdentifier())).append(" \n");
          Type methodType = methodDeclaration.getType();
          res.append(TypeUtils.getType(methodType));
//          res.append("type ").append(StringUtils.wordSplit(methodDeclaration.getType().toString())).append(" \n");
          if (methodDeclaration.getParameters().size() > 0) {
            for (Parameter parameter : methodDeclaration.getParameters()) {
              // res.append("variable
              // ").append(parameter.getName().getIdentifier()).append("\n").append(
              // TypeUtils.getType(parameter.getType())
              res.append(TypeUtils.getType(parameter.getType()))
                  .append("variable ").append(parameter.getName().getIdentifier()).append("\n");
            }
            res.append("move next body\n");
          } else {
            res.append("move next\n");
          }
          // 以上完成方法创建的返回值以及参数
          // 以下完成方法结构体
          Optional<BlockStmt> optionalBlockStmt = methodDeclaration.getBody();
          if (optionalBlockStmt.isPresent()) {
            BlockStmt blockStmt = optionalBlockStmt.get();
            Block.analysisBlock(res, blockStmt);
          } else {
            res.append("move next\n");
          }
        }
      }
      return res.toString();
    }
  }

  public static void xx() {
  }

  // 分析变量类型
  private static void analysisField(StringBuilder res, FieldDeclaration member) {
    for (VariableDeclarator variable : member.getVariables()) {
      // 获取变量类型
      res.append(TypeUtils.analysisVariableType(variable.getType(),
          member.isPublic(), member.isPrivate(), member.isProtected(),
          member.isStatic(), member.isFinal(), StringUtils.wordSplit(variable.getName().getIdentifier())));
      // 判断是否初始化
      if (variable.getInitializer().isPresent()) {
        String str = analysisInitializer(variable);
        res.append(str);
      } else {
        res.append("move next\n");
      }
    }
  }

  private static String analysisInitializer(VariableDeclarator variableDeclarator) {
    if (variableDeclarator.getInitializer().isPresent()) {
      Expression expression = variableDeclarator.getInitializer().get();
      return Expr.analysisExpr(expression);
    } else
      return "";

  }

  /**
   * 将单独的java文件解析为VoiceJava
   * 
   * @param path 文件路径
   */
  public static String singleFile(String path) {
    File file = new File(path);
    if (file.getName().split("\\.").length == 1 || !file.getName().split("\\.")[1].equals("java")) {
      System.out.println("解析错误,未知的文件格式");
      return null;
    }
    return toVoiceJava(file);
  }
}
