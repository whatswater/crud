package com.whatswater.orm.dsl;

import com.whatswater.orm.dsl.criteria.QueryParam;
import com.whatswater.orm.schema.Schema;
import com.whatswater.sql.mapper.ResultMapper;

public class QueryDSL<Q> {
    // 结果集合转换器
    private ResultMapper<Q> resultMapper;
    // 查询的schema
    private Schema schema;
    // 查询条件，修改为树形结构
    QueryParam queryParam;

    public ResultMapper<Q> getResultMapper() {
        return resultMapper;
    }

    public void setResultMapper(ResultMapper<Q> resultMapper) {
        this.resultMapper = resultMapper;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }
}
