package com.whatswater.sql.dialect;


import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.table.DbTable;

public class PaymentsDbColumn {
    public static DbTable<Payments> dbTable = new DbTable<>(Payments.class);

    public static RawColumnReference id = new RawColumnReference(dbTable, "id");
    public static RawColumnReference userId = new RawColumnReference(dbTable, "user_id");
    public static RawColumnReference money = new RawColumnReference(dbTable, "money");
    public static RawColumnReference createTime = new RawColumnReference(dbTable, "create_time");
}
