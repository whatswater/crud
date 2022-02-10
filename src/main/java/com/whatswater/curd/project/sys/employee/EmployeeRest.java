package com.whatswater.curd.project.sys.employee;


import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.common.RestResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/sys/employee")
public class EmployeeRest {
    private final EmployeeService employeeService;

    public EmployeeRest(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<EmployeeListVo>>> search(@BeanParam Page page, EmployeeQuery query) {
        if (query == null) {
            query = new EmployeeQuery();
        }
        return employeeService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Employee>> get(@QueryParam("userId") Long userId) {
        Assert.assertNotNull(userId, "用户Id不能为空");
        return employeeService.getById(userId).map(RestResult::success);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> put(Employee employee) {
        Assert.assertNotNull(employee, "用户不能为空");
        Assert.assertNotNull(employee.getLoginName(), "用户登录名不能为空");
        Assert.assertNotNull(employee.getName(), "用户姓名不能为空");
        Assert.assertNotNull(employee.getOrganizationId(), "用户部门不能为空");

        return employeeService.fillAndInsertWithCheck(employee).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> delete(@QueryParam("id") Long employeeId) {
        Assert.assertNotNull(employeeId, "用户Id不能为空");
        return employeeService.deleteWithCheck(employeeId).map(RestResult::success);
    }

    @POST
    @Path("/enable")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> enable(@QueryParam("id") Long employeeId) {
        Assert.assertNotNull(employeeId, "用户Id不能为空");
        return employeeService.enable(employeeId).map(RestResult::success);
    }

    @POST
    @Path("/disable")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> disable(@QueryParam("id") Long employeeId) {
        Assert.assertNotNull(employeeId, "用户Id不能为空");
        return employeeService.disable(employeeId).map(RestResult::success);
    }
}
