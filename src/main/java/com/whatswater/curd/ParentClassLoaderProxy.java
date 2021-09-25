package com.whatswater.curd;


import com.whatswater.asyncmodule.ClassLoaderProxy;

public class ParentClassLoaderProxy implements ClassLoaderProxy {
    @Override
    public Class<?> loadClass(String className, String classVersion) throws ClassNotFoundException {
        return this.getClass().getClassLoader().loadClass(className);
    }
}
