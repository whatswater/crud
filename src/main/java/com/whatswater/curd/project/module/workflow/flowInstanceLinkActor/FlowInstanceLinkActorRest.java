package com.whatswater.curd.project.module.workflow.flowInstanceLinkActor;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/workflow/flowInstanceLinkActor")
public class FlowInstanceLinkActorRest {
    private final FlowInstanceLinkActorService flowInstanceLinkActorService;

    public FlowInstanceLinkActorRest(FlowInstanceLinkActorService flowInstanceLinkActorService) {
        this.flowInstanceLinkActorService = flowInstanceLinkActorService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowInstanceLinkActor>>> search(@BeanParam Page page, FlowInstanceLinkActorQuery query) {
        if (query == null) {
            query = new FlowInstanceLinkActorQuery();
        }
        return flowInstanceLinkActorService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<FlowInstanceLinkActor> get(@QueryParam("id") Long flowInstanceLinkActorId) {
        return flowInstanceLinkActorService.getById(flowInstanceLinkActorId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowInstanceLinkActor flowInstanceLinkActor) {
        return flowInstanceLinkActorService.insert(flowInstanceLinkActor).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowInstanceLinkActor flowInstanceLinkActor) {
        Assert.assertNotNull(flowInstanceLinkActor.getId(), "Id不能为空");
        return flowInstanceLinkActorService.update(flowInstanceLinkActor).map(RestResult::success);
    }
}
