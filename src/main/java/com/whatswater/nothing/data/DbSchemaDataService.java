package com.whatswater.nothing.data;


import com.whatswater.nothing.property.Properties;
import com.whatswater.nothing.property.Property;
import com.whatswater.nothing.schema.DbSchema;
import com.whatswater.sql.dialect.Dialect;
import com.whatswater.sql.dialect.Dialect.SQL;
import com.whatswater.sql.executor.Executor;
import com.whatswater.sql.table.Table;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbSchemaDataService implements SchemaDataService {
    DbSchema dbSchema;
    Dialect dialect;
    Executor executor;

    public DbSchemaDataService(DbSchema dbSchema, Dialect dialect, Executor executor) {
        this.dbSchema = dbSchema;
        this.dialect = dialect;
        this.executor = executor;
    }

    @Override
    public Future<ModelDataList> list(Map<String, Object> params) {
        Table table = dbSchema.toQueryParamTable(params);
        Properties properties = dbSchema.getProperties();

        SQL sql = dialect.toSql(table);
        return executor.query(sql).map(rows -> {
            ModelDataList modelDataList = new ModelDataList(properties);
            List<Object[]> dataList = new ArrayList<>(rows.size());
            for (Row row: rows) {
                Object[] data = new Object[properties.properties().size()];
                for (int i = 0; i < properties.properties().size(); i++) {
                    Property<?> property = properties.properties().get(i);
                    String dataType = property.getDataType();
//                    data[i] = row.get(String.class, i + 1);
                    data[i] = row.getValue(i + 1);
                }
                dataList.add(data);
            }
            modelDataList.setDataList(dataList);
            return modelDataList;
        });
    }

    @Override
    public ModelData getOne(Map<String, Object> params) {
        return null;
    }

    @Override
    public ModelData getByPrimaryKey(Serializable primaryKey) {
        return null;
    }

    @Override
    public ModelData page(Page page, Map<String, Object> params) {
        return null;
    }
}
