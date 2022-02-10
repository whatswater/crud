package com.whatswater.sql.expression;

import com.whatswater.sql.alias.AliasHolderVisitor;
import com.whatswater.sql.dialect.Dialect.SQL;
import com.whatswater.sql.dialect.ExpressionSqlVisitor;
import com.whatswater.sql.expression.judge.*;
import com.whatswater.sql.expression.judge.InExpression.LiteralList;
import com.whatswater.sql.expression.judge.InExpression.SubSelect;
import com.whatswater.sql.expression.literal.NumberLiteral;
import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.expression.literal.JdbcParameter;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.table.ComplexTable;
import com.whatswater.sql.table.SelectedTable;

import java.util.Arrays;


public interface Expression extends AliasHolderVisitor {
    ExpressionType type();

    default BoolExpression isNull() {
        return new IsNull(this);
    }
    default BoolExpression gt(Expression expr) {
        return new GreaterThan(this, expr);
    }
    default BoolExpression gt(Object value) {
        if(value instanceof Expression) {
            return new GreaterThan(this, (Expression) value);
        } else {
            return new GreaterThan(this,  new JdbcParameter(value));
        }
    }
    default BoolExpression ge(Expression expr) {
        return new GreaterThanEquals(this, expr);
    }
    default BoolExpression ge(Object value) {
        if(value instanceof Expression) {
            return new GreaterThanEquals(this, (Expression) value);
        } else {
            return new GreaterThanEquals(this,  new JdbcParameter(value));
        }
    }
    default BoolExpression lt(Expression expr) {
        return new LessThan(this, expr);
    }
    default BoolExpression lt(Object value) {
        if(value instanceof Expression) {
            return new LessThan(this, (Expression) value);
        } else {
            return new LessThan(this,  new JdbcParameter(value));
        }
    }
    default BoolExpression le(Expression expr) {
        return new LessThanEquals(this, expr);
    }
    default BoolExpression le(Object value) {
        if(value instanceof Expression) {
            return new LessThanEquals(this, (Expression) value);
        } else {
            return new LessThanEquals(this,  new JdbcParameter(value));
        }
    }
    default BoolExpression eq(Expression expr) {
        return new EqualsTo(this, expr);
    }
    default BoolExpression eq(Object value) {
        if(value instanceof Expression) {
            return new EqualsTo(this, (Expression) value);
        } else {
            return new EqualsTo(this,  new JdbcParameter(value));
        }
    }
    default FunctionExpression count() {
        return new FunctionExpression("count", this);
    }
    default Alias as(String aliasName) {
        return new Alias(this, aliasName);
    }
    default Alias as(AliasPlaceholder aliasPlaceholder) {
        return new Alias(this, aliasPlaceholder);
    }
    default BoolExpression ne(Expression expr) {
        return new NotEqualsTo(this, expr);
    }
    default BoolExpression ne(Object value) {
        if(value instanceof Expression) {
            return new NotEqualsTo(this, (Expression) value);
        } else {
            return new NotEqualsTo(this,  new JdbcParameter(value));
        }
    }
    default BoolExpression like(Literal str) {
        return new LikeExpression(this, str);
    }
    default BoolExpression like(String value) {
        return new LikeExpression(this, new JdbcParameter(value));
    }
    default BoolExpression inConstValues(Literal ...constList) {
        return new InExpression(this, new LiteralList(Arrays.asList(constList)));
    }
    default BoolExpression inConstValues(int... values) {
        Literal[] constList = new NumberLiteral[values.length];
        for(int i = 0; i < constList.length; i++) {
            constList[i] = new NumberLiteral(values[i]);
        }

        return new InExpression(this, new LiteralList(Arrays.asList(constList)));
    }
    default BoolExpression in(String... values) {
        JdbcParameter[] constList = new JdbcParameter[values.length];
        for(int i = 0; i < constList.length; i++) {
            constList[i] = new JdbcParameter(values[i]);
        }

        return new InExpression(this, new LiteralList(Arrays.asList(constList)));
    }
    default BoolExpression in(int... values) {
        JdbcParameter[] constList = new JdbcParameter[values.length];
        for(int i = 0; i < constList.length; i++) {
            constList[i] = new JdbcParameter(values[i]);
        }
        return new InExpression(this, new LiteralList(Arrays.asList(constList)));
    }
    default BoolExpression in(SelectedTable table) {
        return new InExpression(this, new SubSelect(table));
    }
    default BoolExpression in(ComplexTable table) {
        return new InExpression(this, new SubSelect(table));
    }
    default OrderByElement desc() {
        return new OrderByElement(this, false);
    }
    default OrderByElement asc() {
        return new OrderByElement(this, true);
    }

    enum ExpressionType {
        // 常量
        LITERAL,

        // 算术
        ARITHMETIC_OPERATOR,
        // 逻辑
        LOGIC_OPERATOR,
        // 判断
        JUDGE_OPERATOR,

        // 变量引用
        COLUMN_REF,

        // 函数
        FUNCTION,
        ;
    }

    static SQL toSQL(ExpressionSqlVisitor visitor, Expression expression) {
        ExpressionSqlVisitor newVisitor = visitor.newSQL();
        newVisitor.visit(expression);
        return newVisitor.getSql();
    }
}
