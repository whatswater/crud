package com.whatswater.curd.project.module.workflow.assignee;


import cn.hutool.core.util.ReflectUtil;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowInstance.FlowInstanceService;
import com.whatswater.curd.project.sys.permission.AuthService;
import io.vertx.ext.web.client.WebClient;

public class AssigneeModule implements Module {
    private AssigneeConfigService assigneeConfigService = new AssigneeConfigService();

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_INSTANCE, "flowInstanceService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "workflowWebClient");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PERMISSION, "authService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ORGANIZATION, "organizationService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE, "employeeService");

        moduleInfo.exportObject("assigneeConfigService", assigneeConfigService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        ReflectUtil.setFieldValue(assigneeConfigService, name, obj);
    }
}
