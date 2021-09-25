package com.whatswater.asyncmodule;

public class ModuleSystemException extends RuntimeException {
    public ModuleSystemException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public ModuleSystemException(String msg) {
        super(msg);
    }
}
