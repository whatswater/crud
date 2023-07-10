package com.whatswater.orm.dsl;

import com.whatswater.orm.dsl.criteria.And;
import com.whatswater.orm.dsl.criteria.Not;
import com.whatswater.orm.dsl.criteria.Or;
import com.whatswater.orm.dsl.criteria.QueryParam;


public interface QueryCriteriaVisitor {
    void visitAnd(And and);
    void visitOr(Or or);
    void visitQueryParam(QueryParam queryParam);
    void visitNot(Not not);

    default void visit(QueryCriteria queryCriteria) {
        if (queryCriteria instanceof And) {
            visitAnd((And) queryCriteria);
        } else if (queryCriteria instanceof QueryParam) {
            visitQueryParam((QueryParam) queryCriteria);
        } else if (queryCriteria instanceof Or) {
            visitOr((Or) queryCriteria);
        } else if (queryCriteria instanceof Not) {
            visitNot((Not) queryCriteria);
        }
    }
}
