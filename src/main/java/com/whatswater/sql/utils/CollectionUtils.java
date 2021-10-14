package com.whatswater.sql.utils;


import java.util.Collection;

public class CollectionUtils {

    public boolean isNotEmpty(Collection<?> collection) {
        return collection != null && (!collection.isEmpty());
    }
}
