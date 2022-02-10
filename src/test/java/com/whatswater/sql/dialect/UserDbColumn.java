package com.whatswater.sql.dialect;


import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.table.DbTable;

public class UserDbColumn {
    public static DbTable<User> dbTable = new DbTable<>(User.class);

    public static RawColumnReference id = new RawColumnReference(dbTable, "id");
    public static RawColumnReference name = new RawColumnReference(dbTable, "name");
    public static RawColumnReference code = new RawColumnReference(dbTable, "code");
    public static RawColumnReference phone = new RawColumnReference(dbTable, "phone");
    public static RawColumnReference gender = new RawColumnReference(dbTable, "gender");
    public static RawColumnReference email = new RawColumnReference(dbTable, "email");
    public static RawColumnReference status = new RawColumnReference(dbTable, "status");
}
