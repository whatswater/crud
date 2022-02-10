package com.whatswater.curd.project.sys.login;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.sys.admin.AdminService;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.permission.UserTokenService;
import com.whatswater.curd.project.sys.uid.UidGeneratorService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class LoginModule implements Module {
    LoginService loginService;
    Router router;
    UidGeneratorService uidGeneratorService;
    EmployeeService employeeService;
    AdminService adminService;
    UserTokenService userTokenService;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_UID, "uidGeneratorService");

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE, "employeeService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ADMIN, "adminService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PERMISSION, "userTokenService");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        MySQLPool pool = null;
        if ("datasource".equals(name)) {
            pool = (MySQLPool) obj;
        } else if ("router".equals(name)) {
            router = (Router) obj;
        } else if ("uidGeneratorService".equals(name)) {
            uidGeneratorService = (UidGeneratorService) obj;
        } else if ("employeeService".equals(name)) {
            employeeService = (EmployeeService) obj;
            if (loginService != null) {
                loginService.setEmployeeService(employeeService);
            }
        } else if ("adminService".equals(name)) {
            adminService = (AdminService) obj;
            if (loginService != null) {
                loginService.setAdminService(adminService);
            }
        } else if ("userTokenService".equals(name)) {
            userTokenService = (UserTokenService) obj;
            if (loginService != null) {
                loginService.setUserTokenService(userTokenService);
            }
        }
        if (pool != null && uidGeneratorService != null && loginService == null) {
            loginService = new LoginService(pool, uidGeneratorService);
            if (employeeService != null) {
                loginService.setEmployeeService(employeeService);
            }
            if (adminService != null) {
                loginService.setAdminService(adminService);
            }
            if (userTokenService != null) {
                loginService.setUserTokenService(userTokenService);
            }
            consumer.exportObject("loginService", loginService);
        }

        if (router != null && loginService != null) {
            LoginRest rest = new LoginRest(loginService);
            RestRouter.register(router, rest);
        }
    }
}
