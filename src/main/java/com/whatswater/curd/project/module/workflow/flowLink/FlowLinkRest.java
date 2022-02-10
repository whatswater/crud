package com.whatswater.curd.project.module.workflow.flowLink;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowLink")
public class FlowLinkRest {
    private final FlowLinkService flowLinkService;

    public FlowLinkRest(FlowLinkService flowLinkService) {
this.flowLinkService = flowLinkService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowLink>>> search(@BeanParam Page page, FlowLinkQuery query) {
        if (query == null) {
            query = new FlowLinkQuery();
        }
        return flowLinkService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowLink> get(@QueryParam("id") Long flowLinkId) {
        return flowLinkService.getById(flowLinkId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowLink flowLink) {
        return flowLinkService.insert(flowLink).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowLink flowLink) {
        Assert.assertNotNull(flowLink.getId(), "Id不能为空");
        return flowLinkService.update(flowLink).map(RestResult::success);
    }
}
