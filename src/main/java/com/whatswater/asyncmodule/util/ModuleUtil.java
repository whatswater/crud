package com.whatswater.asyncmodule.util;


public abstract class ModuleUtil {
    public static String getModulePathParam(String modulePath) {
        return modulePath.split(":")[1];
    }
}
