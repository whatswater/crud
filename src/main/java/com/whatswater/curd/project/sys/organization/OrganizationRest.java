package com.whatswater.curd.project.sys.organization;

import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.common.RestResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sys/organization")
public class OrganizationRest {
    private final OrganizationService organizationService;

    public OrganizationRest(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<Organization>>> search(@BeanParam Page page, OrganizationQuery query) {
        if (query == null) {
            query = new OrganizationQuery();
        }
        return organizationService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/queryChildrenName")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<List<OrganizationName>>> queryChildrenName(@QueryParam("id") Long id) {
        Assert.assertNotNull(id, "Id不能为空");
        return organizationService.queryChildrenName(id).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Organization>> get(@QueryParam("id") Long organizationId) {
        return organizationService.getById(organizationId).map(RestResult::success);
    }

    @POST
    @Path("/listByIds")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<List<Organization>>> listByIds(@QueryParam("ids") String organizationIds) {
        Assert.assertNotEmpty(organizationIds, "部门Id列表不能为空");
        List<Long> orgIds = Arrays.stream(organizationIds.split(StrUtil.COMMA)).map(Long::parseLong).collect(Collectors.toList());
        return organizationService.listByIds(orgIds).map(RestResult::success);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(@QueryParam("parentId") Long parentId, OrganizationName organization) {
        if (parentId == null) {
            parentId = 0L;
        }
        Assert.assertNotEmpty(organization.getOrganizationCode(), "部门编码不能为空");
        Assert.assertNotEmpty(organization.getOrganizationName(), "部门名称不能为空");

        return organizationService.fillAndInsertWithCheck(parentId, organization).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(OrganizationName organization) {
        Assert.assertNotNull(organization.getId(), "Id不能为空");
        Assert.assertNotEmpty(organization.getOrganizationName(), "部门名称不能为空");

        return organizationService.updateWithCheck(organization).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> delete(@QueryParam("id") Long id) {
        Assert.assertNotNull(id, "Id不能为空");
        return organizationService.deleteWithCheck(id).map(RestResult::success);
    }
}
