package com.whatswater.asyncmodule;


import java.util.Objects;

public class ModuleSystemModuleFactory implements ModuleFactory {
    public static final String FACTORY_NAME_PREFIX = "ModuleSystem-";

    private String name;
    private ModuleSystem moduleSystem;

    public ModuleSystemModuleFactory(String name, ModuleSystem moduleSystem) {
        this.moduleSystem = moduleSystem;
        this.name = name;
    }

    @Override
    public String getFactoryName() {
        return FACTORY_NAME_PREFIX + name;
    }

    @Override
    public Module createModule(String modulePath) {
        ModuleInfo moduleInfo = moduleSystem.loadModule(modulePath);
        if (Objects.isNull(moduleInfo)) {
            throw new ModuleSystemException("can't find module with modulePath: " + modulePath);
        }
        return moduleInfo.getModuleInstance();
    }
}
