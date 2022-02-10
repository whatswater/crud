package com.whatswater.curd.project.module.projectReward.projectRewardItem;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.projectReward.projectRewardCategory.ProjectRewardCategoryService;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class ProjectRewardItemModule implements Module {
    ProjectRewardItemService projectRewardItemService = new ProjectRewardItemService();

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PROJECT_REWARD_CATEGORY, "projectRewardCategoryService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ORGANIZATION, "organizationService");

        moduleInfo.exportObject("projectRewardItemService", projectRewardItemService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            projectRewardItemService.setProjectRewardItemSQL(new ProjectRewardItemSQL(SQLExecute.createMySQL(pool)));
        } else if ("router".equals(name)) {
            Router router = (Router) obj;
            ProjectRewardItemRest rest = new ProjectRewardItemRest(projectRewardItemService);
            RestRouter.register(router, rest);
        } else if ("projectRewardCategoryService".equals(name)) {
            projectRewardItemService.setProjectRewardCategoryService((ProjectRewardCategoryService) obj);
        } else if ("organizationService".equals(name)) {
            projectRewardItemService.setOrganizationService((OrganizationService) obj);
        }
    }
}
