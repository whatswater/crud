package com.whatswater.curd.project.module.workflow.flowDefinition;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;


@Table("w_flow_definition_graph")
public class FlowDefinitionGraph {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_DEFINITION_ID = "flow_definition_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_X6_JSON = "x6_json";


    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_DEFINITION_ID)
    Long flowDefinitionId;
    @TableColumn(COLUMN_CONTENT)
    String content;
    @TableColumn(COLUMN_X6_JSON)
    String x6Json;

    public FlowDefinitionGraph() {
    }

    public FlowDefinitionGraph(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowDefinitionId = json.getLong(COLUMN_FLOW_DEFINITION_ID);
        this.content = json.getString(COLUMN_CONTENT);
        this.x6Json = json.getString(COLUMN_X6_JSON);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlowDefinitionId() {
        return flowDefinitionId;
    }

    public void setFlowDefinitionId(Long flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getX6Json() {
        return x6Json;
    }

    public void setX6Json(String x6Json) {
        this.x6Json = x6Json;
    }

    public static SqlAssist flowDefinitionIdSqlAssist(long flowDefinitionId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_DEFINITION_ID, flowDefinitionId);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }
}
