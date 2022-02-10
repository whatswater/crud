package com.whatswater.curd.project.module.projectReward.projectRewardItem;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class ProjectRewardItemSQL extends CommonSQL<ProjectRewardItem, MySQLPool> {
    public ProjectRewardItemSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
