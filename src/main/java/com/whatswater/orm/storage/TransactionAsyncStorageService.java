package com.whatswater.orm.storage;

public interface TransactionAsyncStorageService extends AsyncStorageService {
    Transaction startTransaction();
}
