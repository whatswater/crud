package com.whatswater.nothing.property;


public class PropertyNotExistsException extends RuntimeException {
    private String propertyName;

    public PropertyNotExistsException(String propertyName) {
        super("property: " + propertyName + " is not exists!");
        this.propertyName = propertyName;
    }

    public PropertyNotExistsException(String message, String propertyName) {
        super(message);
        this.propertyName = propertyName;
    }

    public PropertyNotExistsException(String message, Throwable cause, String propertyName) {
        super(message, cause);
        this.propertyName = propertyName;
    }

    public PropertyNotExistsException(Throwable cause, String propertyName) {
        super("property: " + propertyName + " is not exists!", cause);
        this.propertyName = propertyName;
    }

    public PropertyNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String propertyName) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
