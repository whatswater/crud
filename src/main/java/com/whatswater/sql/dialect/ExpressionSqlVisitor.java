package com.whatswater.sql.dialect;


import com.whatswater.sql.dialect.Dialect.SQL;
import com.whatswater.sql.expression.*;
import com.whatswater.sql.expression.judge.*;
import com.whatswater.sql.expression.judge.InExpression.ItemList;
import com.whatswater.sql.expression.literal.*;
import com.whatswater.sql.expression.logic.NotExpression;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.expression.logic.AndExpression;
import com.whatswater.sql.expression.logic.OrExpression;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ExpressionSqlVisitor implements ExpressionVisitor {
    private SQL sql;
    private Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap;

    public ExpressionSqlVisitor(SQL sql) {
        this.sql = sql;
    }

    public SQL getSql() {
        return sql;
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

    @Override
    public void visit(FunctionExpression function) {
        sql.append(function.getFunctionName()).append("(");
        Expression first = function.getParams().get(0);
        visit(first);
        for (int i = 1; i < function.getParams().size(); i++) {
            sql.append(", ");
            visit(function.getParams().get(i));
        }
        sql.append(")");
    }

    @Override
    public void visit(ReferenceExpression reference) {
        ReferenceExpression newRef = symbolReplaceMap == null ? null : symbolReplaceMap.get(reference);
        if (newRef != null) {
            reference = newRef;
        }

        if (reference instanceof RawColumnReference) {
            RawColumnReference rawColumnReference = (RawColumnReference) reference;
            sql.append(rawColumnReference.getTable().getAliasOrTableName()).append(".").append(rawColumnReference.getAliasOrColumnName());
        } else if (reference instanceof AliasColumnReference) {
            AliasColumnReference aliasColumnReference = (AliasColumnReference) reference;
            sql.append(aliasColumnReference.getTable().getAliasOrTableName()).append(".").append(aliasColumnReference.getAliasOrColumnName());
        }
    }

    @Override
    public void visit(Literal literal) {
        if (literal instanceof NumberLiteral) {
            NumberLiteral numberLiteral = (NumberLiteral)literal;
            sql.append(numberToString(numberLiteral.getValue()));
        } else if (literal instanceof StringValue) {
            StringValue stringValue = (StringValue) literal;
            sql.append("'").append(stringValue.getValue()).append("'");
        } else if (literal instanceof NullValue) {
            sql.append("null");
        } else if (literal instanceof DateValue) {
            DateValue dateValue = (DateValue) literal;
            sql.append("'").append(dateFormat(dateValue.getValue())).append("'");
        } else if (literal instanceof DateTimeExpression) {
            DateTimeExpression dateTimeExpression = (DateTimeExpression) literal;
            sql.append("'").append(dateTimeFormat(dateTimeExpression.getValue())).append("'");
        } else if (literal instanceof JdbcParameter) {
            JdbcParameter jdbcParameter = (JdbcParameter) literal;
            sql.append("?");
            sql.addParam(jdbcParameter.getValue());
        }
    }

    @Override
    public void visit(JudgeOperatorExpression judgeOperator) {
        if (judgeOperator instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) judgeOperator;
            visit(binaryExpression.getLeft());
            sql.append(" ").append(binaryExpression.getOperatorString()).append(" ");
            visit(binaryExpression.getRight());
        } else if (judgeOperator instanceof Between) {
            Between between = (Between) judgeOperator;
            visit(between.getLeftValue());
            sql.append(between.isNot() ? " not between " : " between ");
            visit(between.getStart());
            sql.append(" and ");
            visit(between.getEnd());
        } else if (judgeOperator instanceof IsNull) {
            IsNull isNull = (IsNull) judgeOperator;
            visit(isNull.getLeft());
            sql.append(" is null");
        } else if (judgeOperator instanceof InExpression) {
            InExpression inExpression = (InExpression) judgeOperator;
            visit(inExpression.getLeftValue());

            // todo itemList转SQL
            ItemList itemList = inExpression.getInList();
            sql.append(" in ");
            sql.append("()");
        } else if (judgeOperator instanceof ExistsExpression) {
            ExistsExpression existsExpression = (ExistsExpression) judgeOperator;
            // todo exist转SQL
        }
    }

    @Override
    public void visit(LogicExpression logic) {
        if (logic instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) logic;
            sql.append("(");
            Expression first = andExpression.getConditionList().get(0);
            visit(first);
            for (int i = 1; i < andExpression.getConditionList().size(); i++) {
                sql.append(" and ");
                visit(andExpression.getConditionList().get(i));
            }
            sql.append(")");
        } else if (logic instanceof OrExpression) {
            OrExpression orExpression = (OrExpression) logic;
            sql.append("(");
            Expression first = orExpression.getConditionList().get(0);
            visit(first);
            for (int i = 1; i < orExpression.getConditionList().size(); i++) {
                sql.append(" or ");
                visit(orExpression.getConditionList().get(i));
            }
            sql.append(")");
        } else if (logic instanceof NotExpression) {
            NotExpression notExpression = (NotExpression) logic;
            sql.append("not ");
            visit(notExpression.getExpression());
        }
    }

    @Override
    public void visit(ArithmeticExpression arithmeticExpression) {
        if (arithmeticExpression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) arithmeticExpression;
            visit(binaryExpression.getLeft());
            sql.append(" ").append(binaryExpression.getOperatorString()).append(" ");
            visit(binaryExpression.getRight());
        }
    }

    public void setSymbolReplaceMap(Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap) {
        this.symbolReplaceMap = symbolReplaceMap;
    }

    public ExpressionSqlVisitor newSQL() {
        ExpressionSqlVisitor expressionSqlVisitor = new ExpressionSqlVisitor(new SQL());
        expressionSqlVisitor.setSymbolReplaceMap(symbolReplaceMap);
        return expressionSqlVisitor;
    }
}
