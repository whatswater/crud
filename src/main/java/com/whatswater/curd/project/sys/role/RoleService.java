package com.whatswater.curd.project.sys.role;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employeeRole.EmployeeRoleService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoleService {
    private RoleSQL roleSQL;
    private EmployeeRoleService employeeRoleService;

    public RoleService() {

    }

    public Future<PageResult<Role>> search(Page page, RoleQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return roleSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return roleSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(Role::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<Role> getById(Long roleId) {
        Future<JsonObject> result = roleSQL.selectById(roleId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new Role(json);
        });
    }

    public Future<List<Role>> listByIds(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Future.succeededFuture(Collections.emptyList());
        }
        SqlAssist sqlAssist = Role.idListSqlAssist(ids);
        return roleSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(Role::new).collect(Collectors.toList());
        });
    }

    public Future<List<Role>> listByCode(List<String> roleCodeList) {
        if (CollectionUtil.isEmpty(roleCodeList)) {
            return Future.succeededFuture(Collections.emptyList());
        }
        SqlAssist sqlAssist = Role.codeListSqlAssist(roleCodeList);
        return roleSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(Role::new).collect(Collectors.toList());
        });
    }

    public Future<Role> getByCode(String code) {
        SqlAssist sqlAssist = Role.codeSqlAssist(code);
        return roleSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return null;
            }
            return new Role(jsonList.get(0));
        });
    }

    public Future<Role> getByName(String name) {
        SqlAssist sqlAssist = Role.nameSqlAssist(name);
        return roleSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return null;
            }
            return new Role(jsonList.get(0));
        });
    }

    public Future<Long> fillAndInsertWithCheck(Role role) {
        return getByCode(role.getCode()).compose(dbRole -> {
            if (dbRole != null) {
                return Future.failedFuture("角色编码重复");
            }
            return getByName(role.getRoleName());
        }).compose(dbRole -> {
            if (dbRole != null) {
                return Future.failedFuture("角色名称重复");
            }
            return roleSQL.insertNonEmptyGeneratedKeys(role, MySQLClient.LAST_INSERTED_ID);
        });
    }

    public Future<Integer> updateWithCheck(Role role) {
        return getById(role.getId()).<List<Role>>compose(dbRole -> {
            if (dbRole == null) {
                return Future.failedFuture("更新角色时未查询到角色");
            }
            SqlAssist sqlAssist = Role.codeOrNameSqlAssist(role.getCode(), role.getRoleName());
            return roleSQL.selectAll(sqlAssist).map(jsonList -> {
                if (jsonList == null || jsonList.isEmpty()) {
                    return Collections.emptyList();
                }
                return jsonList.stream().map(Role::new).collect(Collectors.toList());
            });
        }).compose(roleList -> {
            for (Role dbRole: roleList) {
                if (!dbRole.getId().equals(role.getId())) {
                    return Future.failedFuture("");
                }
            }
            Role updateRole = new Role();
            updateRole.setId(role.getId());
            updateRole.setCode(role.getCode());
            updateRole.setRoleName(role.getRoleName());
            return update(updateRole);
        });
    }

    public Future<Integer> deleteWithCheck(long roleId) {
        return getById(roleId).<Long>compose(dbRole -> {
            if (dbRole == null) {
                return Future.failedFuture("删除角色时未查询出角色");
            }

            return employeeRoleService.countEmployeeOfRole(roleId);
        }).compose(count -> {
            if (count > 0) {
                return Future.failedFuture("当前角色下存在用户");
            }
            return delete(roleId);
        });
    }

    public Future<Long> insert(Role role) {
        return roleSQL.insertNonEmptyGeneratedKeys(role, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(Role role) {
        return roleSQL.updateNonEmptyById(role);
    }

    public Future<Integer> delete(long roleId) {
        return roleSQL.deleteById(roleId);
    }

    public void setRoleSQL(RoleSQL roleSQL) {
        this.roleSQL = roleSQL;
    }

    public void setEmployeeRoleService(EmployeeRoleService employeeRoleService) {
        this.employeeRoleService = employeeRoleService;
    }
}
