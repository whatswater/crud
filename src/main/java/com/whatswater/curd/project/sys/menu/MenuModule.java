package com.whatswater.curd.project.sys.menu;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class MenuModule implements Module {
    MenuService menuService;
    Router router;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            menuService = new MenuService(pool);
            consumer.exportObject("menuService", menuService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && menuService != null) {
            MenuRest rest = new MenuRest(menuService);
            RestRouter.register(router, rest);
        }
    }
}
