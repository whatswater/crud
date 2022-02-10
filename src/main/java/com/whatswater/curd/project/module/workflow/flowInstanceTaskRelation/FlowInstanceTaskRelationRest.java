package com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowInstanceTaskRelation")
public class FlowInstanceTaskRelationRest {
    private final FlowInstanceTaskRelationService flowInstanceTaskRelationService;

    public FlowInstanceTaskRelationRest(FlowInstanceTaskRelationService flowInstanceTaskRelationService) {
this.flowInstanceTaskRelationService = flowInstanceTaskRelationService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowInstanceTaskRelation>>> search(@BeanParam Page page, FlowInstanceTaskRelationQuery query) {
        if (query == null) {
            query = new FlowInstanceTaskRelationQuery();
        }
        return flowInstanceTaskRelationService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowInstanceTaskRelation> get(@QueryParam("id") Long flowInstanceTaskRelationId) {
        return flowInstanceTaskRelationService.getById(flowInstanceTaskRelationId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowInstanceTaskRelation flowInstanceTaskRelation) {
        return flowInstanceTaskRelationService.insert(flowInstanceTaskRelation).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowInstanceTaskRelation flowInstanceTaskRelation) {
        Assert.assertNotNull(flowInstanceTaskRelation.getId(), "Id不能为空");
        return flowInstanceTaskRelationService.update(flowInstanceTaskRelation).map(RestResult::success);
    }
}
