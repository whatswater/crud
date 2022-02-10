package com.whatswater.curd.project.module.annualTask;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.sys.organization.Organization;
import com.whatswater.curd.project.sys.organization.OrganizationQuery;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.stream.Collectors;

public class AnnualTaskService {
    private final AnnualTaskSQL annualTaskSQL;

    public AnnualTaskService(MySQLPool pool) {
        this.annualTaskSQL = new AnnualTaskSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<AnnualTask>> search(Page page, AnnualTaskQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return annualTaskSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return annualTaskSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(AnnualTask::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<AnnualTask> getById(Long annualTaskId) {
        Future<JsonObject> result = annualTaskSQL.selectById(annualTaskId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            AnnualTask annualTask = new AnnualTask(json);
            return annualTask;
        });
    }

    public Future<Long> insert(AnnualTask annualTask) {
        annualTask.setLevel(1);
        annualTask.setParentId(0L);
        return annualTaskSQL.insertNonEmptyGeneratedKeys(annualTask, MySQLClient.LAST_INSERTED_ID);
    }

    private Future<Integer> updateStatus(Long id, AnnualTaskStatus status) {
        AnnualTask update = new AnnualTask();
        update.setStatus(status.getId());
        update.setId(id);

        return annualTaskSQL.updateNonEmptyById(update);
    }

    public Future<Long> update(AnnualTask update) {
        Long id = update.getId();
        return getById(id)
            .compose(annualTask -> {
                if (!annualTask.canUpdate()) {
                    return Future.failedFuture("当前年度工作任务不允许更新");
                }

                return updateStatus(annualTask.getId(), AnnualTaskStatus.OVERRIDE).compose(cnt -> {
                    AnnualTask newTask = new AnnualTask();
                    newTask.setContent(update.getContent());
                    newTask.setGoal(update.getGoal());
                    newTask.setMakeDate(update.getMakeDate());
                    newTask.setNo(update.getNo());
                    newTask.setVersionNo(annualTask.getVersionNo() + 1);
                    newTask.setStatus(annualTask.getStatus());
                    newTask.setYear(annualTask.getYear());
                    newTask.setLevel(annualTask.getLevel());
                    newTask.setParentId(annualTask.getParentId());
                    newTask.setSourceType(annualTask.getSourceType());

                    return insert(newTask);
                });
            });
    }

    public Future<Integer> delete(Long annualTaskId) {
        return getById(annualTaskId).compose(annualTask -> {
            if (!annualTask.canUpdate()) {
                return Future.failedFuture("当前年度工作任务不允许删除");
            }

            SqlAssist sqlAssist = AnnualTask.idSqlAssist(annualTaskId);
            return annualTaskSQL.deleteByAssist(sqlAssist);
        });
    }
}
