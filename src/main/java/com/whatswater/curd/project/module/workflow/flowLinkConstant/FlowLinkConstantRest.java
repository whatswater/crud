package com.whatswater.curd.project.module.workflow.flowLinkConstant;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowLink")
public class FlowLinkConstantRest {
    private final FlowLinkConstantService flowLinkConstantService;

    public FlowLinkConstantRest(FlowLinkConstantService flowLinkConstantService) {
this.flowLinkConstantService = flowLinkConstantService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowLinkConstant>>> search(@BeanParam Page page, FlowLinkConstantQuery query) {
        if (query == null) {
            query = new FlowLinkConstantQuery();
        }
        return flowLinkConstantService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowLinkConstant> get(@QueryParam("id") Long flowLinkConstantId) {
        return flowLinkConstantService.getById(flowLinkConstantId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowLinkConstant flowLinkConstant) {
        return flowLinkConstantService.insert(flowLinkConstant).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowLinkConstant flowLinkConstant) {
        Assert.assertNotNull(flowLinkConstant.getId(), "Id不能为空");
        return flowLinkConstantService.update(flowLinkConstant).map(RestResult::success);
    }
}
