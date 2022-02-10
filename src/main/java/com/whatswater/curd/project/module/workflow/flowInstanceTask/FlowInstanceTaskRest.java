package com.whatswater.curd.project.module.workflow.flowInstanceTask;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowInstanceTask")
public class FlowInstanceTaskRest {
    private final FlowInstanceTaskService flowInstanceTaskService;

    public FlowInstanceTaskRest(FlowInstanceTaskService flowInstanceTaskService) {
this.flowInstanceTaskService = flowInstanceTaskService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowInstanceTask>>> search(@BeanParam Page page, FlowInstanceTaskQuery query) {
        if (query == null) {
            query = new FlowInstanceTaskQuery();
        }
        return flowInstanceTaskService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowInstanceTask> get(@QueryParam("id") Long flowInstanceTaskId) {
        return flowInstanceTaskService.getById(flowInstanceTaskId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowInstanceTask flowInstanceTask) {
        return flowInstanceTaskService.insert(flowInstanceTask).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowInstanceTask flowInstanceTask) {
        Assert.assertNotNull(flowInstanceTask.getId(), "Id不能为空");
        return flowInstanceTaskService.update(flowInstanceTask).map(RestResult::success);
    }
}
