package com.whatswater.sql.dialect;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.Expression.ExpressionType;
import com.whatswater.sql.expression.FunctionExpression;
import com.whatswater.sql.expression.bool.*;
import com.whatswater.sql.expression.literal.DateTimeExpression;
import com.whatswater.sql.expression.literal.DateValue;
import com.whatswater.sql.expression.literal.NumberLiteral;
import com.whatswater.sql.expression.literal.StringValue;
import com.whatswater.sql.expression.operators.*;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.JdbcParameter;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.expression.relational.AndExpression;
import com.whatswater.sql.expression.relational.OrExpression;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpressionSqlVisitor extends ToSqlVisitor {
    // TODO visit方法添加上下文环境，处理括号问题，处理table的序列化问题
    public void visit(Expression expression) {
        ExpressionType expressionType = expression.type();
        switch (expressionType) {
            case VALUE_NUMBER:
                {
                    NumberLiteral numberLiteral = (NumberLiteral)expression;
                    sql.append(numberToString(numberLiteral.getValue()));
                }
                break;
            case VALUE_STRING:
                {
                    StringValue stringValue = (StringValue) expression;
                    sql.append("'").append(stringValue.getValue()).append("'");
                }
                break;
            case VALUE_NULL:
                {
                    sql.append("null");
                }
                break;
            case VALUE_DATE:
                {
                    DateValue dateValue = (DateValue) expression;
                    sql.append("'").append(dateFormat(dateValue.getValue())).append("'");
                }
                break;
            case VALUE_DATETIME:
                {
                    DateTimeExpression dateTimeExpression = (DateTimeExpression) expression;
                    sql.append("'").append(dateTimeFormat(dateTimeExpression.getValue())).append("'");
                }
                break;
            case JDBC_PARAMETER:
                {
                    JdbcParameter jdbcParameter = (JdbcParameter) expression;
                    sql.append("?");
                    params.add(jdbcParameter.getValue());
                }
                break;
            case OP_ADD:
                {
                    Addition addition = (Addition) expression;
                    sql.append("(");
                    visit(addition.getLeft());
                    sql.append(" + ");
                    visit(addition.getRight());
                    sql.append(")");
                }
                break;
            case OP_BIT_AND:
                {
                    BitwiseAnd bitwiseAnd = (BitwiseAnd) expression;
                    sql.append("(");
                    visit(bitwiseAnd.getLeft());
                    sql.append(" & ");
                    visit(bitwiseAnd.getRight());
                    sql.append(")");
                }
                break;
            case OP_BIT_LEFT_SHIFT:
                {
                    BitwiseLeftShift bitwiseLeftShift = (BitwiseLeftShift) expression;
                    sql.append("(");
                    visit(bitwiseLeftShift.getLeft());
                    sql.append(" << ");
                    visit(bitwiseLeftShift.getRight());
                    sql.append(")");
                }
                break;
            case OP_BIT_OR:
                {
                    BitwiseOr bitwiseOr = (BitwiseOr) expression;
                    sql.append("(");
                    visit(bitwiseOr.getLeft());
                    sql.append(" | ");
                    visit(bitwiseOr.getRight());
                    sql.append(")");
                }
                break;
            case OP_BIT_RIGHT_SHIFT:
                {
                    BitwiseRightShift bitwiseRightShift = (BitwiseRightShift) expression;
                    sql.append("(");
                    visit(bitwiseRightShift.getLeft());
                    sql.append(" >> ");
                    visit(bitwiseRightShift.getRight());
                    sql.append(")");
                }
                break;
            case OP_BIT_XOR:
                {
                    BitwiseXor bitwiseXor = (BitwiseXor) expression;
                    sql.append("(");
                    visit(bitwiseXor.getLeft());
                    sql.append(" ^ ");
                    visit(bitwiseXor.getRight());
                    sql.append(")");
                }
                break;
            case OP_CONCAT:
                {
                    Concat concat = (Concat) expression;
                    sql.append("(");
                    visit(concat.getLeft());
                    sql.append(" || ");
                    visit(concat.getRight());
                    sql.append(")");
                }
                break;
            case OP_DIV:
                {
                    Division division = (Division) expression;
                    sql.append("(");
                    visit(division.getLeft());
                    sql.append(" / ");
                    visit(division.getRight());
                    sql.append(")");
                }
                break;
            case OP_INTEGER_DIV:
                {
                    IntegerDivision integerDivision = (IntegerDivision) expression;
                    sql.append("(");
                    visit(integerDivision.getLeft());
                    sql.append(" // ");
                    visit(integerDivision.getRight());
                    sql.append(")");
                }
                break;
            case OP_MOD:
                {
                    Modulo modulo = (Modulo) expression;
                    sql.append("(");
                    visit(modulo.getLeft());
                    sql.append(" % ");
                    visit(modulo.getRight());
                    sql.append(")");
                }
                break;
            case OP_MUL:
                {
                    Multiplication multiplication = (Multiplication) expression;
                    sql.append("(");
                    visit(multiplication.getLeft());
                    sql.append(" * ");
                    visit(multiplication.getRight());
                    sql.append(")");
                }
                break;
            case OP_SUB:
                {
                    Subtraction subtraction = (Subtraction) expression;
                    sql.append("(");
                    visit(subtraction.getLeft());
                    sql.append(" - ");
                    visit(subtraction.getRight());
                    sql.append(")");
                }
                break;
            case RELATION_AND:
                {
                    AndExpression andExpression = (AndExpression) expression;
                    sql.append("(");
                    Expression first = andExpression.getConditionList().get(0);
                    visit(first);
                    for (int i = 1; i < andExpression.getConditionList().size(); i++) {
                        sql.append(" and ");
                        visit(andExpression.getConditionList().get(i));
                    }
                    sql.append(")");
                }
                break;
            case RELATION_OR:
                {
                    OrExpression orExpression = (OrExpression) expression;
                    sql.append("(");
                    Expression first = orExpression.getConditionList().get(0);
                    visit(first);
                    for (int i = 1; i < orExpression.getConditionList().size(); i++) {
                        sql.append(" or ");
                        visit(orExpression.getConditionList().get(i));
                    }
                    sql.append(")");
                }
                break;
            case RELATION_BETWEEN_AND:
                {
                    Between between = (Between) expression;
                    visit(between.getLeftValue());
                    sql.append(between.isNot() ? " not between " : " between ");
                    visit(between.getStart());
                    sql.append(" and ");
                    visit(between.getEnd());
                }
                break;
            case RELATION_EQUAL:
                {
                    EqualsTo equalsTo = (EqualsTo) expression;
                    visit(equalsTo.getLeft());
                    sql.append(" = ");
                    visit(equalsTo.getRight());
                }
                break;
            case RELATION_GREATER_THAN:
                {
                    GreaterThan greaterThan = (GreaterThan) expression;
                    visit(greaterThan.getLeft());
                    sql.append(" > ");
                    visit(greaterThan.getRight());
                }
                break;
            case RELATION_GREATER_EQUAL:
                {
                    GreaterThanEquals greaterThanEquals = (GreaterThanEquals) expression;
                    visit(greaterThanEquals.getLeft());
                    sql.append(" >= ");
                    visit(greaterThanEquals.getRight());
                }
                break;
            case RELATION_LESS_THAN:
                {
                    LessThan lessThan = (LessThan) expression;
                    visit(lessThan.getLeft());
                    sql.append(" < ");
                    visit(lessThan.getRight());
                }
                break;
            case RELATION_LESS_EQUAL:
                {
                    LessThanEquals lessThanEquals = (LessThanEquals) expression;
                    visit(lessThanEquals.getLeft());
                    sql.append(" <= ");
                    visit(lessThanEquals.getRight());
                }
                break;
            case RELATION_NOT_EQUAL:
                {
                    NotEqualsTo notEqualsTo = (NotEqualsTo) expression;
                    visit(notEqualsTo.getLeft());
                    sql.append(" <> ");
                    visit(notEqualsTo.getRight());
                }
                break;
            case RELATION_IS_NULL:
                {
                    IsNull isNull = (IsNull) expression;
                    visit(isNull.getLeft());
                    sql.append(" is null");
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
                    sql.append(" like ");
                    visit(likeExpression.getRight());
                }
                break;
            case RELATION_REGEX_MATCH:
                {
                    RegExpMatchOperator regExpMatchOperator = (RegExpMatchOperator) expression;
                    visit(regExpMatchOperator.getLeft());
                    sql.append(" ").append(regExpMatchOperator.getOperatorString()).append(" ");
                    visit(regExpMatchOperator.getRight());
                }
                break;
            case COLUMN_REF:
                {
                    RawColumnReference rawColumnReference = (RawColumnReference) expression;
                    sql.append(rawColumnReference.getTable().getAliasOrTableName()).append(".").append(rawColumnReference.getAliasOrColumnName());
                }
                break;
            case COLUMN_ALIAS_REF:
                {
                    AliasColumnReference aliasColumnReference = (AliasColumnReference) expression;
                    sql.append(aliasColumnReference.getTable().getAliasOrTableName()).append(".").append(aliasColumnReference.getAliasOrColumnName());
                }
                break;
            case FUNCTION:
                {
                    FunctionExpression functionExpression = (FunctionExpression) expression;
                    sql.append(functionExpression.getFunctionName()).append("(");
                    Expression first = functionExpression.getParams().get(0);
                    visit(first);
                    for (int i = 1; i < functionExpression.getParams().size(); i++) {
                        sql.append(", ");
                        visit(functionExpression.getParams().get(i));
                    }
                    sql.append(")");
                }
                break;

        }
    }

    public void clearAll() {
        this.sql = new StringBuilder();
        this.params = new ArrayList<>();
    }

    public void clearSql() {
        this.sql = new StringBuilder();
    }

    public StringBuilder getSql() {
        return sql;
    }

    public StringBuilder getAndClearSql() {
        StringBuilder originSql = sql;
        this.sql = new StringBuilder();
        return originSql;
    }

    public List<Object> getParams() {
        return params;
    }

    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String dateFormat(LocalDate localDate) {
        return localDate.format(dateFormatter);
    }

    private static String dateTimeFormat(LocalDateTime localDateTime) {
        return localDateTime.format(dateTimeFormatter);
    }

    private static String numberToString(Number number) {
        if (number instanceof BigDecimal) {
            BigDecimal decimal = (BigDecimal) number;
            return decimal.toPlainString();
        }
        return number.toString();
    }
}
