package com.jandy.jwidget.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtString {


    /**
     * 字符串是否是中文
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        if (TextUtils.isEmpty(str)) return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c)) return true;
        }
        return false;
    }

    /**
     * 是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        try {
            String bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 字符串是否是纯字母
     * @param str
     * @return
     */
    public static boolean isPureLetter(String str){
        if (TextUtils.isEmpty(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 字符是否是中文
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;
    }
}
