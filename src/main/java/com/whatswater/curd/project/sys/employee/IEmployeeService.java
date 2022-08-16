package com.whatswater.curd.project.sys.employee;


import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.sql.executor.ContextService;
import io.vertx.core.Future;

import java.util.List;

public interface IEmployeeService extends ContextService<IEmployeeService> {
    Future<Employee> getById(long id);
    Future<List<Employee>> listByIds(List<Long> ids);
    Future<Employee> getByLoginName(String loginName);
    Future<PageResult<EmployeeListVo>> search(Page page, EmployeeQuery query);
    Future<Long> fillAndInsertWithCheck(Employee employee);
}
