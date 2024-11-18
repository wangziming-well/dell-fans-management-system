package com.wzm.fans.util;


import java.lang.reflect.Field;

public class ReflectUtils {

    public static <T,R>  void setField(Object target, Class<T> clazz, String fieldName, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException("设置字段失败",e);
        }

    }

}
