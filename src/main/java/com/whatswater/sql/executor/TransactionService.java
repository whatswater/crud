package com.whatswater.sql.executor;


import io.vertx.sqlclient.Transaction;

public interface TransactionService<T extends TransactionService<T>> {
    default <T> T bindTransaction(Transaction transaction) {
        return null;
    }

    default Transaction getCurrentTransaction() {
        return null;
    }
}
