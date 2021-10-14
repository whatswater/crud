package com.whatswater.curd;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Starter {
    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions();
        JsonObject config = new JsonObject();

        config.put("http.port", 8080);
        config.put("http.host", "0.0.0.0");
        config.put("datasource.pool.maxSize", 10);
        config.put("datasource.pool.idleTimeout", 300);
        config.put("datasource.connection.port", 3306);
        config.put("datasource.connection.host", "localhost");
        config.put("datasource.connection.database", "crud");
        config.put("datasource.connection.user", "root");
        config.put("datasource.connection.password", "0000000qe");
        config.put("datasource.connection.encode", "utf8mb4");
        config.put("datasource.connection.collation", "utf8mb4_general_ci");
        config.put("datasource.connection.serverTimeZone", "Asia/Shanghai");

        options.setConfig(config);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), options);
    }
}
