package com.lyun.kexin.utils;

import java.io.*;
import java.util.*;

public class FileUtils {

    /**
     * 读取文件内容
     * @param file 读取的文件
     * @return 文件的内容
     */
    public static String readJavaFileToString(File file){
        StringBuilder res = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String tmp = "";
            while ((tmp=bufferedReader.readLine())!=null){
                res.append(tmp);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    /**
     * 获取文件后缀的方法
     *
     * @param file 要获取文件后缀的文件
     * @return 文件后缀
     * @author https://www.4spaces.org/
     */
    public static String getFileExtension(File file) {
        String extension = "";
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
        return extension;
    }

    public static List<File> getDirJavaFile(String path){
        File dir = new File(path);
        List<File> res = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory())return res;
        Queue<File> queue = new LinkedList<>();
        queue.add(dir);
        while (!queue.isEmpty()) {
            File tmp = queue.poll();
            for (File file : Objects.requireNonNull(tmp.listFiles())) {
                if (file.isDirectory()) queue.offer(file);
                else if (getFileExtension(file).equals(".java"))
                    res.add(file);
            }
        }
        return res;
    }

    public static List<String> getRelativePath(String dir){
        File fileDir = new File(dir);
        List<String> res = new ArrayList<>();
        if (!fileDir.exists() || !fileDir.isDirectory())return res;
        Queue<File> queue = new LinkedList<>();
        queue.add(fileDir);
        while (!queue.isEmpty()) {
            File tmp = queue.poll();
            for (File file : Objects.requireNonNull(tmp.listFiles())) {
                if (file.isDirectory()) queue.offer(file);
                else if (getFileExtension(file).equals(".java")){
                    res.add("/"+file.getAbsolutePath().replace(dir,"").replace(file.getName(),""));
                }
            }
        }
        return res;
    }
}
