package com.whatswater.gen;


public enum AccessTypeEnum {
    PUBLIC(0b11, "public"),
    PROTECTED(0b10, "protected"),
    FRIENDLY(0b01, "friendly"),
    PRIVATE(0, "private"),
    ;

    private int value;
    private String code;

    AccessTypeEnum(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}
