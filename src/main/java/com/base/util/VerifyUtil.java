package com.base.util;

import java.util.regex.Pattern;

/**
 * 创建人：郑晓辉
 * 创建日期：2016/3/18
 * 描述：
 */
public class VerifyUtil {

    /**
     * 是不是全数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    /**
     * 是不是手机号码或电话号码
     * @param str
     * @return
     */
    public static boolean isPhoneAddMobile(String str) {
        boolean isVaild = Pattern.compile("^0?\\d{11}$").matcher(str).matches();
        if (!isVaild) {
            return Pattern.compile("^\\(?\\d{3,4}[-\\)]?\\d{7,8}$").matcher(str).matches();
        } else {
            return isVaild;
        }
    }

    /**
     * 是不是手机号码
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        return Pattern.compile("^0?\\d{11}$").matcher(str).matches();
    }

    /**
     * 是不是电子邮箱
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return Pattern.compile(str).matcher(email).matches();
    }
}
