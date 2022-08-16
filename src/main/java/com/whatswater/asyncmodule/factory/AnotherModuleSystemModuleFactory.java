package com.whatswater.asyncmodule.factory;


import com.whatswater.asyncmodule.*;
import com.whatswater.asyncmodule.util.ModuleUtil;

public class AnotherModuleSystemModuleFactory implements ModuleFactory {
    public static final String NAME = "anotherModuleSystem";
    private final ModuleSystem moduleSystem;

    public AnotherModuleSystemModuleFactory(ModuleSystem moduleSystem) {
        this.moduleSystem = moduleSystem;
    }

    @Override
    public String getFactoryName() {
        return NAME;
    }

    @Override
    public Module createModule(String modulePath) throws Exception {
        String url = ModuleUtil.getModulePathParam(modulePath);
        ModuleInfo moduleInfo = moduleSystem.loadModule(url);
        if (moduleInfo == null) {
            throw new ModuleSystemException("can't found module:" + modulePath);
        }
        return moduleInfo.getModuleInstance();
    }
}
