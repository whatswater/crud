package com.whatswater.curd.project.sys.employee;


import com.whatswater.sql.executor.SqlSession;
import io.vertx.mysqlclient.MySQLPool;

public class EmployeeService$$Enhance$$ extends EmployeeService {
    private SqlSession sqlSession;

    public EmployeeService$$Enhance$$(MySQLPool pool) {
        super(pool);
    }

    @Override
    public EmployeeService bindNewSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
        return this;
    }

    @Override
    public SqlSession getCurrentSqlSession() {
        return this.sqlSession;
    }
}
