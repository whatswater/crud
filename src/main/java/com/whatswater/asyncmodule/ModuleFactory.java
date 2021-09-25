package com.whatswater.asyncmodule;

public interface ModuleFactory {
    String getFactoryName();
    Module createModule(String modulePath) throws Exception;
}
