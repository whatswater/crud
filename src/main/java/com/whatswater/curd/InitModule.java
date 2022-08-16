package com.whatswater.curd;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;


public class InitModule implements Module {
    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_TODO, "todoService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_LOGIN, "loginService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_MENU, "menuService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ORGANIZATION, "organizationService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PERMISSION, "userTokenService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ROLE, "roleService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE_ROLE, "employeeRoleService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ATTACHMENT, "attachmentService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_OPINION, "opinionService");

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ANNUAL_TASK, "annualTaskService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_ENGINE, "flowEngineService");

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PROJECT_REWARD_CATEGORY, "projectRewardCategoryService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PROJECT_REWARD_ITEM, "projectRewardItemService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PROJECT_REWARD_APPLY, "projectRewardApplyService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_NAMESPACE, "namespaceSchemaDataService");

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_SHELL);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {

    }
}
