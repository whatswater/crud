package com.whatswater.curd.project.sys.role;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.sys.organization.Organization;
import com.whatswater.curd.project.sys.organization.OrganizationQuery;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/sys/role")
public class RoleRest {
    private final RoleService roleService;

    public RoleRest(RoleService roleService) {
        this.roleService = roleService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<Role>>> search(@BeanParam Page page, RoleQuery query) {
        if (query == null) {
            query = new RoleQuery();
        }
        return roleService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<Role> get(@QueryParam("id") Long roleId) {
        return roleService.getById(roleId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(Role role) {
        return roleService.fillAndInsertWithCheck(role).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(Role role) {
        Assert.assertNotNull(role.getId(), "Id不能为空");
        Assert.assertNotEmpty(role.getCode(), "编码不能为空");
        Assert.assertNotEmpty(role.getRoleName(), "角色名称不能为空");

        return roleService.updateWithCheck(role).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> delete(@QueryParam("id") Long roleId) {
        Assert.assertNotNull(roleId, "Id不能为空");
        return roleService.deleteWithCheck(roleId).map(RestResult::success);
    }
}
