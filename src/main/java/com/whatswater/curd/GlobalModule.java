package com.whatswater.curd;


import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;

public class GlobalModule implements Module {
    private JsonObject config;
    private Vertx vertx;
    private Promise<Void> startPromise;

    public GlobalModule() {

    }

    public void setConfig(JsonObject config) {
        this.config = config;
    }

    public void setStartPromise(Promise<Void> startPromise) {
        this.startPromise = startPromise;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void register(ModuleInfo moduleInfo) {

    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        consumer.exportObject("config", config);
        consumer.exportObject("vertx", vertx);
        consumer.exportObject("startPromise", startPromise);
    }
}
