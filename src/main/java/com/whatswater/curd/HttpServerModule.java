package com.whatswater.curd;


import com.whatswater.curd.project.common.CrudJsonValueReader;
import com.whatswater.curd.project.common.CrudJsonValueWriter;
import com.whatswater.curd.project.common.RestExceptionHandler;
import com.whatswater.curd.project.common.SkipNullBeanProvider;
import com.whatswater.curd.project.sys.permission.PermissionCheckHandler;
import com.zandero.rest.RestBuilder;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.Set;

public class HttpServerModule implements Module {
    private JsonObject config;
    private Promise<Void> startPromise;
    private Vertx vertx;

    private HttpServer httpServer;
    private Router router;
    private PermissionCheckHandler permissionCheckHandler;
    private WebClient workflowWebClient;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require("init:global", "config", "startPromise", "vertx");
        moduleInfo.require("image:81d1aac379310a5566c1439eb44543a9802610de.jpg");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PERMISSION, "permissionCheckHandler");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("config".equals(name)) {
            config = (JsonObject) obj;
        } else if ("startPromise".equals(name)) {
            startPromise = (Promise<Void>) obj;
        } else if ("vertx".equals(name)) {
            vertx = (Vertx) obj;
        } else if ("permissionCheckHandler".equals(name)) {
            permissionCheckHandler = (PermissionCheckHandler) obj;
        }

        if (config != null && startPromise != null && vertx != null && permissionCheckHandler != null) {
            createHttpServer();
            createWebClient();
            String staticEnabled = config.getString("static.enabled");
            if ("1".equals(staticEnabled)) {
                String staticFolder = config.getString("static.folder");
                    router.get("/html/*").handler(StaticHandler
                    .create(staticFolder)
                    .setDirectoryListing(false)
                );
                router.get("/html/*").handler(routingContext -> {
                    routingContext.response().sendFile(staticFolder + "/index.html");
                    routingContext.response().end();
                });
            }

            Set<String> allowedHeaders = new HashSet<>();
            allowedHeaders.add("*");
            router = new RestBuilder(router)
                .bodyHandler(BodyHandler.create(config.getString("upload.tmpFolder")))
                .enableCors("*", true, 1728000, allowedHeaders, HttpMethod.OPTIONS, HttpMethod.GET)
                .register(new PingPongRest())
                .reader(MediaType.APPLICATION_JSON, CrudJsonValueReader.class)
                .writer(MediaType.APPLICATION_JSON, CrudJsonValueWriter.class)
                .errorHandler(RestExceptionHandler.class)
                .routeHandler(permissionCheckHandler)
                .provideWith(SkipNullBeanProvider.class).build();

            consumer.exportObject("httpServer", httpServer);
            consumer.exportObject("router", router);
            consumer.exportObject("workflowWebClient", workflowWebClient);
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

    public void createWebClient() {
        Integer port = config.getInteger("http.port");
        WebClientOptions options = new WebClientOptions()
            .setDefaultHost("127.0.0.1")
            .setDefaultPort(port)
            .setKeepAlive(true)
            .setKeepAliveTimeout(300)
            ;
        this.workflowWebClient = WebClient.create(vertx, options);
    }
}
