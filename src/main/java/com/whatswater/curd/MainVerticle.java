package com.whatswater.curd;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleFactory;
import com.whatswater.asyncmodule.ModuleSystem;

import java.util.Map;
import java.util.TreeMap;


public class MainVerticle extends AbstractVerticle {
    private ModuleSystem moduleSystem;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        JsonObject config = config();
        loadModuleSystem(config, startPromise);
    }

    public void loadModuleSystem(JsonObject config, Promise<Void> startPromise) {
        GlobalModule module = new GlobalModule();
        module.setConfig(config);
        module.setStartPromise(startPromise);
        module.setVertx(vertx);

        ModuleSystem moduleSystem = new ModuleSystem(10);
        ModuleFactory moduleFactory = new NewInstanceModuleFactory(new ParentClassLoaderProxy());
        moduleSystem.registerModuleFactory(moduleFactory);

        Map<String, Module> baseModules = new TreeMap<>();
        baseModules.put("init:global", module);
        moduleSystem.loadModule("NewInstance:com.whatswater.curd.project.user.UserModule:0.1", baseModules);
    }
}
