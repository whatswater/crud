package com.whatswater.curd.project.module.workflow.flowDefinition;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowDefinitionModule implements Module {
    FlowDefinitionService flowDefinitionService;
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

            flowDefinitionService = new FlowDefinitionService(pool);
            consumer.exportObject("flowDefinitionService", flowDefinitionService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && flowDefinitionService != null) {
            FlowDefinitionRest rest = new FlowDefinitionRest(flowDefinitionService);
            RestRouter.register(router, rest);
        }
    }
}
