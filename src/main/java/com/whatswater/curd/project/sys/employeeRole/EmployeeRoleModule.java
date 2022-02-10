package com.whatswater.curd.project.sys.employeeRole;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.role.RoleService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class EmployeeRoleModule implements Module {
    EmployeeRoleService employeeRoleService = new EmployeeRoleService();
    Router router;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE, "employeeService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ROLE, "roleService");

        moduleInfo.exportObject("employeeRoleService", employeeRoleService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            employeeRoleService.setEmployeeRoleSQL(new EmployeeRoleSQL(SQLExecute.createMySQL(pool)));
        } else if ("router".equals(name)) {
            router = (Router) obj;
            EmployeeRoleRest rest = new EmployeeRoleRest(employeeRoleService);
            RestRouter.register(router, rest);
        } else if ("roleService".equals(name)) {
            employeeRoleService.setRoleService((RoleService) obj);
        } else if ("employeeService".equals(name)) {
            employeeRoleService.setEmployeeService((EmployeeService) obj);
        }
    }
}
