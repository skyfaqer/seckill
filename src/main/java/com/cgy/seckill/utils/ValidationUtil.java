package com.cgy.seckill.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern PATTERN_MOBILE = Pattern.compile("^1\\d{10}$");

    public static boolean isMobile(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher m = PATTERN_MOBILE.matcher(str);
        return m.matches();
    }
}
