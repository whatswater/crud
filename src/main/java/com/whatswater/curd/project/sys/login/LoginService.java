package com.whatswater.curd.project.sys.login;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.ErrorCodeEnum;
import com.whatswater.curd.project.sys.admin.AdminService;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.employee.EmployeeStatus;
import com.whatswater.curd.project.sys.permission.UserToken;
import com.whatswater.curd.project.sys.permission.UserTokenService;
import com.whatswater.curd.project.sys.uid.UidGeneratorService;
import io.vertx.core.Future;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class LoginService {
    private EmployeeService employeeService;
    private AdminService adminService;
    private UserTokenService userTokenService;

    private final UidGeneratorService uidGeneratorService;
    private final LoginSQL loginSQL;

    public LoginService(MySQLPool pool, UidGeneratorService uidGeneratorService) {
        this.loginSQL = new LoginSQL(SQLExecute.createMySQL(pool));
        this.uidGeneratorService = uidGeneratorService;
    }

    public Future<UserToken> login(Login login) {
        if (login.isAdminLogin()) {
            return adminService.getAdminByLoginName(login.getLoginName()).compose(admin -> {
                if (admin == null) {
                    return CrudUtils.failedFuture(ErrorCodeEnum.USER_NOT_EXISTS);
                }
                return adminService.verifyPassword(admin, login.getPassword()).compose(result -> {
                    if (!result) {
                        return CrudUtils.failedFuture(ErrorCodeEnum.USER_PASSWORD_ERROR);
                    }

                    return employeeService.getByLoginName(login.getEmployeeLoginName()).compose(employee -> {
                        if (employee == null) {
                            return CrudUtils.failedFuture(ErrorCodeEnum.USER_NOT_EXISTS);
                        }
                        if (!EmployeeStatus.isEnabled(employee.getStatus())) {
                            return CrudUtils.failedFuture(ErrorCodeEnum.USER_DISABLED);
                        }
                        return Future.succeededFuture(userTokenService.newToken(admin, employee));
                    });
                });
            });
        } else {
            Future<Employee> employeeFuture = employeeService.getByLoginName(login.getLoginName());
            return employeeFuture.compose(employee -> {
                if (employee == null) {
                    return CrudUtils.failedFuture(ErrorCodeEnum.USER_NOT_EXISTS);
                }
                if (!EmployeeStatus.isEnabled(employee.getStatus())) {
                    return CrudUtils.failedFuture(ErrorCodeEnum.USER_DISABLED);
                }
//                boolean verifyResult = employeeService.verifyPassword(employee, login.getPassword());
//                if (!verifyResult) {
//                    return CrudUtils.failedFuture(ErrorCodeEnum.USER_PASSWORD_ERROR);
//                }
                return Future.succeededFuture(userTokenService.newToken(employee));
            });
        }
    }

    public void logout() {

    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    public void setUserTokenService(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }
}
