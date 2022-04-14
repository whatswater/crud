package com.whatswater.sql.executor;


public interface TransactionService<T extends TransactionService<T>> {
    default T bindNewSqlSession(SqlSession sqlSession) {
        return null;
    }

    default SqlSession getCurrentSqlSession() {
        return null;
    }
}
