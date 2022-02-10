package com.whatswater.curd.project.module.workflow.flowLinkRelation;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkService;
import com.whatswater.curd.project.module.workflow.flowLinkRelation.FlowLinkRelationService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowLinkRelationModule implements Module {
    FlowLinkService flowLinkService;
    FlowLinkRelationService flowLinkRelationService;
    Router router;
    FlowLinkRelationRest rest;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_LINK, "flowLinkService");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            flowLinkRelationService = new FlowLinkRelationService(pool);
            if (flowLinkService != null) {
                flowLinkRelationService.setFlowLinkService(flowLinkService);
            }
            consumer.exportObject("flowLinkRelationService", flowLinkRelationService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        } else if ("flowLinkService".equals(name)) {
            flowLinkService = (FlowLinkService) obj;
            if (flowLinkRelationService != null) {
                flowLinkRelationService.setFlowLinkService(flowLinkService);
            }
        }

        if (router != null && flowLinkRelationService != null && rest == null) {
            rest = new FlowLinkRelationRest(flowLinkRelationService);
            RestRouter.register(router, rest);
        }
    }
}
