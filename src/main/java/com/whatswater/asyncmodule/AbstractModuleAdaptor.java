package com.whatswater.asyncmodule;


import com.whatswater.asyncmodule.util.Key;

import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractModuleAdaptor implements Module {
    private ModuleInfo moduleInfo;
    private final Map<Key<?>, Object> resolvedMap = new TreeMap<>();

    @Override
    public void register(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
        this.init();
    }
    public abstract void init();
    public void require(Key<?> ...keys) {
        for (Key<?> key : keys) {
            moduleInfo.require(key.getModulePath(), key.getName());
        }
    }
    public void require(String modulePath) {
        moduleInfo.require(modulePath, ModuleSystem.DEFAULT_NAME);
    }

    @SuppressWarnings("unchecked")
    public <T> T getResolved(Key<T> key) {
        return (T)resolvedMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getResolved(String modulePath) {
        Key<T> key = new Key<>(modulePath, ModuleSystem.DEFAULT_NAME);
        return (T)resolvedMap.get(key);
    }

    public boolean isAllResolved(Key<?> ...keys) {
        for (Key<?> key : keys) {
            if (!resolvedMap.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    public void export(String name, Object obj) {
        moduleInfo.exportObject(name, obj);
    }

    public void export(Object obj) {
        moduleInfo.exportObject(obj);
    }

    public void export() {
        moduleInfo.exportObject(ModuleSystem.DEFAULT_OBJ);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        Key<?> key = new Key<>(provider.getModulePath(), name);
        resolvedMap.put(key, obj);
        resolve();
    }
    public abstract void resolve();
}
