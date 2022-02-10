package com.whatswater.curd.project.module.workflow.flowInstanceTask;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTaskService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceTaskModule implements Module {

    FlowInstanceTaskService flowInstanceTaskService;
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
            flowInstanceTaskService = new FlowInstanceTaskService(pool);
            consumer.exportObject("flowInstanceTaskService", flowInstanceTaskService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && flowInstanceTaskService != null) {
            FlowInstanceTaskRest rest = new FlowInstanceTaskRest(flowInstanceTaskService);
            RestRouter.register(router, rest);
        }
    }
}
