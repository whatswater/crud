package com.whatswater.curd.project.module.workflow.flowInstanceVariable;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowInstanceVariable.FlowInstanceVariableService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceVariableModule implements Module {

    FlowInstanceVariableService flowInstanceVariableService;
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

            flowInstanceVariableService = new FlowInstanceVariableService(pool);
            consumer.exportObject("flowInstanceVariableService", flowInstanceVariableService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && flowInstanceVariableService != null) {
            FlowInstanceVariableRest rest = new FlowInstanceVariableRest(flowInstanceVariableService);
            RestRouter.register(router, rest);
        }
    }
}
