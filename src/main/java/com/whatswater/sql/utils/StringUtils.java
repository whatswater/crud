package com.whatswater.sql.utils;


public class StringUtils {
    public static final String EMPTY = "";

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return !(value == null || value.length() == 0);
    }
}
