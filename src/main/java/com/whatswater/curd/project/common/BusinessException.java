package com.whatswater.curd.project.common;


public class BusinessException extends RuntimeException {
    private final String code;
    public BusinessException(String code) {
        this.code = code;
    }

    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public BusinessException(String code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
