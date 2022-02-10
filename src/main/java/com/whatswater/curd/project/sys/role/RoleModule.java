package com.whatswater.curd.project.sys.role;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.sys.employeeRole.EmployeeRoleService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class RoleModule implements Module {
    RoleService roleService = new RoleService();
    Router router;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE_ROLE, "employeeRoleService");

        moduleInfo.exportObject("roleService", roleService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            roleService.setRoleSQL(new RoleSQL(SQLExecute.createMySQL(pool)));
            consumer.exportObject("roleService", roleService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
            RoleRest rest = new RoleRest(roleService);
            RestRouter.register(router, rest);
        } else if ("employeeRoleService".equals(name)) {
            roleService.setEmployeeRoleService((EmployeeRoleService)obj);
        }
    }
}
