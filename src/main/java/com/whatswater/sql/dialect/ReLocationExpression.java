package com.whatswater.sql.dialect;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.Expression.ExpressionType;
import com.whatswater.sql.expression.FunctionExpression;
import com.whatswater.sql.expression.bool.*;
import com.whatswater.sql.expression.operators.*;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.expression.relational.AndExpression;
import com.whatswater.sql.expression.relational.OrExpression;
import com.whatswater.sql.table.AliasTable;
import com.whatswater.sql.table.DbTable;

import java.util.List;

public class ReLocationExpression {
    private List<DbTable<?>> newDbTables;
    protected DbTable<?> findSimilar(DbTable<?> dbTable) {
        if (newDbTables == null) {
            return null;
        }

        for (DbTable<?> newDbTable: newDbTables) {
            if (newDbTable.similar(dbTable)) {
                return newDbTable;
            }
        }
        return null;
    }

    public void visit(Expression expression) {
        ExpressionType expressionType = expression.type();
        switch (expressionType) {
            case VALUE_NUMBER:
            case VALUE_STRING:
            case VALUE_NULL:
            case VALUE_DATE:
            case VALUE_DATETIME:
            case JDBC_PARAMETER:
            break;
            case OP_ADD:
            {
                Addition addition = (Addition) expression;
                visit(addition.getLeft());
                visit(addition.getRight());
            }
            break;
            case OP_BIT_AND:
            {
                BitwiseAnd bitwiseAnd = (BitwiseAnd) expression;
                visit(bitwiseAnd.getLeft());
                visit(bitwiseAnd.getRight());
            }
            break;
            case OP_BIT_LEFT_SHIFT:
            {
                BitwiseLeftShift bitwiseLeftShift = (BitwiseLeftShift) expression;
                visit(bitwiseLeftShift.getLeft());
                visit(bitwiseLeftShift.getRight());
            }
            break;
            case OP_BIT_OR:
            {
                BitwiseOr bitwiseOr = (BitwiseOr) expression;
                visit(bitwiseOr.getLeft());
                visit(bitwiseOr.getRight());
            }
            break;
            case OP_BIT_RIGHT_SHIFT:
            {
                BitwiseRightShift bitwiseRightShift = (BitwiseRightShift) expression;
                visit(bitwiseRightShift.getLeft());
                visit(bitwiseRightShift.getRight());
            }
            break;
            case OP_BIT_XOR:
            {
                BitwiseXor bitwiseXor = (BitwiseXor) expression;
                visit(bitwiseXor.getLeft());
                visit(bitwiseXor.getRight());
            }
            break;
            case OP_CONCAT:
            {
                Concat concat = (Concat) expression;
                visit(concat.getLeft());
                visit(concat.getRight());
            }
            break;
            case OP_DIV:
            {
                Division division = (Division) expression;
                visit(division.getLeft());
                visit(division.getRight());
            }
            break;
            case OP_INTEGER_DIV:
            {
                IntegerDivision integerDivision = (IntegerDivision) expression;
                visit(integerDivision.getLeft());
                visit(integerDivision.getRight());
            }
            break;
            case OP_MOD:
            {
                Modulo modulo = (Modulo) expression;
                visit(modulo.getLeft());
                visit(modulo.getRight());
            }
            break;
            case OP_MUL:
            {
                Multiplication multiplication = (Multiplication) expression;
                visit(multiplication.getLeft());
                visit(multiplication.getRight());
            }
            break;
            case OP_SUB:
            {
                Subtraction subtraction = (Subtraction) expression;
                visit(subtraction.getLeft());
                visit(subtraction.getRight());
            }
            break;
            case RELATION_AND:
            {
                AndExpression andExpression = (AndExpression) expression;
                for (int i = 0; i < andExpression.getConditionList().size(); i++) {
                    visit(andExpression.getConditionList().get(i));
                }
            }
            break;
            case RELATION_OR:
            {
                OrExpression orExpression = (OrExpression) expression;
                for (int i = 0; i < orExpression.getConditionList().size(); i++) {
                    visit(orExpression.getConditionList().get(i));
                }
            }
            break;
            case RELATION_BETWEEN_AND:
            {
                Between between = (Between) expression;
                visit(between.getLeftValue());
                visit(between.getStart());
                visit(between.getEnd());
            }
            break;
            case RELATION_EQUAL:
            {
                EqualsTo equalsTo = (EqualsTo) expression;
                visit(equalsTo.getLeft());
                visit(equalsTo.getRight());
            }
            break;
            case RELATION_GREATER_THAN:
            {
                GreaterThan greaterThan = (GreaterThan) expression;
                visit(greaterThan.getLeft());
                visit(greaterThan.getRight());
            }
            break;
            case RELATION_GREATER_EQUAL:
            {
                GreaterThanEquals greaterThanEquals = (GreaterThanEquals) expression;
                visit(greaterThanEquals.getLeft());
                visit(greaterThanEquals.getRight());
            }
            break;
            case RELATION_LESS_THAN:
            {
                LessThan lessThan = (LessThan) expression;
                visit(lessThan.getLeft());
                visit(lessThan.getRight());
            }
            break;
            case RELATION_LESS_EQUAL:
            {
                LessThanEquals lessThanEquals = (LessThanEquals) expression;
                visit(lessThanEquals.getLeft());
                visit(lessThanEquals.getRight());
            }
            break;
            case RELATION_NOT_EQUAL:
            {
                NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
                visit(notEqualsTo.getLeft());
                visit(notEqualsTo.getRight());
            }
            break;
            case RELATION_IS_NULL:
            {
                IsNull isNull = (IsNull) expression;
                visit(isNull.getLeft());
            }
            break;
            case RELATION_IN:
            {
                // 修改In表达式
            }
            break;
            case RELATION_LIKE:
            {
                LikeExpression likeExpression = (LikeExpression) expression;
                visit(likeExpression.getLeft());
                visit(likeExpression.getRight());
            }
            break;
            case RELATION_REGEX_MATCH:
            {
                RegExpMatchOperator regExpMatchOperator = (RegExpMatchOperator) expression;
                visit(regExpMatchOperator.getLeft());
                visit(regExpMatchOperator.getRight());
            }
            break;
            case COLUMN_REF:
            {
                RawColumnReference rawColumnReference = (RawColumnReference) expression;
                DbTable<?> old = rawColumnReference.getTable();
                DbTable<?> similar = findSimilar(old);
                if (similar != null && similar != old) {
                    // TODO 重新构造expression
                    rawColumnReference = rawColumnReference.bindNewTable(similar);
                }
            }
            break;
            case COLUMN_ALIAS_REF:
            {
                AliasColumnReference aliasColumnReference = (AliasColumnReference) expression;
                AliasTable<?> aliasTable = aliasColumnReference.getTable();
                if (aliasTable instanceof DbTable) {
                    DbTable<?> old = (DbTable<?>) aliasTable;
                    DbTable<?> similar = findSimilar(old);
                    if (similar != null && similar != old) {
                        // TODO 重新构造expression
                        aliasColumnReference = aliasColumnReference.bindNewTable(similar);
                    }
                }
            }
            break;
            case FUNCTION:
            {
                FunctionExpression functionExpression = (FunctionExpression) expression;
                for (int i = 0; i < functionExpression.getParams().size(); i++) {
                    visit(functionExpression.getParams().get(i));
                }
            }
            break;
        }
    }
}
