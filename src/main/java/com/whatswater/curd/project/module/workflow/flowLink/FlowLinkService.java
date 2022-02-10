package com.whatswater.curd.project.module.workflow.flowLink;


import com.whatswater.curd.project.common.*;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstant;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstantService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.*;
import java.util.stream.Collectors;

// todo 添加事件事务相关配置
public class FlowLinkService {
    private final FlowLinkSQL flowLinkSQL;
    private FlowLinkConstantService flowLinkConstantService;

    public FlowLinkService(MySQLPool pool) {
        this.flowLinkSQL = new FlowLinkSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<FlowLink>> search(Page page, FlowLinkQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowLinkSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowLinkSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowLink::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowLink> getById(Long flowLinkId) {
        Future<JsonObject> result = flowLinkSQL.selectById(flowLinkId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowLink(json);
        });
    }

    public Future<FlowLinkWithConstant> getWithConstantById(Long flowLinkId) {
        Future<JsonObject> result = flowLinkSQL.selectById(flowLinkId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowLink(json);
        }).compose(this::withConstant);
    }


    public Future<List<FlowLink>> listByIds(List<Long> flowLinkIds) {
        return flowLinkSQL.selectAll(FlowLink.idListSqlAssist(flowLinkIds)).map(list -> {
            if (list == null || list.isEmpty()) {
                return Collections.emptyList();
            }
            return list.stream().map(FlowLink::new).collect(Collectors.toList());
        });
    }

    public Future<Long> insert(FlowLink flowLink) {
        return flowLinkSQL.insertNonEmptyGeneratedKeys(flowLink, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(FlowLink flowLink) {
        return flowLinkSQL.updateNonEmptyById(flowLink);
    }

    /**
     * 获取某个流程开始环节的配置
     * @param flowDefinitionId 环节定义Id
     * @return 开始环节的配置
     */
    public Future<FlowLinkWithConstant> getFlowStartLink(Long flowDefinitionId) {
        SqlAssist sqlAssist = FlowLink.definitionIdLinkCodeSqlAssist(flowDefinitionId, FlowLink.LINK_CODE_START);
        return flowLinkSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return new FlowLink(list.get(0));
        }).compose(this::withConstant);
    }

    public Future<FlowLinkWithConstant> withConstant(FlowLink flowLink) {
        Future<List<FlowLinkConstant>> configListFuture = flowLinkConstantService.queryConstantOfLink(flowLink.getId());
        return configListFuture.map(flowLinkConstants -> {
            if (flowLinkConstants == null) {
                return FlowLinkWithConstant.from(flowLink);
            }
            Map<String, FlowLinkConstant> configMap = new TreeMap<>();
            for (FlowLinkConstant flowLinkConstant: flowLinkConstants) {
                configMap.put(flowLinkConstant.getConstantName(), flowLinkConstant);
            }
            return FlowLinkWithConstant.from(flowLink, configMap);
        });
    }

    public Future<FlowLinkWithConstant> ensureConstant(FlowLinkWithConstant flowLinkWithConstant) {
        return Future.succeededFuture();
    }

    public void setFlowLinkConstantService(FlowLinkConstantService flowLinkConstantService) {
        this.flowLinkConstantService = flowLinkConstantService;
    }
}
