package com.wkc.library.common;

/**
 * @author xiaoniao
 * @date 2024/5/30 22:12
 * @deprecated 常量类
 */

public class Constant {
    public static final String CAPTCHA_KEY = "captcha_";
    public static final long CAPTCHA_TIME_OUT = 90;

    public static String getCaptchaKey(String uuid) {
        return "captcha:"+uuid;
    }

    public static String getTokenKey(String username) {
        return "token:"+username;
    }
}
