package com.whatswater.curd.project.sys.organization;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.projectReward.projectRewardApply.ProjectRewardApply;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLink;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizationService {
    private final OrganizationSQL organizationSQL;

    public OrganizationService(MySQLPool pool) {
        this.organizationSQL = new OrganizationSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<Organization>> search(Page page, OrganizationQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return organizationSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return organizationSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(Organization::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<Organization> getById(Long organizationId) {
        Future<JsonObject> result = organizationSQL.selectById(organizationId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            Organization organization = new Organization(json);
            return organization;
        });
    }

    public Future<List<Organization>> listByCode(List<String> organizationCodeList) {
        SqlAssist sqlAssist = Organization.codeListSqlAssist(organizationCodeList);
        return organizationSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(Organization::new).collect(Collectors.toList());
        });
    }

    public Future<List<Organization>> listTopLevelOrganization() {
        SqlAssist sqlAssist = Organization.topLevelSqlAssist();
        return organizationSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(Organization::new).collect(Collectors.toList());
        });
    }

    public Future<Organization> getTopLevelOrganization(long organizationId) {
        return this.getById(organizationId).compose(organization -> {
            if (organization.getLevel() == 1) {
                return Future.succeededFuture(organization);
            }
            String path = organization.getPath();
            long topLevelOrganizationId = Long.parseLong(path.split(CrudConst.PATH_SPLIT_CHAR)[1]);
            return this.getById(topLevelOrganizationId);
        });
    }

    public Future<List<Organization>> listByIds(List<Long> organizationIds) {
        return organizationSQL.selectAll(Organization.idListSqlAssist(organizationIds)).map(list -> {
            if (list == null || list.isEmpty()) {
                return Collections.emptyList();
            }
            return list.stream().map(Organization::new).collect(Collectors.toList());
        });
    }

    public Future<Organization> getByCode(Long organizationId) {
        Future<JsonObject> result = organizationSQL.selectById(organizationId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new Organization(json);
        });
    }

    /**
     * 查询子部门
     * @param parentId 部门Id
     * @return 子部门列表
     */
    public Future<List<OrganizationName>> queryChildrenName(long parentId) {
        SqlAssist sqlAssist = Organization.parentIdSqlAssist(parentId);
       return  organizationSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }

            return jsonList.stream().map(Organization::new).map(OrganizationName::fromOrganization).collect(Collectors.toList());
        });
    }

    /**
     * 查询子部门个数
     * @param parentId 部门Id
     * @return 子部门个数
     */
    public Future<Boolean> existsChildren(long parentId) {
        SqlAssist sqlAssist = Organization.parentIdSqlAssist(parentId);
        return organizationSQL.getCount(sqlAssist).map(value -> value != null && value > 0L);
    }


    private Future<Boolean> exists(String code, String name) {
        SqlAssist sqlAssist = Organization.codeOrNameLimitOneSqlAssist(code, name);
        return organizationSQL.getCount(sqlAssist).map(value -> value != null && value > 0L);
    }

    private Future<Integer> syncLeaf(long organizationId) {
        if (organizationId == 0L) {
            return Future.succeededFuture(0);
        }
        return existsChildren(organizationId).compose(r -> {
            Organization organization = new Organization();
            organization.setId(organizationId);
            organization.setLeaf(!r);
            return update(organization);
        });
    }

    public Future<Long> fillAndInsertWithCheck(long parentId, OrganizationName organizationName) {
        return exists(organizationName.getOrganizationCode(), organizationName.getOrganizationName()).compose(exists -> {
            if (exists) {
                return Future.failedFuture("系统中存在与当前部门编码或名称完全一致的部门");
            }
            return Future.succeededFuture();
        }).compose(r -> this.getById(parentId)).compose(parent -> {
            Organization organization = new Organization();
            organization.setOrganizationCode(organizationName.getOrganizationCode());
            organization.setOrganizationName(organizationName.getOrganizationName());
            organization.setLeaf(true);
            organization.setParentId(parent == null ? 0 : parent.getId());
            organization.setLevel(parent == null ? 1 : parent.getLevel() + 1);
            organization.setPath(parent == null ? StrUtil.EMPTY : parent.getPath() + CrudConst.PATH_SPLIT_CHAR + parent.getId());

            return insert(organization);
        }).compose(r -> this.syncLeaf(parentId).map(r));
    }

    public Future<Integer> deleteWithCheck(long id) {
        return getById(id).compose(org -> {
            if (org == null) {
                return Future.failedFuture("不存在此部门");
            }
            return existsChildren(id).compose(r -> {
                if (r) {
                    return Future.failedFuture("当前部门存在子部门，请先删除子部门");
                }
                return delete(id);
            }).compose(r -> this.syncLeaf(org.getParentId()).map(r));
        });
    }

    public Future<Long> insert(Organization organization) {
        return organizationSQL.insertNonEmptyGeneratedKeys(organization, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> updateWithCheck(OrganizationName organization) {
        return exists(organization.getOrganizationCode(), organization.getOrganizationName()).compose(exists -> {
            if (exists) {
                return Future.failedFuture("系统中存在与当前部门编码或名称完全一致的部门");
            }
            return Future.succeededFuture();
        }).compose(r -> {
            Organization organizationUpdate = new Organization();
            organizationUpdate.setId(organization.getId());
            organizationUpdate.setOrganizationName(organization.getOrganizationName());
            return this.update(organizationUpdate);
        });
    }

    public Future<Integer> update(Organization organization) {
        return organizationSQL.updateNonEmptyById(organization);
    }

    public Future<Integer> delete(long id) {
        return organizationSQL.deleteById(id);
    }
}
