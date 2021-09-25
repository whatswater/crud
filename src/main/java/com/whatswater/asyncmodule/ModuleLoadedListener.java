package com.whatswater.asyncmodule;

public interface ModuleLoadedListener {
    void onModuleLoaded(ModuleInfo moduleInfo, ModuleSystem factory);
    void onAllModuleLoaded(ModuleSystem factory);
}
