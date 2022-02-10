package com.whatswater.curd.project.module.workflow.flowConstant;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowConstant")
public class FlowConstantRest {
    private final FlowConstantService flowConstantService;

    public FlowConstantRest(FlowConstantService flowConstantService) {
        this.flowConstantService = flowConstantService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowConstant>>> search(@BeanParam Page page, FlowConstantQuery query) {
        if (query == null) {
            query = new FlowConstantQuery();
        }
        return flowConstantService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowConstant> get(@QueryParam("id") Long flowConstantId) {
        return flowConstantService.getById(flowConstantId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowConstant flowConstant) {
        return flowConstantService.insert(flowConstant).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowConstant flowConstant) {
        Assert.assertNotNull(flowConstant.getId(), "Id不能为空");
        return flowConstantService.update(flowConstant).map(RestResult::success);
    }
}
