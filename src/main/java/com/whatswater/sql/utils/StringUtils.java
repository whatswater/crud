package com.whatswater.sql.utils;


public class StringUtils {
    public static final String EMPTY = "";
    public static final String DOT = ".";
    public static final String COMMA = ",";


    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return !(value == null || value.length() == 0);
    }

    public static boolean startsWith(StringBuilder sb, String prefix) {
        return startsWith(sb, prefix, 0);
    }

    public static boolean startsWith(StringBuilder sb, String prefix, int offset) {
        if (offset < 0 || sb.length() - offset < prefix.length()) {
            return false;
        }

        int len = prefix.length();
        for (int i = 0; i < len; ++i) {
            if (sb.charAt(offset + i) != prefix.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static boolean endsWith(StringBuilder sb, String prefix) {
        int offset = sb.length() - prefix.length();
        return startsWith(sb, prefix, offset);
    }
}
