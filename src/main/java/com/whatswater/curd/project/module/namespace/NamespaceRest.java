package com.whatswater.curd.project.module.namespace;


import com.whatswater.curd.CrudConst;
import com.whatswater.nothing.data.ModelDataList;
import com.whatswater.nothing.data.SchemaDataService;
import io.vertx.core.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.TreeMap;

@Path("/business/namespace")
public class NamespaceRest {
    private final SchemaDataService schemaDataService;

    public NamespaceRest(SchemaDataService schemaDataService) {
        this.schemaDataService = schemaDataService;
    }

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<ModelDataList> list() {
        Map<String, Object> params = new TreeMap<>();
        params.put("eq id", 1);
        params.put("like name", "01");
        return schemaDataService.list(params);
    }
}
