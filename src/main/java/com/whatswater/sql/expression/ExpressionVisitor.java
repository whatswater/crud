package com.whatswater.sql.expression;

public interface ExpressionVisitor {
    default void visit(Expression expression) {
        if (expression instanceof FunctionExpression) {
            visit((FunctionExpression) expression);
        } else if (expression instanceof ReferenceExpression) {
            visit((ReferenceExpression) expression);
        } else if (expression instanceof Literal) {
            visit((Literal) expression);
        } else if (expression instanceof JudgeOperatorExpression) {
            visit((JudgeOperatorExpression) expression);
        } else if (expression instanceof LogicExpression) {
            visit((LogicExpression) expression);
        } else if (expression instanceof ArithmeticExpression) {
            visit((ArithmeticExpression) expression);
        }
    }

    void visit(FunctionExpression function);
    void visit(ReferenceExpression reference);
    void visit(Literal literal);
    void visit(JudgeOperatorExpression judgeOperator);
    void visit(LogicExpression logic);
    void visit(ArithmeticExpression arithmeticExpression);
}
