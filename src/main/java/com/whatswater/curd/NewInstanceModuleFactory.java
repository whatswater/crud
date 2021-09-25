package com.whatswater.curd;


import com.whatswater.asyncmodule.*;

public class NewInstanceModuleFactory implements ModuleFactory {
    final ClassLoaderProxy classLoaderProxy;

    public NewInstanceModuleFactory(ClassLoaderProxy classLoaderProxy) {
        this.classLoaderProxy = classLoaderProxy;
    }

    @Override
    public String getFactoryName() {
        return "NewInstance";
    }

    @Override
    public Module createModule(String modulePath) throws Exception {
        String[] info = modulePath.split(ModuleSystem.MODULE_PATH_SPLIT);
        String className = info[1];
        String version = info[2];
        Class<?> cls = classLoaderProxy.loadClass(className, version);
        if(!Module.class.isAssignableFrom(cls)) {
            throw new ModuleSystemException("ModuleFactory NewInstance: The class should implements Module Interface, className: " + className + ", version: " + version);
        }

        Class<? extends Module> moduleClass = (Class<? extends Module>) cls;
        return moduleClass.getConstructor().newInstance();
    }
}
