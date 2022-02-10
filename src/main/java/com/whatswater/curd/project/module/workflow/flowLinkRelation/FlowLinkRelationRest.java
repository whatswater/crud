package com.whatswater.curd.project.module.workflow.flowLinkRelation;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowLinkRelation")
public class FlowLinkRelationRest {
    private final FlowLinkRelationService flowLinkRelationService;

    public FlowLinkRelationRest(FlowLinkRelationService flowLinkRelationService) {
this.flowLinkRelationService = flowLinkRelationService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowLinkRelation>>> search(@BeanParam Page page, FlowLinkRelationQuery query) {
        if (query == null) {
            query = new FlowLinkRelationQuery();
        }
        return flowLinkRelationService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowLinkRelation> get(@QueryParam("id") Long flowLinkRelationId) {
        return flowLinkRelationService.getById(flowLinkRelationId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowLinkRelation flowLinkRelation) {
        return flowLinkRelationService.insert(flowLinkRelation).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowLinkRelation flowLinkRelation) {
        Assert.assertNotNull(flowLinkRelation.getId(), "Id不能为空");
        return flowLinkRelationService.update(flowLinkRelation).map(RestResult::success);
    }
}
