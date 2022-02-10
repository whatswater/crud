package com.whatswater.curd.project.module.projectReward.projectRewardItem;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.Tuple2;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.projectReward.projectRewardCategory.ProjectRewardCategory;
import com.whatswater.curd.project.module.projectReward.projectRewardCategory.ProjectRewardCategoryService;
import com.whatswater.curd.project.sys.organization.Organization;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectRewardItemService {
    private ProjectRewardItemSQL projectRewardItemSQL;
    private ProjectRewardCategoryService projectRewardCategoryService;
    private OrganizationService organizationService;

    public ProjectRewardItemService() {
    }

    public Future<PageResult<ProjectRewardItemVoOfList>> search(Page page, ProjectRewardItemQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return projectRewardItemSQL.getCount(sqlAssist).<PageResult<ProjectRewardItem>>compose(total -> {
            if (CrudUtils.notZero(total)) {
                return projectRewardItemSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(ProjectRewardItem::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        }).compose(pageResult -> {
            if (!CrudUtils.notZero(pageResult.getTotal())) {
                return Future.succeededFuture(PageResult.empty());
            }
            List<ProjectRewardItem> dataList = pageResult.getData();
            List<Long> contentIds = dataList.stream().map(ProjectRewardItem::getCategoryId).distinct().collect(Collectors.toList());
            return projectRewardCategoryService.listByIds(contentIds).compose(contentList -> {
                List<Long> categoryIds = contentList.stream().map(ProjectRewardCategory::getParentId).distinct().collect(Collectors.toList());
                return projectRewardCategoryService
                    .listByIds(categoryIds)
                    .map(categoryList -> {
                        List<ProjectRewardItemVoOfList> voList = new ArrayList<>(dataList.size());
                        for (ProjectRewardItem item: dataList) {
                            ProjectRewardItemVoOfList vo = ProjectRewardItemVoOfList.fromProjectRewardItem(item);
                            Long categoryId = item.getCategoryId();
                            for (ProjectRewardCategory content: contentList) {
                                if (content.getId().equals(categoryId)) {
                                    vo.setRewardContent(content.getCategory());
                                    for (ProjectRewardCategory category: categoryList) {
                                        if (content.getParentId().equals(category.getId())) {
                                            vo.setCategory(category.getCategory());
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            voList.add(vo);
                        }
                        return PageResult.of(voList, pageResult);
                    });
            });
        });
    }

    public Future<ProjectRewardItem> getById(Long projectRewardItemId) {
        Future<JsonObject> result = projectRewardItemSQL.selectById(projectRewardItemId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new ProjectRewardItem(json);
        });
    }

    public Future<Long> fillAndInsertWithCheck(ProjectRewardItem projectRewardItem) {
        Long categoryId = projectRewardItem.getCategoryId();
        return projectRewardCategoryService.getById(categoryId).compose(category -> {
            if (category == null) {
                return Future.failedFuture("系统中不存在此激励内容");
            }
            projectRewardItem.setCreateTime(LocalDateTime.now());
            projectRewardItem.setYear(category.getYear());
            if (StrUtil.isEmpty(projectRewardItem.allocDepartmentIds)) {
                return insert(projectRewardItem);
            }

            List<Long> orgIds = Arrays.stream(projectRewardItem.allocDepartmentIds.split(StrUtil.COMMA)).map(Long::parseLong).collect(Collectors.toList());
            return organizationService.listByIds(orgIds).compose(orgList -> {
                String names = orgList.stream().map(Organization::getOrganizationName).collect(Collectors.joining(StrUtil.COMMA));
                projectRewardItem.setAllocDepartmentNames(names);
                return insert(projectRewardItem);
            });
        });
    }

    public Future<Long> insert(ProjectRewardItem projectRewardItem) {
        return projectRewardItemSQL.insertNonEmptyGeneratedKeys(projectRewardItem, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> updateWithCheck(ProjectRewardItem projectRewardItem) {
        return getById(projectRewardItem.getId()).compose(dbItem -> {
            if (dbItem == null) {
                return Future.failedFuture("修改失败，当前系统不存在此数据");
            }

            ProjectRewardItem update = new ProjectRewardItem();
            update.setId(projectRewardItem.getId());
            update.setItemName(projectRewardItem.getItemName());
            update.setStandard(projectRewardItem.getStandard());
            if (StrUtil.isEmpty(projectRewardItem.getAllocDepartmentIds())) {
                update.setAllocDepartmentIds(projectRewardItem.getAllocDepartmentIds());
                update.setAllocDepartmentNames(StrUtil.EMPTY);
                return update(update);
            }

            List<Long> orgIds = Arrays.stream(projectRewardItem.getAllocDepartmentIds().split(StrUtil.COMMA)).map(Long::parseLong).collect(Collectors.toList());
            return organizationService.listByIds(orgIds).compose(orgList -> {
                String names = orgList.stream().map(Organization::getOrganizationName).collect(Collectors.joining(StrUtil.COMMA));

                update.setAllocDepartmentIds(projectRewardItem.getAllocDepartmentIds());
                update.setAllocDepartmentNames(names);
                return update(update);
            });
        });
    }

    public Future<Integer> update(ProjectRewardItem projectRewardItem) {
        return projectRewardItemSQL.updateNonEmptyById(projectRewardItem);
    }

    public Future<Integer> deleteWithCheck(Long projectRewardItemId) {
        return getById(projectRewardItemId).compose(dbItem -> {
            if (dbItem == null) {
                return Future.failedFuture("删除失败，当前系统不存在此数据");
            }
            return projectRewardItemSQL.deleteById(projectRewardItemId);
        });
    }

    public Future<Integer> delete(Long projectRewardItemId) {
        return projectRewardItemSQL.deleteById(projectRewardItemId);
    }

    public void setProjectRewardItemSQL(ProjectRewardItemSQL projectRewardItemSQL) {
        this.projectRewardItemSQL = projectRewardItemSQL;
    }

    public void setProjectRewardCategoryService(ProjectRewardCategoryService projectRewardCategoryService) {
        this.projectRewardCategoryService = projectRewardCategoryService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
}
