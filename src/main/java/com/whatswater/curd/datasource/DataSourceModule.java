package com.whatswater.curd.datasource;


import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;

public class DataSourceModule implements Module {
    private JsonObject config;
    private Vertx vertx;
    private MySQLPool pool;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require("init:global", "config", "vertx");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("config".equals(name)) {
            config = (JsonObject) obj;
        } else if ("vertx".equals(name)) {
            vertx = (Vertx) obj;
        }

        if (config != null && vertx != null) {
            MySQLPool pool = createDataSource();
            consumer.exportObject("datasource", pool);
            VertxExecutor vertxExecutor = new VertxExecutor(pool);
            consumer.exportObject("dbExecutor", vertxExecutor);
        }
    }

    public MySQLPool createDataSource() {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
            .setPort(config.getInteger("datasource.connection.port"))
            .setHost(config.getString("datasource.connection.host"))
            .setDatabase(config.getString("datasource.connection.database"))
            .setUser(config.getString("datasource.connection.user"))
            .setPassword(config.getString("datasource.connection.password"))
            .setCharset(config.getString("datasource.connection.encoding"))
            .setCollation(config.getString("datasource.connection.collation"))
            .setSsl(false)
            .setReconnectAttempts(2)
            .setReconnectInterval(1000L)
            .addProperty("serverTimeZone", config.getString("datasource.connection.serverTimeZone"));

        PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(config.getInteger("datasource.pool.maxSize"))
            .setIdleTimeout(config.getInteger("datasource.pool.idleTimeout"));
        return MySQLPool.pool(vertx, connectOptions, poolOptions);
    }
}
