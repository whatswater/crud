package com.whatswater.curd.project.module.workflow.flowInstance;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceModule implements Module {
    FlowInstanceService flowInstanceService;
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
            flowInstanceService = new FlowInstanceService(pool);
            consumer.exportObject("flowInstanceService", flowInstanceService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && flowInstanceService != null) {
            FlowInstanceRest rest = new FlowInstanceRest(flowInstanceService);
            RestRouter.register(router, rest);
        }
    }
}
