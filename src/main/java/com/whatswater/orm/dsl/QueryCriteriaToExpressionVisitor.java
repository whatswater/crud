package com.whatswater.orm.dsl;

import com.whatswater.orm.dsl.criteria.And;
import com.whatswater.orm.dsl.criteria.Not;
import com.whatswater.orm.dsl.criteria.Or;
import com.whatswater.orm.dsl.criteria.QueryParam;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.judge.EqualsTo;
import com.whatswater.sql.expression.literal.JdbcParameter;
import com.whatswater.sql.expression.logic.AndExpression;
import com.whatswater.sql.expression.logic.NotExpression;
import com.whatswater.sql.expression.logic.OrExpression;

import java.util.ArrayList;
import java.util.List;


public class QueryCriteriaToExpressionVisitor implements QueryCriteriaVisitor {
    QueryCriteriaPath queryCriteriaPath = new AndPath(null);

    public BoolExpression getBoolExpression() {
        AndExpression andExpression = (AndExpression) queryCriteriaPath.getValue();
        return andExpression.flatten();
    }

    @Override
    public void visitAnd(And and) {
        List<? extends QueryCriteria> queryCriteriaList = and.getConditionList();
        AndPath andPath = new AndPath(this.queryCriteriaPath);
        this.queryCriteriaPath.addOrSetQueryParam(andPath.getValue());
        this.queryCriteriaPath = andPath;
        for(QueryCriteria criteria : queryCriteriaList) {
            this.visit(criteria);
        }
        this.queryCriteriaPath = andPath.prev();
    }

    @Override
    public void visitOr(Or or) {
        List<? extends QueryCriteria> queryCriteriaList = or.getConditionList();
        OrPath orPath = new OrPath(this.queryCriteriaPath);
        this.queryCriteriaPath.addOrSetQueryParam(orPath.getValue());
        this.queryCriteriaPath = orPath;
        for(QueryCriteria criteria : queryCriteriaList) {
            this.visit(criteria);
        }
        this.queryCriteriaPath = orPath.prev();
    }

    @Override
    public void visitQueryParam(QueryParam queryParam) {
        // TODO 添加一个context，由context将queryParam解析成BOOL表达式
        this.queryCriteriaPath.addOrSetQueryParam(new EqualsTo(new JdbcParameter(), new JdbcParameter()));
    }

    @Override
    public void visitNot(Not not) {
        NotPath notPath = new NotPath(this.queryCriteriaPath);
        this.queryCriteriaPath.addOrSetQueryParam(notPath.getValue());
        this.queryCriteriaPath = notPath;
        this.visit(not.getQueryCriteria());
        this.queryCriteriaPath = notPath.prev();
    }

    interface QueryCriteriaPath {
        QueryCriteriaPath prev();
        void addOrSetQueryParam(BoolExpression expression);
        BoolExpression getValue();
    }

    static class AndPath implements QueryCriteriaPath {
        AndExpression andExpression;
        final QueryCriteriaPath prev;

        public AndPath(QueryCriteriaPath prev) {
            this.prev = prev;
            this.andExpression = new AndExpression(new ArrayList<>());
        }

        @Override
        public QueryCriteriaPath prev() {
            return prev;
        }

        @Override
        public void addOrSetQueryParam(BoolExpression expression) {
            this.andExpression.getConditionList().add(expression);
        }

        @Override
        public BoolExpression getValue() {
            return andExpression;
        }
    }

    static class OrPath implements QueryCriteriaPath {
        OrExpression orExpression;
        final QueryCriteriaPath prev;

        public OrPath(QueryCriteriaPath prev) {
            this.prev = prev;
            this.orExpression = new OrExpression(new ArrayList<>());
        }

        @Override
        public QueryCriteriaPath prev() {
            return prev;
        }

        @Override
        public void addOrSetQueryParam(BoolExpression expression) {
            this.orExpression.getConditionList().add(expression);
        }

        @Override
        public BoolExpression getValue() {
            return orExpression;
        }
    }

    static class NotPath implements QueryCriteriaPath {
        NotExpression notExpression;
        final QueryCriteriaPath prev;

        public NotPath(QueryCriteriaPath prev) {
            this.prev = prev;
            this.notExpression = new NotExpression();
        }

        @Override
        public QueryCriteriaPath prev() {
            return prev;
        }

        @Override
        public void addOrSetQueryParam(BoolExpression expression) {
            this.notExpression.setExpression(expression);
        }

        @Override
        public BoolExpression getValue() {
            return notExpression;
        }
    }
}
