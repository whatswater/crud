package com.whatswater.curd.project.user;


import com.whatswater.sql.dialect.Dialect.SQL;
import com.whatswater.sql.dialect.MysqlDialect;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.statement.Delete;
import com.whatswater.sql.statement.Insert;
import com.whatswater.sql.statement.Query;
import com.whatswater.sql.statement.Update;
import com.whatswater.sql.table.ComplexTable;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.Table;
import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;

public class UserRepository {
    public static final DbTable<User> dbTable = new DbTable<>(User.class);

    public static final RawColumnReference id = new RawColumnReference(dbTable, "id");
    public static final RawColumnReference name = new RawColumnReference(dbTable, "name");
    public static final RawColumnReference deleted = new RawColumnReference(dbTable, "deleted");


    private final MySQLPool pool;
    private final MysqlDialect dialect;

    public UserRepository(MySQLPool pool) {
        this.pool = pool;
        this.dialect = new MysqlDialect();
    }

    public Insert<User> insert(String name, long id) {
        User user = new User();
        user.setName(name);
        user.setId(id);
        return dbTable.toInsert(user);
    }

    public Table nameCountGroup() {
        Table inner = dbTable.groupBy(name).select(id.count().gt(2), name, id.count().as("cnt"));
        return new ComplexTable(inner);
    }

    public static BoolExpression idEq(long val) {
        return id.eq(val);
    }

    public static BoolExpression nameLike(String val) {
        return name.like(val);
    }

    // 当innerTable为DbTable类型时，需重定向表格
    public Future<User> getById(long id) {
        DbTable<?> newTable = dbTable.newAlias();
        Query<?> query = newTable.select(UserRepository.id.bindNewTable(newTable), UserRepository.name.bindNewTable(newTable))
            .where(UserRepository.id.bindNewTable(newTable).eq(id)).limit(1).toQuery(null);
        SQL sql = dialect.toSql(query);
        return execute(sql).map(rows -> {
            if (rows.size() == 0) {
                return new User();
            }

            RowIterator<Row> rowIterator = rows.iterator();
            Row row = rowIterator.next();

            User user = new User();
            user.setId(row.getLong(UserRepository.id.getAliasOrColumnName()));
            user.setName(row.getString(UserRepository.name.getAliasOrColumnName()));
            return user;
        });
    }

    public Future<Integer> updateById(final User user) {
        Update update = dbTable.newAlias().toUpdate();
        update.set(name, user.getName());
        update.where(id.eq(user.getId()));

        SQL sql = dialect.toSql(update);
        return execute(sql).map(SqlResult::size);
    }

    public Future<Long> insert(final User user) {
        Insert<User> insert = dbTable.toInsert(user);
        SQL sql = dialect.toSql(insert);
        return execute(sql).map(rows -> rows.property(MySQLClient.LAST_INSERTED_ID));
    }

    public Future<Integer> deleteById(long id) {
        Delete delete = dbTable.newAlias().toDelete(idEq(id));
        SQL sql = dialect.toSql(delete);

        return execute(sql).map(SqlResult::size);
    }

    public Future<RowSet<Row>> execute(SQL sql) {
        return pool.withConnection(client -> client
            .preparedQuery(sql.getSql())
            .execute(Tuple.tuple(sql.getParams()))
        );
    }
}
