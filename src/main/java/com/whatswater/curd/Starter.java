package com.whatswater.curd;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleFactory;
import com.whatswater.asyncmodule.ModuleSystem;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.TreeMap;

public class Starter {
    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions();
        JsonObject config = new JsonObject();

        config.put("http.port", 8080);
        config.put("http.host", "0.0.0.0");
        config.put("datasource.pool.maxSize", 10);
        config.put("datasource.pool.idleTimeout", 300);
        config.put("datasource.connection.port", 3306);
        config.put("datasource.connection.host", "localhost");
        config.put("datasource.connection.database", "crud");
        config.put("datasource.connection.user", "root");
        config.put("datasource.connection.password", "0000000qe");
        config.put("datasource.connection.encode", "utf8mb4");
        config.put("datasource.connection.collation", "utf8mb4_general_ci");
        config.put("datasource.connection.serverTimeZone", "Asia/Shanghai");
        config.put("static.folder", "D:/code/javascript/react-admin-starter/dist");
        config.put("static.enabled", "0");
        config.put("upload.tmpFolder", "/curd_tmp");
        config.put("upload.fileFolder", "/curd_file");

        options.setConfig(config);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), options);
    }

    public static ModuleSystem loadModuleSystem(Vertx vertx, JsonObject config) {
        GlobalModule module = new GlobalModule();
        module.setConfig(config);
        module.setVertx(vertx);

        ModuleSystem moduleSystem = new ModuleSystem(9);
        ModuleFactory moduleFactory = new NewInstanceModuleFactory(new ParentClassLoaderProxy());
        boolean success = moduleSystem.registerModuleFactory(moduleFactory);
        if (!success) {
            System.out.println("....");
        }

        Map<String, Module> baseModules = new TreeMap<>();
        baseModules.put("init:global", module);
        moduleSystem.loadInitModule(baseModules);

        return moduleSystem;
    }
}
