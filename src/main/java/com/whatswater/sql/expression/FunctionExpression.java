package com.whatswater.sql.expression;


import java.util.Arrays;
import java.util.List;

public class FunctionExpression implements Expression {
    private String functionName;
    private List<Expression> params;

    public FunctionExpression() {

    }

    public FunctionExpression(String functionName) {
        this.functionName = functionName;
    }

    public FunctionExpression(String functionName, List<Expression> params) {
        this.functionName = functionName;
        this.params = params;
    }

    public FunctionExpression(String functionName, Expression... params) {
        this.functionName = functionName;
        this.params = Arrays.asList(params);
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.FUNCTION;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<Expression> getParams() {
        return params;
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        if (params != null) {
            for (Expression expression: params) {
                expression.visitAliasHolder(handler);
            }
        }
    }
}
