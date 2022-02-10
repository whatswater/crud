package com.whatswater.curd.project.module.projectReward.projectRewardCategory;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectRewardCategoryService {
    private final ProjectRewardCategorySQL projectRewardCategorySQL;

    public ProjectRewardCategoryService(MySQLPool pool) {
        this.projectRewardCategorySQL = new ProjectRewardCategorySQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<ProjectRewardCategory>> search(Page page, ProjectRewardCategoryQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return projectRewardCategorySQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return projectRewardCategorySQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(ProjectRewardCategory::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<ProjectRewardCategory> getById(Long projectRewardCategoryId) {
        Future<JsonObject> result = projectRewardCategorySQL.selectById(projectRewardCategoryId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new ProjectRewardCategory(json);
        });
    }

    public Future<List<ProjectRewardCategory>> listByIds(List<Long> projectRewardCategoryIds) {
        SqlAssist sqlAssist = ProjectRewardCategory.idListSqlAssist(projectRewardCategoryIds);
        return projectRewardCategorySQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }

            return jsonList.stream().map(ProjectRewardCategory::new).collect(Collectors.toList());
        });
    }

    public Future<ProjectRewardCategory> getByCategoryYear(String category, Integer year) {
        SqlAssist sqlAssist = ProjectRewardCategory.categoryYearSqlAssist(category, year);
        return projectRewardCategorySQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return null;
            }
            return new ProjectRewardCategory(jsonList.get(0));
        });
    }

    public Future<List<ProjectRewardCategory>> queryChildren(long parentId) {
        SqlAssist sqlAssist = ProjectRewardCategory.parentIdSqlAssist(parentId);
        return projectRewardCategorySQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(ProjectRewardCategory::new).collect(Collectors.toList());
        });
    }

    public Future<List<ProjectRewardCategory>> queryChildren(List<Long> parentIdList) {
        SqlAssist sqlAssist = ProjectRewardCategory.parentIdListSqlAssist(parentIdList);
        return projectRewardCategorySQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(ProjectRewardCategory::new).collect(Collectors.toList());
        });
    }

    public Future<Long> queryChildrenCount(long parentId) {
        SqlAssist sqlAssist = ProjectRewardCategory.parentIdSqlAssist(parentId);
        return projectRewardCategorySQL.getCount(sqlAssist);
    }


    public Future<Long> fillAndInsertWithCheck(ProjectRewardCategory projectRewardCategory) {
        projectRewardCategory.setCreateTime(LocalDateTime.now());
        return getByCategoryYear(projectRewardCategory.getCategory(), projectRewardCategory.getYear())
            .compose(category -> {
                if (category != null) {
                    return Future.failedFuture("已经存在相同类别的专项类别");
                }
                return projectRewardCategorySQL.insertNonEmptyGeneratedKeys(projectRewardCategory, MySQLClient.LAST_INSERTED_ID);
            });
    }


    public Future<Integer> updateWithCheck(ProjectRewardCategory projectRewardCategory) {
        return getById(projectRewardCategory.getId()).compose(dbCategory -> {
            if (dbCategory == null) {
                return Future.failedFuture("更新失败，不存在此专项类别");
            }

            ProjectRewardCategory update = new ProjectRewardCategory();
            update.setId(projectRewardCategory.getId());
            update.setCategory(projectRewardCategory.getCategory());
            return projectRewardCategorySQL.updateNonEmptyById(update);
        });
    }

    public Future<Integer> deleteWithCheck(long projectRewardCategoryId) {
        return getById(projectRewardCategoryId).compose(dbCategory -> {
            if (dbCategory == null) {
                return Future.failedFuture("删除失败，此专项类别可能已经被删除");
            }
            return queryChildrenCount(projectRewardCategoryId);
        }).compose(childrenCount -> {
            if (childrenCount != null && childrenCount > 0L) {
                return Future.failedFuture("删除失败，此专项类别存在激励内容");
            }

            return delete(projectRewardCategoryId);
        });
    }


    public Future<Long> insert(ProjectRewardCategory projectRewardCategory) {
        return projectRewardCategorySQL.insertNonEmptyGeneratedKeys(projectRewardCategory, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(ProjectRewardCategory projectRewardCategory) {
        return projectRewardCategorySQL.updateNonEmptyById(projectRewardCategory);
    }

    public Future<Integer> delete(long projectRewardCategoryId) {
        return projectRewardCategorySQL.deleteById(projectRewardCategoryId);
    }
}
