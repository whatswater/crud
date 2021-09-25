package com.whatswater.sql.executor;


import java.util.List;

public interface Executor {
    void query(String sql, Object[] params, QueryCallBack queryCallBack);
    void update(String sql, Object[] params);
    void batchUpdate(String sql, List<Object[]> batchParams);
}
