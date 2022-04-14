package com.whatswater.asyncmodule;

import com.whatswater.asyncmodule.ModuleEvent.RequireResolvedEvent;
import com.whatswater.asyncmodule.executor.ModuleEventExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class ModuleSystem {
    public static final String DEFAULT_NAME = "default";
    public static final String MODULE_PATH_SPLIT = ":";
    public static final String MODULE_PATH_GLOBAL = "module-system:global";
    public static final String EMPTY = "";

    private final Map<String, ModuleFactory> moduleFactoryMap = new ConcurrentHashMap<>();
    private final Map<String, ModuleInfo> moduleInfoMap = new ConcurrentHashMap<>();
    private final AtomicLong moduleIdCounter = new AtomicLong(0L);
    private ModuleInfo globalModuleInfo;

    private final ModuleEventExecutor executor;

    public ModuleSystem(int executorSize) {
        executor = new ModuleEventExecutor(executorSize, Executors.defaultThreadFactory());
    }

    public ModuleEventExecutor getExecutor() {
        return executor;
    }

    public void submitModuleEvent(List<ModuleEvent> eventList) {
        if (eventList.isEmpty()) {
            return;
        }
        Map<ModuleInfo, List<ModuleEvent>> group = new TreeMap<>();
        for (ModuleEvent event: eventList) {
            List<ModuleEvent> tmp = group.computeIfAbsent(event.getConsumer(), k -> new ArrayList<>());
            tmp.add(event);
        }
        for (Map.Entry<ModuleInfo, List<ModuleEvent>> entry: group.entrySet()) {
            executor.submitTask(entry.getKey(), entry.getValue());
        }
    }

    public void submitModuleEvent(ModuleInfo consumer, List<ModuleEvent> eventList) {
        if (eventList.isEmpty()) {
            return;
        }
        executor.submitTask(consumer, eventList);
    }

    public void loadModule(String modulePath) {
        getOrCreateModuleInfo(modulePath);
    }

    public void loadModule(String modulePath, Map<String, Module> baseModules) {
        this.globalModuleInfo = new ModuleInfo(MODULE_PATH_GLOBAL, new Module() {
            @Override
            public void register(ModuleInfo moduleInfo) {

            }

            @Override
            public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {

            }
        }, this);

        for (Map.Entry<String, Module> entry: baseModules.entrySet()) {
            String path = entry.getKey();
            Module instance = entry.getValue();

            ModuleInfo moduleInfo = new ModuleInfo(path, instance, this);
            moduleInfoMap.put(path, moduleInfo);
            submitModuleEvent(moduleInfo, Collections.singletonList(new RequireResolvedEvent(moduleInfo, this.globalModuleInfo, "load", EMPTY)));
        }
        getOrCreateModuleInfo(modulePath);
    }


    /**
     * 根据模块路径和参数，获取或者创建模块信息
     * @param modulePath 模块路径
     * @return 模块信息实例
     */
    ModuleInfo getOrCreateModuleInfo(String modulePath) {
        ModuleInfoCreator create = new ModuleInfoCreator(this);
        ModuleInfo moduleInfo = moduleInfoMap.computeIfAbsent(modulePath, create);
        create.executeEvent();
        return moduleInfo;
    }

    void removeModuleInfo(String modulePath) {
        moduleInfoMap.remove(modulePath);
    }

    /**
     * 根据模块路径获取模块信息
     * @param modulePath 模块路径
     * @return 模块信息实例
     */
    public ModuleInfo getModuleInfo(String modulePath) {
        return moduleInfoMap.get(modulePath);
    }

    /**
     * 注册模块工厂，如果之前注册过同名的，那么将不会注册，并且返回false
     * @param moduleFactory 模块工厂
     * @return 是否实际注册了
     */
    public boolean registerModuleFactory(ModuleFactory moduleFactory) {
        ModuleFactory put = moduleFactoryMap.putIfAbsent(moduleFactory.getFactoryName(), moduleFactory);
        return put == moduleFactory;
    }

    long nextModuleId() {
        return moduleIdCounter.incrementAndGet();
    }

    private static class ModuleInfoCreator implements Function<String, ModuleInfo> {
        private final ModuleSystem moduleSystem;
        private ModuleInfo moduleInfo;

        ModuleInfoCreator(ModuleSystem moduleSystem) {
            this.moduleSystem = moduleSystem;
        }

        @Override
        public ModuleInfo apply(String path) {
            String factoryName = path.split(MODULE_PATH_SPLIT)[0];
            ModuleFactory factory = moduleSystem.moduleFactoryMap.get(factoryName);
            if(factory == null) {
                throw new ModuleSystemException("can't find module factory when instance module: " + path);
            }

            try {
                Module module = factory.createModule(path);
                moduleInfo = new ModuleInfo(path, module, moduleSystem);
                return moduleInfo;
            }
            catch(Exception e) {
                e.printStackTrace();
                throw new ModuleSystemException("can't find module factory when instance module: " + path, e);
            }
        }

        public void executeEvent() {
            if(moduleInfo != null) {
                moduleInfo.getModuleInstance().register(moduleInfo);
            }
        }
    }
}
