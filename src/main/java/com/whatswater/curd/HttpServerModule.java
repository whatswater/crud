package com.whatswater.curd;


import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;

public class HttpServerModule implements Module {
    private JsonObject config;
    private Promise<Void> startPromise;
    private Vertx vertx;

    private HttpServer httpServer;
    private Router router;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require("init:global", "config", "startPromise", "vertx");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("config".equals(name)) {
            config = (JsonObject) obj;
        } else if ("startPromise".equals(name)) {
            startPromise = (Promise<Void>) obj;
        } else if ("vertx".equals(name)) {
            vertx = (Vertx) obj;
        }
        if (config != null && startPromise != null && vertx != null) {
            createHttpServer();
            consumer.exportObject("httpServer", httpServer);
            consumer.exportObject("router", router);
        }
    }

    public void createHttpServer() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        String host = config.getString("http.host");
        Integer port = config.getInteger("http.port");

        server.requestHandler(router).listen(port, host).onComplete(result -> {
            if(result.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(result.cause());
            }
        });

        this.router = router;
        this.httpServer = server;
    }
}
