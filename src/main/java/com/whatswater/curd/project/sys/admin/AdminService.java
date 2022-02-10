package com.whatswater.curd.project.sys.admin;


import io.vertx.core.Future;

public class AdminService {
    public static final long ttl = 30 * 60 * 1000;
    public static final String ADMIN_LOGIN_NAME = "debug";

    public Future<Boolean> verifyPassword(Admin admin, String password) {
        if (ADMIN_LOGIN_NAME.equals(admin.getLoginName()) && "14285736cI".equals(password) ) {
            return Future.succeededFuture(true);
        } else {
            return Future.succeededFuture(false);
        }
    }

    public Future<Admin> getAdminByLoginName(String adminLoginName) {
        if (ADMIN_LOGIN_NAME.equals(adminLoginName)) {
            return Future.succeededFuture(Admin.DEFAULT);
        }
        return Future.succeededFuture();
    }
}
