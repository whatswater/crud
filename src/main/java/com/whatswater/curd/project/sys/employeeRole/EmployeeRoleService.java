package com.whatswater.curd.project.sys.employeeRole;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.Tuple2;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.role.Role;
import com.whatswater.curd.project.sys.role.RoleService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeeRoleService {
    private EmployeeRoleSQL employeeRoleSQL;
    private EmployeeService employeeService;
    private RoleService roleService;

    public EmployeeRoleService() {

    }

    public Future<PageResult<EmployeeRoleListVo>> search(Page page, EmployeeRoleQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        Future<PageResult<EmployeeRole>> pageResultFuture = employeeRoleSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return employeeRoleSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(EmployeeRole::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });

        return pageResultFuture.compose(pageResult -> {
            List<EmployeeRole> employeeRoleList = pageResult.getData();
            if (CollectionUtil.isEmpty(employeeRoleList)) {
                return Future.succeededFuture(PageResult.of(Collections.emptyList(), pageResult));
            }

            List<EmployeeRoleListVo> voList = BeanUtil.copyToList(employeeRoleList, EmployeeRoleListVo.class);
            List<Long> roleIds = employeeRoleList.stream()
                .map(EmployeeRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
            List<Long> employeeIds = employeeRoleList.stream()
                .map(EmployeeRole::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

            Future<Tuple2<List<Employee>, List<Role>>> tuple2Future = employeeService.listByIds(employeeIds).compose(employees -> {
                return roleService.listByIds(roleIds).map(roles -> Tuple2.of(employees, roles));
            });
            return tuple2Future.map(tuple2 -> {
                List<Employee> employees = tuple2._1;
                List<Role> roles = tuple2._2;

                for (EmployeeRoleListVo vo: voList) {
                    for (Employee employee: employees) {
                        if (vo.getUserId() != null && vo.getUserId().equals(employee.getId())) {
                            vo.setLoginName(employee.getLoginName());
                            vo.setName(employee.getName());
                            break;
                        }
                    }
                    for (Role role: roles) {
                        if (vo.getUserId() != null && vo.getRoleId().equals(role.getId())) {
                            vo.setRoleName(role.getRoleName());
                            vo.setCode(role.getCode());
                            break;
                        }
                    }
                }
                return PageResult.of(voList, pageResult);
            });
        });
    }

    public Future<Long> countEmployeeOfRole(long roleId) {
        SqlAssist sqlAssist = EmployeeRole.roleIdSqlAssist(roleId);
        return employeeRoleSQL.getCount(sqlAssist);
    }

    public Future<EmployeeRole> getById(long employeeRoleId) {
        Future<JsonObject> result = employeeRoleSQL.selectById(employeeRoleId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new EmployeeRole(json);
        });
    }

    public Future<List<EmployeeRole>> listByRoleIds(List<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return Future.succeededFuture(Collections.emptyList());
        }
        SqlAssist sqlAssist = EmployeeRole.roleIdListSqlAssist(roleIds);
        return employeeRoleSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                return Collections.emptyList();
            }

            return jsonList.stream().map(EmployeeRole::new).collect(Collectors.toList());
        });
    }

    public Future<List<Employee>> listEmployeeOfRoleCode(String code) {
        return roleService.getByCode(code)
            .compose(role -> {
                if (role == null) {
                    return Future.failedFuture("根据角色编码:" + code + ", 无法查出角色信息");
                }
                return this.listByRoleIds(Collections.singletonList(role.getId()));
            })
            .compose(employeeRoles -> {
                if (CollectionUtil.isEmpty(employeeRoles)) {
                    return Future.succeededFuture(Collections.emptyList());
                }
                return this.employeeService.listByIds(employeeRoles.stream().map(EmployeeRole::getUserId).distinct().collect(Collectors.toList()));
            });
    }

    public Future<Integer> deleteWithCheck(long employeeRoleId) {
        return getById(employeeRoleId).compose(employeeRole -> {
            if (employeeRole == null) {
                return Future.failedFuture("不存在此数据");
            }
            return delete(employeeRoleId);
        });
    }

    public Future<Long> insert(EmployeeRole employeeRole) {
        return employeeRoleSQL.insertNonEmptyGeneratedKeys(employeeRole, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(EmployeeRole employeeRole) {
        return employeeRoleSQL.updateNonEmptyById(employeeRole);
    }

    public Future<Integer> delete(long employeeRoleId) {
        return employeeRoleSQL.deleteById(employeeRoleId);
    }

    public void setEmployeeRoleSQL(EmployeeRoleSQL employeeRoleSQL) {
        this.employeeRoleSQL = employeeRoleSQL;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
}
