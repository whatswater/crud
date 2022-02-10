package com.whatswater.curd.project.common;


public enum ErrorCodeEnum {
    PARAM_NO_VALID("01", "参数检查不通过"),

    UPDATE_ID_NULL("0201", "更新时Id为空"),
    UPDATE_NON_DATA("0202", "更新数据条数为0"),

    TOO_MANY_RESULT("0203", "查询数据时查出多条数据"),
    GET_NOT_EXISTS("0204", "未查出任何数据"),

    NOT_LOGIN("0301", "未登录"),
    PERMISSION_DENY("0302", "权限不足"),

    USER_NOT_EXISTS("0401", "用户不存在"),
    USER_DISABLED("0402", "用户已停用"),
    USER_HAS_NOT_ROLE("0403", "用户没有角色"),
    USER_PASSWORD_ERROR("0404", "用户密码错误"),
    ;

    private String errCode;
    private String errMsg;

    ErrorCodeEnum(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public BusinessException toException() {
        return new BusinessException(errCode, errMsg);
    }

    public BusinessException toException(String errMsg) {
        return new BusinessException(errCode, errMsg);
    }

    public BusinessException childException(String childCode, String errMsg) {
        return new BusinessException(errCode + childCode, errMsg);
    }
}
