package com.whatswater.asyncmodule;

public interface ClassLoaderProxy {
    Class<?> loadClass(String className, String classVersion) throws ClassNotFoundException;
}
