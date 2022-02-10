package com.whatswater.curd.project.module.workflow.flowEngine;


import cn.hutool.core.util.ReflectUtil;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;

import java.util.concurrent.atomic.AtomicInteger;

public class FlowEngineModule implements Module {
    Router router;
    FlowEngineService flowEngineService = new FlowEngineService();

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_DEFINITION, "flowDefinitionService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_CONSTANT, "flowConstantService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_INSTANCE, "flowInstanceService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_INSTANCE_VARIABLE, "flowInstanceVariableService");

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_LINK, "flowLinkService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_LINK_CONSTANT, "flowLinkConstantService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_LINK_RELATION, "flowLinkRelationService");

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_INSTANCE_TASK, "flowInstanceTaskService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_INSTANCE_TASK_RELATION, "flowInstanceTaskRelationService");


        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "workflowWebClient");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_ASSIGNEE, "assigneeConfigService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_INSTANCE_LINK_ACTOR, "flowInstanceLinkActorService");

        moduleInfo.exportObject("flowEngineService", flowEngineService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("router".equals(name)) {
            router = (Router) obj;
            FlowEngineRest rest = new FlowEngineRest(flowEngineService);
            RestRouter.register(router, rest);
            return;
        }
        ReflectUtil.setFieldValue(flowEngineService, name, obj);
    }
}
