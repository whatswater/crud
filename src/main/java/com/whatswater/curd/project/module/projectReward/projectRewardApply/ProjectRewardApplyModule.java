package com.whatswater.curd.project.module.projectReward.projectRewardApply;


import cn.hutool.core.util.ReflectUtil;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowEngine.FlowEngineService;
import com.whatswater.curd.project.sys.attachment.AttachmentService;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import com.whatswater.curd.project.sys.permission.UserTokenService;
import com.whatswater.curd.project.sys.serial.SerialService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class ProjectRewardApplyModule implements Module {
    ProjectRewardApplyService projectRewardApplyService = new ProjectRewardApplyService();

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ORGANIZATION, "organizationService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PERMISSION, "userTokenService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_ENGINE, "flowEngineService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_SERIAL, "serialService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ATTACHMENT, "attachmentService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE_ROLE, "employeeRoleService");

        moduleInfo.exportObject("projectRewardApplyService", projectRewardApplyService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            projectRewardApplyService.setProjectRewardApplySQL(new ProjectRewardApplySQL(SQLExecute.createMySQL(pool)));
        } else if ("router".equals(name)) {
            Router router = (Router) obj;
            ProjectRewardApplyRest rest = new ProjectRewardApplyRest(projectRewardApplyService);
            RestRouter.register(router, rest);
        } else {
            ReflectUtil.setFieldValue(projectRewardApplyService, name, obj);
        }
    }
}
