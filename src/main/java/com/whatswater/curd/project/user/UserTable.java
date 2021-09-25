package com.whatswater.curd.project.user;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.reference.RawColumnRef;
import com.whatswater.sql.table.DbTable;

public class UserTable {
    public static final DbTable<User> dbTable = new DbTable<>(User.class);

    public static final RawColumnRef id = new RawColumnRef(dbTable, "id");
    public static final RawColumnRef name = new RawColumnRef(dbTable, "name");

    public static BoolExpression idEq(long val) {
        return id.eq(val);
    }

    public static BoolExpression nameLike(String val) {
        return name.like(val);
    }

}
