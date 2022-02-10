package com.whatswater.curd.project.module.workflow.flowLinkConstant;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstantService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowLinkConstantModule implements Module {

    FlowLinkConstantService flowLinkConstantService;
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
            flowLinkConstantService = new FlowLinkConstantService(pool);
            consumer.exportObject("flowLinkConstantService", flowLinkConstantService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && flowLinkConstantService != null) {
            FlowLinkConstantRest rest = new FlowLinkConstantRest(flowLinkConstantService);
            RestRouter.register(router, rest);
        }
    }
}
