package com.whatswater.curd.project.sys.permission;


import cn.hutool.core.util.ReflectUtil;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.sys.employee.EmployeeSQL;
import com.whatswater.curd.project.sys.employeeFilter.EmployeeFilterDataService;
import com.whatswater.curd.project.sys.menu.MenuService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class PermissionModule implements Module {
    UserTokenService userTokenService = new UserTokenService();
    UserFetcher userFetcher = new UserFetcher();
    AuthService authService = new AuthService();

    @Override
    public void register(ModuleInfo moduleInfo) {
        authService.setUserFetcher(userFetcher);
        PermissionCheckHandler permissionCheckHandler = new PermissionCheckHandler(userTokenService);
        permissionCheckHandler.setAuthService(authService);

        moduleInfo.exportObject("permissionCheckHandler", permissionCheckHandler);
        moduleInfo.exportObject("userFetcher", userFetcher);
        moduleInfo.exportObject("userTokenService", userTokenService);
        moduleInfo.exportObject("authService", authService);

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_MENU, "menuService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE_FILTER, "employeeFilterDataService");

        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE, "employeeService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ROLE, "roleService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE_ROLE, "employeeRoleService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_ORGANIZATION, "organizationService");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
        } else if ("menuService".equals(name)) {
            MenuService menuService = (MenuService) obj;
            userTokenService.setMenuService(menuService);
        } else if ("router".equals(name)) {
            UserTokenRest userTokenRest = new UserTokenRest(userTokenService);
            RestRouter.register((Router) obj, userTokenRest);
        } else if ("employeeFilterDataService".equals(name)) {
            authService.setEmployeeFilterDataService((EmployeeFilterDataService) obj);
        } else if ("employeeService".equals(name)
            || "roleService".equals(name)
            || "employeeRoleService".equals(name)
            || "organizationService".equals(name)) {
            ReflectUtil.setFieldValue(userFetcher, name, obj);
        }
    }
}
