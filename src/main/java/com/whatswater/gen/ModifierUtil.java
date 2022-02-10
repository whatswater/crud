package com.whatswater.gen;


public abstract class ModifierUtil {
    public static int setSynchronized(int modifier) {
        return modifier | (1 << 6);
    }
    public static int setNotSynchronized(int modifier) {
        return modifier & (~(1 << 6));
    }
    public static boolean isSynchronized(int modifier) {
        return (modifier & (1 << 6)) != 0;
    }

    public static int setTransient(int modifier) {
        return modifier | (1 << 5);
    }
    public static int setNotTransient(int modifier) {
        return modifier & (~(1 << 5));
    }
    public static boolean isTransient(int modifier) {
        return (modifier & (1 << 5)) != 0;
    }

    public static int setVolatile(int modifier) {
        return modifier | (1 << 4);
    }
    public static int setNotVolatile(int modifier) {
        return modifier & (~(1 << 4));
    }
    public static boolean isVolatile(int modifier) {
        return (modifier & (1 << 4)) != 0;
    }

    public static int setStatic(int modifier) {
        return modifier | (1 << 3);
    }
    public static int setNotStatic(int modifier) {
        return modifier & (~(1 << 3));
    }
    public static boolean isStatic(int modifier) {
        return (modifier & (1 << 3)) != 0;
    }

    public static int setFinal(int modifier) {
        return modifier | (1 << 2);
    }
    public static int setNotFinal(int modifier) {
        return modifier & (~(1 << 2));
    }
    public static boolean isFinal(int modifier) {
        return (modifier & (1 << 2)) != 0;
    }

    public static int setPublic(int modifier) {
        return (modifier & (~0b0011)) | 0b0011;
    }
    public static int setProtected(int modifier) {
        return (modifier & (~0b0011)) | 0b0010;
    }
    public static int setFriendly(int modifier) {
        return (modifier & (~0b0011)) | 0b0001;
    }
    public static int setPrivate(int modifier) {
        return (modifier & (~0b0011));
    }

    public static int setAccess(int modifier, int value) {
        return (modifier & (~0b0011)) | value;
    }

    public static int getAccess(int modifier) {
        return modifier & 0b11;
    }

    public static final String[] ACCESS_NAME = new String[] { "private", "friendly", "protected", "public" };
    public static String getAccessName(int modifier) {
        int access = getAccess(modifier);
        return ACCESS_NAME[access];
    }
}
