package com.anonymous.kexin.utils;

public class StringUtils {

    private final static char[] caps = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    /**
     * 拆分单词
     * @param str 待拆分的字符串
     * @return 拆分后的字符串
     */
    public static String wordSplit(String str){
        char[] chars = str.toCharArray();
        StringBuilder res = new StringBuilder();
        res.append(chars[0]);
        int before = 0;
        for (int i = 1; i < chars.length; i++) {
            for (int j = 0; j < 26; j++) {
                if (chars[i] == caps[j]){
                    if (i - before == 1 || chars[i-1] == '_'){
                        before = i;
                        break;
                    }
                    res.append(' ');
                    before = i;
                    break;
                }
            }
            res.append(chars[i]);
        }
        return res.toString().toLowerCase();
    }
}
