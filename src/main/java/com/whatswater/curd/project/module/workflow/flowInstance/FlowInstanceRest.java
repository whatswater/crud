package com.whatswater.curd.project.module.workflow.flowInstance;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowInstance")
public class FlowInstanceRest {
    private final FlowInstanceService flowInstanceService;

    public FlowInstanceRest(FlowInstanceService flowInstanceService) {
this.flowInstanceService = flowInstanceService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowInstance>>> search(@BeanParam Page page, FlowInstanceQuery query) {
        if (query == null) {
            query = new FlowInstanceQuery();
        }
        return flowInstanceService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowInstance> get(@QueryParam("id") Long flowInstanceId) {
        return flowInstanceService.getById(flowInstanceId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowInstance flowInstance) {
        return flowInstanceService.insert(flowInstance).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowInstance flowInstance) {
        Assert.assertNotNull(flowInstance.getId(), "Id不能为空");
        return flowInstanceService.update(flowInstance).map(RestResult::success);
    }
}
