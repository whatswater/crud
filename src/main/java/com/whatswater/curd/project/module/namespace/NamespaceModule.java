package com.whatswater.curd.project.module.namespace;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.nothing.data.DbSchemaDataService;
import com.whatswater.nothing.data.SchemaDataService;
import com.whatswater.nothing.property.BasicProperties;
import com.whatswater.nothing.property.Property;
import com.whatswater.nothing.property.Property.PropertyDbConfig;
import com.whatswater.nothing.schema.DbSchema;
import com.whatswater.sql.dialect.MysqlDialect;
import com.whatswater.sql.executor.Executor;
import com.whatswater.sql.table.DbTable;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;

import java.util.Arrays;

public class NamespaceModule implements Module {
    SchemaDataService schemaDataService;
    Router router;

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "dbExecutor");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("dbExecutor".equals(name)) {
            Executor executor = (Executor) obj;
            DbTable<?> dbTable = new DbTable<>(Namespace.class, "q_name");
            DbSchema dbSchema = new DbSchema();
            dbSchema.setDbTable(dbTable);
            dbSchema.setNamespace("l_name");
            dbSchema.setModuleName("system");
            dbSchema.setSchemaName("namespace");
            dbSchema.setTableName("q_namespace");
            dbSchema.setRemark("测试");
            dbSchema.setPrimaryKeyColumnName("id");

            Property<?> idProperty = new Property<>();
            idProperty.setPropertyName("id");
            idProperty.setDataType("long");

            PropertyDbConfig idDbConfig = new Property.PropertyDbConfig();
            idDbConfig.setColumnName("ID");
            idProperty.setDbConfig(idDbConfig);

            Property<?> nameProperty = new Property<>();
            nameProperty.setPropertyName("name");
            nameProperty.setDataType("string");
            PropertyDbConfig nameDbConfig = new Property.PropertyDbConfig();
            nameDbConfig.setColumnName("NAME");
            nameProperty.setDbConfig(nameDbConfig);

            BasicProperties basicProperties = new BasicProperties(
                Arrays.asList(idProperty, nameProperty)
            );
            basicProperties.initProperties();
            dbSchema.setProperties(basicProperties);

            MysqlDialect mysqlDialect = new MysqlDialect();
            schemaDataService = new DbSchemaDataService(dbSchema, mysqlDialect, executor);
            consumer.exportObject("namespaceSchemaDataService", schemaDataService);
            if (router != null) {
                NamespaceRest rest = new NamespaceRest(schemaDataService);
                RestRouter.register(router, rest);
            }
        } else if ("router".equals(name)) {
            router = (Router) obj;
            if (schemaDataService != null) {
                NamespaceRest rest = new NamespaceRest(schemaDataService);
                RestRouter.register(router, rest);
            }
        }
    }
}
