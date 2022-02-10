package com.whatswater.curd.project.sys.employeeRole;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/sys/employeeRole")
public class EmployeeRoleRest {
    private final EmployeeRoleService employeeRoleService;

    public EmployeeRoleRest(EmployeeRoleService employeeRoleService) {
        this.employeeRoleService = employeeRoleService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<EmployeeRoleListVo>>> search(@BeanParam Page page, EmployeeRoleQuery query) {
        if (query == null) {
            query = new EmployeeRoleQuery();
        }
        return employeeRoleService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<EmployeeRole> get(@QueryParam("id") Long employeeRoleId) {
        return employeeRoleService.getById(employeeRoleId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(EmployeeRole employeeRole) {
        return employeeRoleService.insert(employeeRole).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(EmployeeRole employeeRole) {
        Assert.assertNotNull(employeeRole.getId(), "Id不能为空");
        return employeeRoleService.update(employeeRole).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> delete(@QueryParam("id") Long employeeRoleId) {
        Assert.assertNotNull(employeeRoleId, "Id不能为空");
        return employeeRoleService.deleteWithCheck(employeeRoleId).map(RestResult::success);
    }
}
