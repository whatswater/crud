package com.whatswater.curd.shell;

import com.whatswater.asyncmodule.AbstractModuleAdaptor;
import com.whatswater.asyncmodule.util.Key;
import io.vertx.core.Vertx;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.term.HttpTermOptions;
import io.vertx.ext.web.Router;


public class ShellModule extends AbstractModuleAdaptor {
    public static final Key<Router> routerKey = new Key<>("NewInstance:com.whatswater.curd.HttpServerModule:0.1", "router");
    public static final Key<Vertx> vertxKey = new Key<>("init:global", "vertx");

    @Override
    public void init() {
        require(routerKey, vertxKey);
    }

    @Override
    public void resolve() {
        if (isAllResolved(routerKey, vertxKey)) {
            Router router = getResolved(routerKey);
            Vertx vertx = getResolved(vertxKey);

            createShellService(vertx, router);
            export();
        }
    }

    public void createShellService(Vertx vertx, Router router) {
        ShellService service = ShellService.create(vertx,
            new ShellServiceOptions().setHttpOptions(
                new HttpTermOptions().
                    setHost("localhost").
                    setPort(5000)
            )
        );
        service.start();
    }
}
