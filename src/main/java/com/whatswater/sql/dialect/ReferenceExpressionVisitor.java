package com.whatswater.sql.dialect;


import com.whatswater.sql.expression.*;
import com.whatswater.sql.expression.judge.Between;
import com.whatswater.sql.expression.judge.InExpression;
import com.whatswater.sql.expression.judge.IsNull;
import com.whatswater.sql.expression.logic.AndExpression;
import com.whatswater.sql.expression.logic.NotExpression;
import com.whatswater.sql.expression.logic.OrExpression;

public abstract class ReferenceExpressionVisitor implements ExpressionVisitor {
    @Override
    public void visit(FunctionExpression function) {
        for (Expression expression: function.getParams()) {
            visit(expression);
        }
    }

    @Override
    public abstract void visit(ReferenceExpression reference);

    @Override
    public void visit(Literal literal) {

    }

    @Override
    public void visit(JudgeOperatorExpression judgeOperator) {
        if (judgeOperator instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) judgeOperator;
            visit(binaryExpression.getLeft());
            visit(binaryExpression.getRight());
        } else if (judgeOperator instanceof Between) {
            Between between = (Between) judgeOperator;
            visit(between.getLeftValue());
            visit(between.getStart());
            visit(between.getEnd());
        } else if (judgeOperator instanceof IsNull) {
            IsNull isNull = (IsNull) judgeOperator;
            visit(isNull.getLeft());
        } else if (judgeOperator instanceof InExpression) {
            InExpression inExpression = (InExpression) judgeOperator;
            visit(inExpression.getLeftValue());
        }
    }

    @Override
    public void visit(LogicExpression logic) {
        if (logic instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) logic;
            for (Expression expression: andExpression.getConditionList()) {
                visit(expression);
            }
        } else if (logic instanceof OrExpression) {
            OrExpression orExpression = (OrExpression) logic;
            for (Expression expression: orExpression.getConditionList()) {
                visit(expression);
            }
        } else if (logic instanceof NotExpression) {
            NotExpression notExpression = (NotExpression) logic;
            visit(notExpression.getExpression());
        }
    }

    @Override
    public void visit(ArithmeticExpression arithmeticExpression) {
        if (arithmeticExpression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) arithmeticExpression;
            visit(binaryExpression.getLeft());
            visit(binaryExpression.getRight());
        }
    }
}
