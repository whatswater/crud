package com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation.FlowInstanceTaskRelationService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceTaskRelationModule implements Module {

    FlowInstanceTaskRelationService flowInstanceTaskRelationService;
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

            flowInstanceTaskRelationService = new FlowInstanceTaskRelationService(pool);
            consumer.exportObject("flowInstanceTaskRelationService", flowInstanceTaskRelationService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && flowInstanceTaskRelationService != null) {
            FlowInstanceTaskRelationRest rest = new FlowInstanceTaskRelationRest(flowInstanceTaskRelationService);
            RestRouter.register(router, rest);
        }
    }
}
