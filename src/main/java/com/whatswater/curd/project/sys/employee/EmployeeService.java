package com.whatswater.curd.project.sys.employee;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.sys.organization.Organization;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class EmployeeService {
    private final EmployeeSQL employeeSQL;
    private OrganizationService organizationService;

    public EmployeeService(MySQLPool pool) {
        this.employeeSQL = new EmployeeSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<EmployeeListVo>> search(Page page, EmployeeQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        Future<PageResult<Employee>> pageResultFuture = employeeSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return employeeSQL.selectAll(sqlAssist)
                    .map(list -> PageResult.of(list.stream().map(Employee::new)
                        .collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });

        return pageResultFuture.compose(pageResult -> {
            List<Employee> employeeList = pageResult.getData();
            if (CollectionUtil.isEmpty(employeeList)) {
                return Future.succeededFuture(PageResult.of(Collections.emptyList(), pageResult));
            }

            List<EmployeeListVo> voList = BeanUtil.copyToList(employeeList, EmployeeListVo.class);
            List<Long> organizationIds = employeeList.stream()
                .map(Employee::getOrganizationId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(organizationIds)) {
                return Future.succeededFuture(PageResult.of(voList, pageResult));
            }
            Future<List<Organization>> organizationListFuture = organizationService.listByIds(organizationIds);

            return organizationListFuture.map(organizationList -> {
                if (CollectionUtil.isEmpty(organizationList)) {
                    return PageResult.of(voList, pageResult);
                }
                for (EmployeeListVo vo: voList) {
                    for (Organization org: organizationList) {
                        if (vo.getOrganizationId() != null && vo.getOrganizationId().equals(org.getId())) {
                            vo.setOrganizationName(org.getOrganizationName());
                            vo.setOrganizationCode(org.getOrganizationCode());
                            break;
                        }
                    }
                }
                return PageResult.of(voList, pageResult);
            });
        });
    }

    // todo 判断是否存在角色和部门
    public Future<Boolean> canLogin(String loginName) {
        return isEnabled(loginName);
    }

    public Future<Boolean> isEnabled(String loginName) {
        return getByLoginName(loginName).compose(employee -> {
            if (employee == null) {
                return Future.succeededFuture(false);
            }

            if (!EmployeeStatus.isEnabled(employee.getStatus())) {
                return Future.succeededFuture(false);
            }
            return Future.succeededFuture(true);
        });
    }

    public Future<Employee> getById(long id) {
        Future<JsonObject> result = employeeSQL.selectById(id);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new Employee(json);
        });
    }

    public Future<List<Employee>> listByIds(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Future.succeededFuture(Collections.emptyList());
        }
        SqlAssist sqlAssist = Employee.idListSqlAssist(ids);
        return employeeSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(Employee::new).collect(Collectors.toList());
        });
    }

    public Future<Employee> getByLoginName(String loginName) {
        Employee employee = new Employee();
        employee.setLoginName(loginName);
        SqlAssist sqlAssist = employee.loginNameSqlAssist();

        return employeeSQL.selectAll(sqlAssist).map(list -> {
            if (list.size() == 0) {
                return null;
            }
            return new Employee(list.get(0));
        });
    }

    public Future<List<Employee>> listByLoginName(List<String> loginNameList) {
        if (CollectionUtil.isEmpty(loginNameList)) {
            return Future.succeededFuture(Collections.emptyList());
        }
        SqlAssist sqlAssist = Employee.loginNameListSqlAssist(loginNameList);
        return employeeSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(Employee::new).collect(Collectors.toList());
        });
    }

    public Future<List<Employee>> listByOrganization(List<Long> organizationIds) {
        if (CollectionUtil.isEmpty(organizationIds)) {
            return Future.succeededFuture(Collections.emptyList());
        }
        SqlAssist sqlAssist = Employee.organizationIdListSqlAssist(organizationIds);
        return employeeSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(Employee::new).collect(Collectors.toList());
        });
    }

    public Future<Long> fillAndInsertWithCheck(Employee employee) {
        return getByLoginName(employee.getLoginName()).compose(old -> {
            if (old != null) {
                return Future.failedFuture("当前用户已存在相同的登录名: " + employee.getLoginName());
            }

            Employee employeeInsert = new Employee();
            employeeInsert.setLoginName(employee.getLoginName());
            employeeInsert.setName(employee.getName());
            employeeInsert.setInitPassword("2021@" + CrudUtils.randomString(8));
            employeeInsert.setPassword(CrudUtils.hashPassword(employeeInsert.getInitPassword()));
            employeeInsert.setOrganizationId(employee.getOrganizationId());

            employeeInsert.setPhone(employee.getPhone());
            employeeInsert.setEmail(employee.getEmail());
            employeeInsert.setStatus(EmployeeStatus.INIT.getId());

            return insert(employeeInsert);
        });
    }

    public Future<Long> insert(Employee employee) {
        return employeeSQL.insertNonEmptyGeneratedKeys(employee, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> enable(long employeeId) {
        return getById(employeeId).compose(employee -> {
            if (employee == null) {
                return Future.failedFuture("当前用户不存在，无法启用");
            }

            Employee updateEmployee = new Employee();
            updateEmployee.setId(employeeId);
            updateEmployee.setStatus(EmployeeStatus.ENABLED.getId());

            return update(updateEmployee);
        });
    }

    public Future<Integer> disable(long employeeId) {
        return getById(employeeId).compose(employee -> {
            if (employee == null) {
                return Future.failedFuture("当前用户不存在，无法启用");
            }
            if (employee.getStatus() == null || EmployeeStatus.ENABLED.getId() != employee.getStatus()) {
                return Future.failedFuture("当前用户不是启用状态，不需禁用启用");
            }

            Employee updateEmployee = new Employee();
            updateEmployee.setId(employeeId);
            updateEmployee.setStatus(EmployeeStatus.DISABLED.getId());

            return update(updateEmployee);
        });
    }

    public Future<Integer> update(Employee updateEmployee) {
        return employeeSQL.updateNonEmptyById(updateEmployee);
    }

    public Future<Integer> deleteWithCheck(long employeeId) {
        return getById(employeeId).compose(employee -> {
           if (!EmployeeStatus.canDelete(employee.getStatus())) {
                return Future.failedFuture("只有初始化状态的用户才能删除");
           }
           return delete(employee.getId());
        });
    }

    public Future<Integer> delete(long employeeId) {
        return employeeSQL.deleteById(employeeId);
    }

    public boolean verifyPassword(Employee employee, String password) {
        String hashed = CrudUtils.hashPassword(password);
        String real = employee.getPassword();
        return hashed.equals(real);
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
}
