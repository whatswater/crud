package com.whatswater.curd.project.sys.employeeFilter;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sys/employeeFilter")
public class EmployeeFilterRest {
    private final EmployeeFilterService employeeFilterService;

    public EmployeeFilterRest(EmployeeFilterService employeeFilterService) {
        this.employeeFilterService = employeeFilterService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<EmployeeFilter>>> search(@BeanParam Page page, EmployeeFilterQuery query) {
        if (query == null) {
            query = new EmployeeFilterQuery();
        }
        return employeeFilterService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/queryDataByCode")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<List<EmployeeFilterData>>> queryDataByCode(@QueryParam("code") String code) {
        Assert.assertNotEmpty(code, "编码不能为空");
        return employeeFilterService.queryDataByCode(code).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<EmployeeFilter> get(@QueryParam("id") Long employeeFilterId) {
        return employeeFilterService.getById(employeeFilterId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<List<Long>>> insert(EmployeeFilterTreeVo employeeFilterTreeVo) {
        Assert.assertNotEmpty(employeeFilterTreeVo.getCode(), "编码不能为空");
        Assert.assertNotEmpty(employeeFilterTreeVo.getPathValueList(), "过滤列表不能为空");

        return employeeFilterService.insertTreeWithCheck(employeeFilterTreeVo).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<List<Long>>> update(EmployeeFilterTreeVo employeeFilterTreeVo) {
        Assert.assertNotEmpty(employeeFilterTreeVo.getCode(), "编码不能为空");
        return employeeFilterService.updateTreeWithCheck(employeeFilterTreeVo).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> delete(@QueryParam("code") String code) {
        Assert.assertNotEmpty(code, "编码不能为空");
        return employeeFilterService.deleteWithCheck(code).map(RestResult::success);
    }
}
