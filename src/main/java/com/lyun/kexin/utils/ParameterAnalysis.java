package com.lyun.kexin.utils;

import java.io.*;
import java.util.List;

public class ParameterAnalysis {

    private static void drawProgress(int all,int now){
        int progress = (int)(((float)now/(float)all)*100.0);
        if (progress >= 0 && progress <= 100){
            StringBuilder res = new StringBuilder("[");
            int num = (int)(20.0 * ((float)progress/100.0));
            for (int i = 0; i < num; i++) {
                res.append("|");
            }
            for (int i = 0; i < (20 - num); i++) {
                res.append(" ");
            }
            res.append("]");
            System.out.println(res);
            for (int i = 0; i < 22; i++) {
                System.out.print("\b");
            }
        }
    }

    private static void clearTerminal(){
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")){
                Runtime.getRuntime().exec("cls");
            }else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        if (args.length == 3 && args[1].equals("o")){
            File inputDir = new File(args[0]);
            File outputDir = new File(args[2]);
            if (!inputDir.exists() || !outputDir.exists()){
                System.out.println("输入或输出文件不存在");
                return;
            }
            if (!inputDir.isDirectory() || !inputDir.isDirectory()){
                System.out.println("输入或输出文件夹不存在");
                return;
            }
            List<File> files = FileUtils.getDirJavaFile(args[0]);
            List<String> relativePath = FileUtils.getRelativePath(args[0]);
            for (int i = 0; i < files.size(); i++) {
                String out = FileAnalysis.singleFile(files.get(i).getPath());
                try {
                    String outPath = args[2] + "/" +
                            relativePath.get(i) + "/";
                    File dir = new File(outPath);
                    boolean mkdirs = dir.mkdirs();
                    String outName = outPath +
                            files.get(i).getName().replace(".java",".out");
                    FileWriter writer = new FileWriter(outName,false);
                    assert out != null;
                    writer.write(out);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                drawProgress(files.size(), i+1);
            }

        }
    }
}
