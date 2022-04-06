package com.whatswater.nothing.schema;


public enum QueryParamNameOperationEnum {
    EQ("eq"),
    GT("gt"),
    LT("lt"),
    GE("ge"),
    LE("le"),
    RANGE("range"),
    IN("in"),
    LIKE("like"),
    LEFT_LIKE("left_like"),
    RIGHT_LIKE("right_like"),
    ;

    private String operation;
    QueryParamNameOperationEnum(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public static QueryParamNameOperationEnum get(String operation) {
        for (QueryParamNameOperationEnum e: QueryParamNameOperationEnum.values()) {
            if (e.operation.equals(operation)) {
                return e;
            }
        }
        return null;
    }
}
