package com.whatswater.curd.project.sys.employee;


public enum EmployeeStatus {
    INIT(0, "初始化"),
    ENABLED(1, "启用"),
    DISABLED(2, "停用"),
    ;

    private int id;
    private String name;


    EmployeeStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static boolean isDisabled(int status) {
        return DISABLED.id == status || INIT.id == status;
    }

    public static boolean isEnabled(int status) {
        return ENABLED.id == status;
    }

    public static boolean canDelete(int status) {
        return INIT.id == status;
    }
}
