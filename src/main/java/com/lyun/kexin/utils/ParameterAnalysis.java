package com.lyun.kexin.utils;

import java.io.File;
import java.util.List;

public class ParameterAnalysis {

    public static void analysis(String[] args){
        if (args.length == 0){
            System.out.println("缺少参数");
            return;
        }
        //单文件模式
        if (args.length == 1 || (args.length == 2 && args[1].equals("s"))){
            File singleFile = new File(args[0]);
            if (!singleFile.exists()){
                System.out.println("文件不存在");
                return;
            }
            if (!FileUtils.getFileExtension(singleFile).equals(".java")){
                System.out.println("错误的文件后缀");
                return;
            }
            System.out.println(FileAnalysis.singleFile(singleFile.getPath()));
        }
        //文件夹模式
        if (args.length == 2 && args[1].equals("d")){
            File singleFile = new File(args[0]);
            if (!singleFile.exists()){
                System.out.println("文件不存在");
                return;
            }
            if (!singleFile.isDirectory()){
                System.out.println("该文件不是文件夹");
                return;
            }
            List<File> files = FileUtils.getDirJavaFile(args[0]);
            for (File file : files) {
                System.out.println(file.getName() + "=====>\n");
                System.out.println(FileAnalysis.singleFile(file.getPath()));
            }
        }
    }
}
