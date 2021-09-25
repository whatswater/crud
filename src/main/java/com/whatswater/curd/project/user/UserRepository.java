package com.whatswater.curd.project.user;


import com.whatswater.sql.statement.Update;
import com.whatswater.sql.table.Table;
import io.vertx.mysqlclient.MySQLPool;

public class UserRepository {
    private final MySQLPool pool;

    public UserRepository(MySQLPool pool) {
        this.pool = pool;
    }

    public Table userTable() {
        return UserTable.dbTable.where(UserTable.idEq(1));
    }

    public Update updateName(String name, long id) {
        return UserTable.dbTable.toUpdate().set(UserTable.name, name).where(UserTable.idEq(id));
    }

    public Table nameCountGroup() {
        return UserTable.dbTable.groupBy(UserTable.name).select(UserTable.id);
    }
}
