package com.whatswater.curd.project.sys.employee;


import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;

public class EmployeeModule implements Module {
    OrganizationService organizationService;
    EmployeeService employeeService;
    Router router;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require("NewInstance:com.whatswater.curd.datasource.DataSourceModule:0.1", "datasource");
        moduleInfo.require("NewInstance:com.whatswater.curd.HttpServerModule:0.1", "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ORGANIZATION, "organizationService");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;

            employeeService = new EmployeeService(pool);
            consumer.exportObject("employeeService", employeeService);
            if (router != null) {
                EmployeeRest rest = new EmployeeRest(employeeService);
                RestRouter.register(router, rest);
            }
            if (organizationService != null) {
                employeeService.setOrganizationService(organizationService);
            }
        } else if ("router".equals(name)) {
            router = (Router) obj;
            if (employeeService != null) {
                EmployeeRest rest = new EmployeeRest(employeeService);
                RestRouter.register(router, rest);
            }
        } else if ("organizationService".equals(name)) {
            organizationService = (OrganizationService) obj;
            if (employeeService != null) {
                employeeService.setOrganizationService(organizationService);
            }
        }

    }
}
