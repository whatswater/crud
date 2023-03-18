package com.whatswater.curd.shell;

import com.whatswater.asyncmodule.AbstractModuleAdaptor;
import com.whatswater.asyncmodule.util.Key;
import io.vertx.core.Vertx;
import io.vertx.ext.shell.ShellServer;
import io.vertx.ext.shell.command.CommandResolver;
import io.vertx.ext.shell.term.TermServer;
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
        ModuleCommandPack moduleCommandPack = new ModuleCommandPack();
        moduleCommandPack.resolver(vertx, result -> {
            ShellServer shellServer = ShellServer.create(vertx);
            Router shellRouter = Router.router(vertx);
            router.route("/shell/*").subRouter(shellRouter);
            TermServer httpTermServer = TermServer.createHttpTermServer(vertx, shellRouter);
            shellServer.registerTermServer(httpTermServer);
            shellServer.registerCommandResolver(CommandResolver.baseCommands(vertx));
            shellServer.registerCommandResolver(result.result());
            shellServer.listen();
        });
    }
}
