package com.whatswater.curd.project.module.workflow.flowLinkConstant;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.List;
import java.util.stream.Collectors;

public class FlowLinkConstantService {
    private final FlowLinkConstantSQL flowLinkConstantSQL;

    public FlowLinkConstantService(MySQLPool pool) {
        this.flowLinkConstantSQL = new FlowLinkConstantSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<FlowLinkConstant>> search(Page page, FlowLinkConstantQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowLinkConstantSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowLinkConstantSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowLinkConstant::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowLinkConstant> getById(Long flowLinkConstantId) {
        Future<JsonObject> result = flowLinkConstantSQL.selectById(flowLinkConstantId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowLinkConstant(json);
        });
    }

    public Future<Long> insert(FlowLinkConstant flowLinkConstant) {
        return flowLinkConstantSQL.insertNonEmptyGeneratedKeys(flowLinkConstant, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(FlowLinkConstant flowLinkConstant) {
        return flowLinkConstantSQL.updateNonEmptyById(flowLinkConstant);
    }

    /**
     * 根据linkId获取环节所有的配置
     * @param linkId 环节Id
     * @return 环节列表
     */
    public Future<List<FlowLinkConstant>> queryConstantOfLink(long linkId) {
        SqlAssist sqlAssist = FlowLinkConstant.linkIdSqlAssist(linkId);
        return flowLinkConstantSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return list.stream().map(FlowLinkConstant::new).collect(Collectors.toList());
        });
    }
}
