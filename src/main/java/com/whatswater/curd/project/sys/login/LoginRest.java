package com.whatswater.curd.project.sys.login;


import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.sys.admin.Admin;
import com.whatswater.curd.project.sys.admin.AdminService;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.permission.UserToken;
import io.vertx.core.Future;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.function.Function;

@Path("/login")
public class LoginRest {
    private final LoginService loginService;

    public LoginRest(LoginService loginService) {
        this.loginService = loginService;
    }

    @POST
    @Path("/employee")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<LoginResult>> employeeLogin(Login login) {
        Assert.assertNotNull(login, "登录信息不能为空");
        login.setAdminLogin(false);

        return loginService.login(login).map(t -> {
            LoginResult loginResult = new LoginResult();
            loginResult.setAdminLogin(false);
            Employee employee = t.getEmployee();
            loginResult.setLoginName(employee.getLoginName());
            loginResult.setName(employee.getName());
            loginResult.setUserId(String.valueOf(employee.getId()));
            loginResult.setToken(t.getToken());
            return loginResult;
        }).map(RestResult::success);
    }

    @POST
    @Path("/admin")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<LoginResult>> adminLogin(Login login) {
        Assert.assertNotNull(login, "登录信息不能为空");
        Assert.assertNotEmpty(login.getEmployeeLoginName(), "用户登录名不能为空");

        login.setAdminLogin(true);
        return loginService.login(login).map(t -> {
            LoginResult loginResult = new LoginResult();
            loginResult.setAdminLogin(true);
            Admin admin = t.getAdmin();
            loginResult.setLoginName(admin.getLoginName());
            loginResult.setName(admin.getLoginName());
            loginResult.setUserId(admin.getLoginName());
            loginResult.setToken(t.getToken());
            return loginResult;
        }).map(RestResult::success);
    }
}
