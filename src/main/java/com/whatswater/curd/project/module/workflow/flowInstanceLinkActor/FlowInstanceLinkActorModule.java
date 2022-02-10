package com.whatswater.curd.project.module.workflow.flowInstanceLinkActor;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowInstanceLinkActor.FlowInstanceLinkActorService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceLinkActorModule implements Module {
    FlowInstanceLinkActorService flowInstanceLinkActorService = new FlowInstanceLinkActorService();
    Router router;


    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");

        moduleInfo.exportObject("flowInstanceLinkActorService", flowInstanceLinkActorService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;

            flowInstanceLinkActorService.setFlowInstanceLinkActorSQL(new FlowInstanceLinkActorSQL(SQLExecute.createMySQL(pool)));
        } else if ("router".equals(name)) {
            router = (Router) obj;
            FlowInstanceLinkActorRest rest = new FlowInstanceLinkActorRest(flowInstanceLinkActorService);
            RestRouter.register(router, rest);
        }
    }
}
