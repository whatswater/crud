package com.whatswater.curd.project.module.workflow.flowInstanceVariable;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowInstanceVariable")
public class FlowInstanceVariableRest {
    private final FlowInstanceVariableService flowInstanceVariableService;

    public FlowInstanceVariableRest(FlowInstanceVariableService flowInstanceVariableService) {
this.flowInstanceVariableService = flowInstanceVariableService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowInstanceVariable>>> search(@BeanParam Page page, FlowInstanceVariableQuery query) {
        if (query == null) {
            query = new FlowInstanceVariableQuery();
        }
        return flowInstanceVariableService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowInstanceVariable> get(@QueryParam("id") Long flowInstanceVariableId) {
        return flowInstanceVariableService.getById(flowInstanceVariableId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowInstanceVariable flowInstanceVariable) {
        return flowInstanceVariableService.insert(flowInstanceVariable).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowInstanceVariable flowInstanceVariable) {
        Assert.assertNotNull(flowInstanceVariable.getId(), "Id不能为空");
        return flowInstanceVariableService.update(flowInstanceVariable).map(RestResult::success);
    }
}
