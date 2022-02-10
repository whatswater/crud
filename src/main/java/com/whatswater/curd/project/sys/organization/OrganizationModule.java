package com.whatswater.curd.project.sys.organization;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class OrganizationModule implements Module {
    OrganizationService organizationService;
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

            organizationService = new OrganizationService(pool);
            consumer.exportObject("organizationService", organizationService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && organizationService != null) {
            OrganizationRest rest = new OrganizationRest(organizationService);
            RestRouter.register(router, rest);
        }
    }
}
