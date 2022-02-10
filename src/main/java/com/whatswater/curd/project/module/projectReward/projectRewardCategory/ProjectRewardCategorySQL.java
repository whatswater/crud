package com.whatswater.curd.project.module.projectReward.projectRewardCategory;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class ProjectRewardCategorySQL extends CommonSQL<ProjectRewardCategory, MySQLPool> {
    public ProjectRewardCategorySQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
