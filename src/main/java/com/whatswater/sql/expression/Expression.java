package com.whatswater.sql.expression;

import com.whatswater.sql.expression.bool.*;
import com.whatswater.sql.expression.bool.InExpression.ExpressionList;
import com.whatswater.sql.expression.bool.InExpression.LiteralList;
import com.whatswater.sql.expression.literal.Literal;
import com.whatswater.sql.expression.literal.NumberLiteral;
import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.expression.reference.JdbcParameter;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.alias.AliasPlaceholder;

import java.util.Arrays;
import java.util.Collections;

/**
 * SQL构建器所使用的表达式接口
 * 和AST不同，Expression并不是为了表示SQL语句，而是为了类型安全和生成SQL
 * 故Expression的最小粒度可能会比AST大得多，一些不影响语义的细节（例如not，!的区分，是否加as），不会体现在Expression中
 * 同时一些接口继承Expression，实现类型安全
 */
// TODO 添加构造的静态方法
// TODO 优化SFunction和常量操作
public interface Expression {
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
        return new InExpression(this, new ExpressionList(Collections.singletonList(new JdbcParameter(value))));
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

        return new InExpression(this, new ExpressionList(Arrays.asList(constList)));
    }

    default BoolExpression in(int... values) {
        JdbcParameter[] constList = new JdbcParameter[values.length];
        for(int i = 0; i < constList.length; i++) {
            constList[i] = new JdbcParameter(values[i]);
        }

        return new InExpression(this, new ExpressionList(Arrays.asList(constList)));
    }

//    default BoolExpression in(Query query) {
//        return new InSubQueryExpr(this, query);
//    }

    default OrderByElement desc() {
        return new OrderByElement(this, false);
    }

    default OrderByElement asc() {
        return new OrderByElement(this, true);
    }

    enum ExpressionType {
        VALUE_NUMBER,
        VALUE_STRING,
        VALUE_NULL,
        VALUE_DATE,
        VALUE_DATETIME,
        JDBC_PARAMETER,
        OP_ADD,
        OP_BIT_AND,
        OP_BIT_LEFT_SHIFT,
        OP_BIT_OR,
        OP_BIT_RIGHT_SHIFT,
        OP_BIT_XOR,
        OP_CONCAT,
        OP_DIV,
        OP_INTEGER_DIV,
        OP_MOD,
        OP_MUL,
        OP_SUB,
        RELATION_AND,
        RELATION_OR,
        RELATION_NOT,
        RELATION_BETWEEN_AND,
        RELATION_EQUAL,
        RELATION_GREATER_THAN,
        RELATION_GREATER_EQUAL,
        RELATION_LESS_THAN,
        RELATION_LESS_EQUAL,
        RELATION_NOT_EQUAL,
        RELATION_IS_NULL,
        RELATION_IN,
        RELATION_LIKE,
        RELATION_REGEX_MATCH,
        COLUMN_REF,
        COLUMN_ALIAS_REF,
        FUNCTION,
        ;
    }
}
