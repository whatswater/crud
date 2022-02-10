package com.whatswater.curd.project.sys.permission;


import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employeeFilter.EmployeeFilterDataService;
import com.whatswater.curd.project.sys.employeeFilter.SExpressionUtil;
import com.whatswater.curd.project.sys.menu.MenuService;
import io.vertx.core.Future;

import java.util.*;


public class AuthService {
    MenuService menuService;
    EmployeeFilterDataService employeeFilterDataService;
    UserFetcher userFetcher;

    public Future<Boolean> authPermission(String url) {
//        return menuService.getByUrl(url).compose(menu -> {
//            String code = menu.getPermission();
//            return employeeFilterDataService.queryByCode(code).map(SExpressionUtil::parse);
//        }).compose(sExpression -> {
//            return Future.succeededFuture(true);
//        });
        return Future.succeededFuture(true);
    }

    public Future<List<Employee>> queryEmployeeByFilterCode(UserFetcherContext userFetcherContext, String code) {
        return employeeFilterDataService.queryByCode(code).map(SExpressionUtil::parse).compose(sExpression -> {
            return userFetcher.executeSExpression(sExpression, userFetcherContext);
        });
    }

    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    public void setEmployeeFilterDataService(EmployeeFilterDataService employeeFilterDataService) {
        this.employeeFilterDataService = employeeFilterDataService;
    }

    public UserFetcher getUserFetcher() {
        return userFetcher;
    }

    public void setUserFetcher(UserFetcher userFetcher) {
        this.userFetcher = userFetcher;
    }
}
