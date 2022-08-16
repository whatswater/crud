package com.whatswater.curd.project.sys.employee;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.sys.organization.Organization;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import com.whatswater.sql.executor.Context;
import io.vertx.core.Future;
import io.vertx.ext.sql.assist.SqlAssist;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EmployeeService$$Enhance$$ implements IEmployeeService {
    private EmployeeService employeeService;
    private Context context;

    @Override
    public Future<Employee> getById(long id) {
        return null;
    }

    @Override
    public Future<List<Employee>> listByIds(List<Long> ids) {
        return null;
    }

    @Override
    public Future<Employee> getByLoginName(String loginName) {
        return null;
    }

    @Override
    public Future<PageResult<EmployeeListVo>> search(Page page, EmployeeQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());
        EmployeeSQL employeeSQL = getContextService(employeeService.getEmployeeSQL());
        OrganizationService organizationService = getContextService(employeeService.getOrganizationService());

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

    @Override
    public Future<Long> fillAndInsertWithCheck(Employee employee) {
        return null;
    }
}
