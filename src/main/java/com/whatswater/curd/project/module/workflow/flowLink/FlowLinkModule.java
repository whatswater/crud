package com.whatswater.curd.project.module.workflow.flowLink;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkService;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstantService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowLinkModule implements Module {
    FlowLinkConstantService flowLinkConstantService;
    FlowLinkService flowLinkService;
    Router router;
    FlowLinkRest rest;


    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_LINK_CONSTANT, "flowLinkConstantService");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            flowLinkService = new FlowLinkService(pool);
            if (flowLinkConstantService != null) {
                flowLinkService.setFlowLinkConstantService(flowLinkConstantService);
            }
            consumer.exportObject("flowLinkService", flowLinkService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        } else if ("flowLinkConstantService".equals(name)) {
            flowLinkConstantService = (FlowLinkConstantService) obj;
            if (flowLinkService != null) {
                flowLinkService.setFlowLinkConstantService(flowLinkConstantService);
            }
        }

        if (router != null && flowLinkService != null && rest == null) {
            FlowLinkRest rest = new FlowLinkRest(flowLinkService);
            RestRouter.register(router, rest);
        }
    }
}
