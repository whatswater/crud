package com.whatswater.curd.project.sys.employeeFilter;


public enum EmployeeFilterDataValueTypeEnum {
    ROLE(1, "角色", "role"),
    ORGANIZATION(2, "部门", "organization"),
    EMPLOYEE(3, "人员", "employee"),
    ;
    private Integer code;
    private String name;
    private String functionName;

    EmployeeFilterDataValueTypeEnum(Integer code, String name, String functionName) {
        this.code = code;
        this.name = name;
        this.functionName = functionName;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }


    public static EmployeeFilterDataValueTypeEnum getByFunctionName(String functionName) {
        for (EmployeeFilterDataValueTypeEnum valueTypeEnum: values()) {
            if (valueTypeEnum.functionName.equals(functionName)) {
                return valueTypeEnum;
            }
        }
        return null;
    }

    public static Integer getCodeByFunctionName(String functionName) {
        for (EmployeeFilterDataValueTypeEnum valueTypeEnum: values()) {
            if (valueTypeEnum.functionName.equals(functionName)) {
                return valueTypeEnum.code;
            }
        }
        return null;
    }
}
