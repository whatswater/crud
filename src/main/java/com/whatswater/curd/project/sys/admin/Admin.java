package com.whatswater.curd.project.sys.admin;


public class Admin {
    private String loginName;
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }


    private static Admin createAdmin() {
        Admin admin = new Admin();
        admin.loginName = "debug";
        admin.password = "123456";
        return admin;
    }

    public static final Admin DEFAULT = createAdmin();
}
