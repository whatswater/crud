package com.whatswater.sql.utils;


import java.util.Collection;

public class CollectionUtils {
    private CollectionUtils() {

    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && (!collection.isEmpty());
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
