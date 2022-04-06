package com.whatswater.sql.dialect;

import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.dialect.Dialect.SQL;
import com.whatswater.sql.expression.FunctionExpression;
import com.whatswater.sql.expression.literal.NumberLiteral;
import com.whatswater.sql.expression.literal.StringValue;
import com.whatswater.sql.statement.*;
import com.whatswater.sql.table.AliasTable;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.Table;
import com.whatswater.sql.utils.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.junit.Assert;
import org.junit.Test;

// todo 编写更多的单元测试代码
public class MysqlDialectTest {
    @Test
    public void testUpdateOneTable() {
        DbTable<User> dbTable = UserDbColumn.dbTable.newAlias();
        Update update = new Update(dbTable);
        update.set(UserDbColumn.code, "1001")
            .set(UserDbColumn.email, "1522961253@qq.com")
            .set(UserDbColumn.name, "fanxiao")
            .set(UserDbColumn.phone, "15527087413")
            .set(UserDbColumn.status, 1)
            .where(UserDbColumn.id.eq(1L));

        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(update);

        Assert.assertEquals("update user a set a.code = ?,a.email = ?,a.name = ?,a.phone = ?,a.status = ? where a.id = ?", sql.getSql().toString());
        for (Object param: sql.getParams()) {
            System.out.println(param);
        }
    }

    @Test
    public void testUpdateJoinedTable() {
        DbTable<User> userTable = UserDbColumn.dbTable.newAlias();
        DbTable<Payments> paymentsTable = PaymentsDbColumn.dbTable.newAlias();

        FunctionExpression functionExpression = new FunctionExpression("count", PaymentsDbColumn.id);
        Update update = new Update(userTable);
        update.leftJoin(paymentsTable, PaymentsDbColumn.userId.eq(UserDbColumn.id))
            .set(UserDbColumn.status, functionExpression)
            .where(UserDbColumn.code.eq("1001"));
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(update);
        System.out.println(sql.getSql().toString());
        for (Object param: sql.getParams()) {
            System.out.println(param);
        }
    }

    @Test
    public void testUpdateLimitTable() {
        DbTable<User> userTable = UserDbColumn.dbTable.newAlias();
        DbTable<Payments> paymentsTable = PaymentsDbColumn.dbTable.newAlias();

        FunctionExpression functionExpression = new FunctionExpression("count", PaymentsDbColumn.id);
        Update update = new Update(userTable);
        update.leftJoin(paymentsTable, PaymentsDbColumn.userId.eq(UserDbColumn.id))
            .set(UserDbColumn.status, functionExpression)
            .where(UserDbColumn.code.eq("1001"))
            .limit(1);
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(update);
        System.out.println(sql.getSql().toString());
        for (Object param: sql.getParams()) {
            System.out.println(param);
        }
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setCode("0100092");
        user.setName("ACS");
        user.setStatus(1);
        Insert<User> insert = new Insert<>(user, UserDbColumn.dbTable);
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(insert);
        System.out.println(sql.getSql().toString());
        for (Object param: sql.getParams()) {
            System.out.println(param);
        }
    }

    @Test
    public void testDelete() {
        Delete delete = new Delete(UserDbColumn.dbTable.newAlias());
        delete.where(UserDbColumn.code.eq("1001").and(UserDbColumn.status.eq(1)));
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(delete);
        System.out.println(sql.getSql().toString());
        for (Object param: sql.getParams()) {
            System.out.println(param);
        }
    }

    @Test
    public void testDbQuery() {
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(UserDbColumn.dbTable.newAlias());

        Assert.assertEquals("select * from user a", sql.getSql().toString());
        Assert.assertTrue(CollectionUtils.isEmpty(sql.getParams()));
    }

    @Test
    public void testSimpleQuery() {
        Table table = UserDbColumn.dbTable
            .where(UserDbColumn.code.eq("1001").and(UserDbColumn.status.eq(1)))
            .orderBy(UserDbColumn.code)
            .select(UserDbColumn.id, UserDbColumn.code)
            .limit(10);
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(table);

        System.out.println(sql.getSql().toString());
    }

    @Test
    public void testJoinedTable() {
        AliasTable<?> table2 = ((AliasTable<?>) UserDbColumn.dbTable
            .where(UserDbColumn.code.eq("1001").and(UserDbColumn.status.eq(1)))
            .orderBy(UserDbColumn.code)
            .select(UserDbColumn.id, UserDbColumn.code));

        AliasTable<?> table1 = ((AliasTable<?>) UserDbColumn.dbTable
            .where(UserDbColumn.code.eq("1001").and(UserDbColumn.status.eq(1)))
            .orderBy(UserDbColumn.code)
            .select(UserDbColumn.id, UserDbColumn.code));

        Table table = table2.join(table1, table1.columnReference(UserDbColumn.code.getColumnName())
            .eq(table2.columnReference(UserDbColumn.code.getColumnName())));
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(table);

        System.out.println(sql.getSql().toString());
    }

    @Test
    public void testComplexTable() {
        AliasTable<?> table2 = ((AliasTable<?>) UserDbColumn.dbTable
            .where(UserDbColumn.code.eq("1001").and(UserDbColumn.status.eq(1)))
            .orderBy(UserDbColumn.code)
            .select(UserDbColumn.id, UserDbColumn.code));

        AliasTable<?> table1 = ((AliasTable<?>) UserDbColumn.dbTable
            .where(UserDbColumn.code.eq("1001").and(UserDbColumn.status.eq(1)))
            .orderBy(UserDbColumn.code)
            .select(UserDbColumn.id, UserDbColumn.code));

        Table table = table2.join(table1, table1.columnReference(UserDbColumn.code.getColumnName())
            .eq(table2.columnReference(UserDbColumn.code.getColumnName())))
            .where(new NumberLiteral(1).eq(new StringValue("1")));
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(table);

        System.out.println(sql.getSql().toString());
    }

    @Test
    public void testComplexTable2() {
        SelectColumn countColumn = new Alias(new FunctionExpression("count", UserDbColumn.id), new AliasPlaceholder());

        Table table = UserDbColumn.dbTable
            .where(UserDbColumn.status.eq(1)).groupBy(UserDbColumn.gender)
            .select(UserDbColumn.phone.like("155%"), countColumn);
        MysqlDialect dialect = new MysqlDialect();
        SQL sql = dialect.toSql(table);

        System.out.println(sql.getSql().toString());
    }
}
