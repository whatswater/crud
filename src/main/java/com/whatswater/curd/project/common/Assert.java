package com.whatswater.curd.project.common;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Collection;

public abstract class Assert {
    public static void assertNotEmpty(Collection<?> valueList, String msg) {
        if (CollectionUtil.isEmpty(valueList)) {
            throw ErrorCodeEnum.PARAM_NO_VALID.toException(msg);
        }
    }

    public static void assertNotEmpty(String value, String msg) {
        if (StrUtil.isEmpty(value)) {
            throw ErrorCodeEnum.PARAM_NO_VALID.toException(msg);
        }
    }

    public static void assertNotNull(Object obj, String msg) {
        if (obj == null) {
            throw ErrorCodeEnum.PARAM_NO_VALID.toException(msg);
        }
    }

    public static void assertTrue(boolean value, String msg) {
        if (value) {
            throw ErrorCodeEnum.PARAM_NO_VALID.toException(msg);
        }
    }
}
