package com.lyun.kexin;

import com.lyun.kexin.utils.ParameterAnalysis;

public class MainFunction {

    /**
     * 参数:<path>,[mode]<br/>
     * path:待转换的文件路径<br/>
     * mode:d文件夹下所有文件,s单个java文件,默认为s
     * @param args 传入参数
     */
    public static void main(String[] args) {
        ParameterAnalysis.analysis(args);
    }
}
