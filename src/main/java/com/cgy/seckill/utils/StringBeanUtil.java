package com.cgy.seckill.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

public class StringBeanUtil {

    @SuppressWarnings("unchecked")
    public static <T> T stringToBean(String str, Class<T> cl) {
        if (StringUtils.isEmpty(str) || cl == null) {
            return null;
        }
        if (cl == int.class || cl == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (cl == long.class || cl == Long.class) {
            return (T) Long.valueOf(str);
        } else if (cl == String.class) {
            return (T) str;
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), cl);
        }
    }

    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> cl = value.getClass();
        if(cl == int.class || cl == Integer.class || cl == long.class || cl == Long.class) {
            return "" + value;
        } else if (cl == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }
    }
}
