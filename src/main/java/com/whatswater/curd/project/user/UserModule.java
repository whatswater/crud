package com.whatswater.curd.project.user;


import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;

public class UserModule implements Module {
    UserRepository userRepository;
    UserService userService;
    Router router;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require("NewInstance:com.whatswater.curd.datasource.DataSourceModule:0.1", "datasource");
        moduleInfo.require("NewInstance:com.whatswater.curd.HttpServerModule:0.1", "router");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        MySQLPool pool = null;
        if ("datasource".equals(name)) {
            pool = (MySQLPool) obj;
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }
        if (pool != null) {
            userRepository = new UserRepository(pool);
            userService = new UserService(userRepository);
            consumer.exportObject(userService);
        }
        if (router != null && userService != null) {
            UserRest rest = new UserRest(userService);
            RestRouter.register(router, rest);
        }
    }
}
