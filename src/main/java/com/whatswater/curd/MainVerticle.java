package com.whatswater.curd;

import com.whatswater.asyncmodule.factory.ImageResourceModuleFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleFactory;
import com.whatswater.asyncmodule.ModuleSystem;

import java.util.Map;
import java.util.TreeMap;

// 目前只是单Verticle运行，多Verticle运行需考虑vertx、router、startPromise的共享问题（GlobalModule可能不太适用了，并且router需要注册多次？）
// 需要有动态的直接添加module对象的能力
public class MainVerticle extends AbstractVerticle {
    private static ModuleSystem moduleSystem;

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

        ModuleSystem moduleSystem = new ModuleSystem(9);
        ModuleFactory moduleFactory = new NewInstanceModuleFactory(new ParentClassLoaderProxy());
        ImageResourceModuleFactory imageModuleFactory = new ImageResourceModuleFactory("D:\\");
        moduleSystem.registerModuleFactory(moduleFactory);
        moduleSystem.registerModuleFactory(imageModuleFactory);

        Map<String, Module> baseModules = new TreeMap<>();
        baseModules.put("init:global", module);
        moduleSystem.loadModule("NewInstance:com.whatswater.curd.InitModule:0.1", baseModules);
    }
}
