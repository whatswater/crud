package com.whatswater.curd;


import com.whatswater.asyncmodule.*;

public class NewInstanceModuleFactory implements ModuleFactory {
    final ClassLoaderProxy classLoaderProxy;

    public NewInstanceModuleFactory(ClassLoaderProxy classLoaderProxy) {
        this.classLoaderProxy = classLoaderProxy;
    }

    @Override
    public String getFactoryName() {
        return "NewInstance";
    }

    @Override
    public Module createModule(String modulePath) throws Exception {
        String[] info = modulePath.split(ModuleSystem.MODULE_PATH_SPLIT);
        String className = info[1];
        String version = info[2];
        Class<?> cls = classLoaderProxy.loadClass(className, version);
        if(!Module.class.isAssignableFrom(cls)) {
            throw new ModuleSystemException("ModuleFactory NewInstance: The class should implements Module Interface, className: " + className + ", version: " + version);
        }

        Class<? extends Module> moduleClass = (Class<? extends Module>) cls;
        return moduleClass.getConstructor().newInstance();
    }

    public static final String MODULE_PATH_MENU = "NewInstance:com.whatswater.curd.project.sys.menu.MenuModule:0.1";
    public static final String MODULE_PATH_TODO = "NewInstance:com.whatswater.curd.project.module.todo.TodoModule:0.1";
    public static final String MODULE_PATH_OPINION = "NewInstance:com.whatswater.curd.project.module.opinion.OpinionModule:0.1";
    public static final String MODULE_PATH_ATTACHMENT = "NewInstance:com.whatswater.curd.project.sys.attachment.AttachmentModule:0.1";
    public static final String MODULE_PATH_SERIAL = "NewInstance:com.whatswater.curd.project.sys.serial.SerialModule:0.1";


    public static final String MODULE_PATH_HTTP_SERVER = "NewInstance:com.whatswater.curd.HttpServerModule:0.1";
    public static final String MODULE_PATH_DATA_SOURCE = "NewInstance:com.whatswater.curd.datasource.DataSourceModule:0.1";
    public static final String MODULE_PATH_UID = "NewInstance:com.whatswater.curd.project.sys.uid.UidModule:0.1";
    public static final String MODULE_PATH_EMPLOYEE = "NewInstance:com.whatswater.curd.project.sys.employee.EmployeeModule:0.1";
    public static final String MODULE_PATH_ADMIN = "NewInstance:com.whatswater.curd.project.sys.admin.AdminModule:0.1";
    public static final String MODULE_PATH_LOGIN = "NewInstance:com.whatswater.curd.project.sys.login.LoginModule:0.1";
    public static final String MODULE_PATH_ORGANIZATION = "NewInstance:com.whatswater.curd.project.sys.organization.OrganizationModule:0.1";
    public static final String MODULE_PATH_PERMISSION = "NewInstance:com.whatswater.curd.project.sys.permission.PermissionModule:0.1";
    public static final String MODULE_PATH_EMPLOYEE_FILTER = "NewInstance:com.whatswater.curd.project.sys.employeeFilter.EmployeeFilterModule:0.1";

    public static final String MODULE_PATH_ROLE = "NewInstance:com.whatswater.curd.project.sys.role.RoleModule:0.1";
    public static final String MODULE_PATH_EMPLOYEE_ROLE = "NewInstance:com.whatswater.curd.project.sys.employeeRole.EmployeeRoleModule:0.1";

    public static final String MODULE_PATH_ANNUAL_TASK = "NewInstance:com.whatswater.curd.project.module.annualTask.AnnualTaskModule:0.1";

    public static final String MODULE_PATH_PROJECT_REWARD_CATEGORY = "NewInstance:com.whatswater.curd.project.module.projectReward.projectRewardCategory.ProjectRewardCategoryModule:0.1";
    public static final String MODULE_PATH_PROJECT_REWARD_ITEM = "NewInstance:com.whatswater.curd.project.module.projectReward.projectRewardItem.ProjectRewardItemModule:0.1";
    public static final String MODULE_PATH_PROJECT_REWARD_APPLY = "NewInstance:com.whatswater.curd.project.module.projectReward.projectRewardApply.ProjectRewardApplyModule:0.1";

    public static final String MODULE_PATH_FLOW_DEFINITION = "NewInstance:com.whatswater.curd.project.module.workflow.flowDefinition.FlowDefinitionModule:0.1";
    public static final String MODULE_PATH_FLOW_CONSTANT = "NewInstance:com.whatswater.curd.project.module.workflow.flowConstant.FlowConstantModule:0.1";
    public static final String MODULE_PATH_FLOW_LINK = "NewInstance:com.whatswater.curd.project.module.workflow.flowLink.FlowLinkModule:0.1";
    public static final String MODULE_PATH_FLOW_LINK_RELATION = "NewInstance:com.whatswater.curd.project.module.workflow.flowLinkRelation.FlowLinkRelationModule:0.1";
    public static final String MODULE_PATH_FLOW_LINK_CONSTANT = "NewInstance:com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstantModule:0.1";
    public static final String MODULE_PATH_FLOW_INSTANCE = "NewInstance:com.whatswater.curd.project.module.workflow.flowInstance.FlowInstanceModule:0.1";
    public static final String MODULE_PATH_FLOW_INSTANCE_VARIABLE = "NewInstance:com.whatswater.curd.project.module.workflow.flowInstanceVariable.FlowInstanceVariableModule:0.1";
    public static final String MODULE_PATH_FLOW_INSTANCE_LINK_ACTOR = "NewInstance:com.whatswater.curd.project.module.workflow.flowInstanceLinkActor.FlowInstanceLinkActorModule:0.1";
    public static final String MODULE_PATH_FLOW_INSTANCE_TASK = "NewInstance:com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTaskModule:0.1";
    public static final String MODULE_PATH_FLOW_INSTANCE_TASK_RELATION = "NewInstance:com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation.FlowInstanceTaskRelationModule:0.1";
    public static final String MODULE_PATH_FLOW_ASSIGNEE = "NewInstance:com.whatswater.curd.project.module.workflow.assignee.AssigneeModule:0.1";
    public static final String MODULE_PATH_FLOW_ENGINE = "NewInstance:com.whatswater.curd.project.module.workflow.flowEngine.FlowEngineModule:0.1";

}
