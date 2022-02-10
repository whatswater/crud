package com.whatswater.curd.project.module.projectReward.projectRewardApply;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class ProjectRewardApplySQL extends CommonSQL<ProjectRewardApply, MySQLPool> {
    public ProjectRewardApplySQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
