package com.whatswater.curd.project.sys.permission;

import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.employeeFilter.EmployeeFilterDataValueTypeEnum;
import com.whatswater.curd.project.sys.employeeFilter.SExpressionUtil;
import com.whatswater.curd.project.sys.employeeFilter.SExpressionUtil.FunctionCall;
import com.whatswater.curd.project.sys.employeeFilter.SExpressionUtil.LogicNode;
import com.whatswater.curd.project.sys.employeeFilter.SExpressionUtil.SExpression;
import com.whatswater.curd.project.sys.employeeRole.EmployeeRole;
import com.whatswater.curd.project.sys.employeeRole.EmployeeRoleService;
import com.whatswater.curd.project.sys.organization.Organization;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import com.whatswater.curd.project.sys.role.Role;
import com.whatswater.curd.project.sys.role.RoleService;
import io.vertx.core.Future;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserFetcher {
    private EmployeeService employeeService;
    private OrganizationService organizationService;
    private RoleService roleService;
    private EmployeeRoleService employeeRoleService;

    public Future<List<Employee>> executeSExpression(SExpression sExpression, UserFetcherContext userFetcherContext) {
        return executeSExpression(sExpression, userFetcherContext, null);
    }

    public Future<List<Employee>> executeSExpression(SExpression sExpression, UserFetcherContext userFetcherContext, List<Employee> initialList) {
        if (sExpression instanceof LogicNode) {
            LogicNode logicNode = (LogicNode) sExpression;
            int logicType = logicNode.getLogicType();
            if (SExpressionUtil.LOGIC_TYPE_AND == logicType) {
                List<SExpression> sExpressionList = logicNode.getChildren();

                List<SameFutureBuilder<List<Employee>>> taskBuilderList = sExpressionList.stream().map(expr ->
                    (SameFutureBuilder<List<Employee>>)list -> {
                        List<Employee> startList = initialList;
                        if (CollectionUtil.isNotEmpty(list)) {
                            startList = list.get(list.size() - 1);
                        }
                        return executeSExpression(expr, userFetcherContext, startList);
                    }
                ).collect(Collectors.toList());

                return CrudUtils.serialTask(taskBuilderList).map(list -> {
                    if (CollectionUtil.isEmpty(list)) {
                        return Collections.emptyList();
                    }
                    return list.get(list.size() - 1);
                });
            } else if (SExpressionUtil.LOGIC_TYPE_OR == logicType) {
                List<SExpression> sExpressionList = logicNode.getChildren();
                List<SameFutureBuilder<List<Employee>>> taskBuilderList = sExpressionList.stream().map(expr ->
                    (SameFutureBuilder<List<Employee>>)list -> {
                        return executeSExpression(expr, userFetcherContext, initialList);
                    }
                ).collect(Collectors.toList());

                return CrudUtils.serialTask(taskBuilderList).map(list -> {
                    if (CollectionUtil.isNotEmpty(list)) {
                        return Collections.emptyList();
                    }
                    List<Employee> ret = new ArrayList<>();
                    for (List<Employee> item: list) {
                        ret.addAll(item);
                    }
                    return ret;
                });
            } else {
                return Future.succeededFuture(Collections.emptyList());
            }
        } else if (sExpression instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) sExpression;
            return replaceParamVariable(userFetcherContext, functionCall.getParams()).compose(params -> {
                if (EmployeeFilterDataValueTypeEnum.EMPLOYEE.getFunctionName().equals(functionCall.getFunctionName())) {
                    if (CollectionUtil.isEmpty(initialList)) {
                        return fetchEmployeeByLoginName(params);
                    } else {
                        return filterEmployeeByLoginName(initialList, params);
                    }
                } else if (EmployeeFilterDataValueTypeEnum.ORGANIZATION.getFunctionName().equals(functionCall.getFunctionName())) {
                    if (CollectionUtil.isEmpty(initialList)) {
                        return fetchEmployeeByOrganization(params);
                    } else {
                        return filterEmployeeByOrganization(initialList, params);
                    }
                } else if (EmployeeFilterDataValueTypeEnum.ROLE.getFunctionName().equals(functionCall.getFunctionName())) {
                    if (CollectionUtil.isEmpty(initialList)) {
                        return fetchEmployeeByRole(params);
                    } else {
                        return filterEmployeeByRole(initialList, params);
                    }
                } else {
                    return Future.succeededFuture(Collections.emptyList());
                }
            });
        } else {
            return Future.succeededFuture(Collections.emptyList());
        }
    }

    public Future<List<Employee>> fetchEmployeeByLoginName(List<String> employeeLoginNameList) {
        return employeeService.listByLoginName(employeeLoginNameList);
    }

    public Future<List<Employee>> filterEmployeeByLoginName(List<Employee> employeeList, List<String> employeeLoginNameList) {
        List<Employee> remain = employeeList.stream().filter(employee -> employeeLoginNameList.contains(employee.getLoginName())).collect(Collectors.toList());
        return Future.succeededFuture(remain);
    }

    public Future<List<Employee>> fetchEmployeeByRole(List<String> roleCodeList) {
        return roleService.listByCode(roleCodeList)
            .compose(roleList -> {
                List<Long> roleIds = roleList.stream().map(Role::getId).collect(Collectors.toList());
                return employeeRoleService.listByRoleIds(roleIds);
            }).compose(employeeRoles -> {
                List<Long> userIds = employeeRoles.stream().map(EmployeeRole::getUserId).distinct().collect(Collectors.toList());
                return employeeService.listByIds(userIds);
            });
    }

    public Future<List<Employee>> filterEmployeeByRole(List<Employee> employeeList, List<String> roleCodeList) {
        return roleService.listByCode(roleCodeList)
            .compose(roleList -> {
                List<Long> roleIds = roleList.stream().map(Role::getId).collect(Collectors.toList());
                return employeeRoleService.listByRoleIds(roleIds);
            }).map(employeeRoles -> {
                Set<Long> userIds = employeeRoles.stream().map(EmployeeRole::getUserId).collect(Collectors.toSet());
                return employeeList.stream().filter(employee -> userIds.contains(employee.getId())).collect(Collectors.toList());
            });
    }

    public Future<List<Employee>> fetchEmployeeByOrganization(List<String> organizationCodeList) {
        return organizationService
            .listByCode(organizationCodeList)
            .compose(orgList -> {
                List<Long> orgIds = orgList.stream().map(Organization::getId).collect(Collectors.toList());
                return employeeService.listByOrganization(orgIds);
            });
    }

    public Future<List<Employee>> filterEmployeeByOrganization(List<Employee> employeeList, List<String> organizationCodeList) {
        return organizationService
            .listByCode(organizationCodeList)
            .map(orgList -> {
                Set<Long> orgIds = orgList.stream().map(Organization::getId).collect(Collectors.toSet());
                return employeeList.stream().filter(employee -> orgIds.contains(employee.getOrganizationId())).collect(Collectors.toList());
            });
    }

    static Pattern pattern = Pattern.compile("^\\$\\{(.+)}$");
    public static Future<List<String>> replaceParamVariable(UserFetcherContext userFetcherContext, List<String> params) {
        if (CollectionUtil.isEmpty(params)) {
            return Future.succeededFuture(params);
        }

        List<SameFutureBuilder<String>> taskBuilderList = params.stream().map(param -> (SameFutureBuilder<String>) r -> {
            Matcher matcher = pattern.matcher(param);
            if (matcher.matches()) {
                String variableName = matcher.group(1);
                return userFetcherContext.getVariableValue(variableName);
            } else {
                return Future.succeededFuture(param);
            }
        }).collect(Collectors.toList());
        return CrudUtils.serialTask(taskBuilderList);
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public void setEmployeeRoleService(EmployeeRoleService employeeRoleService) {
        this.employeeRoleService = employeeRoleService;
    }
}
