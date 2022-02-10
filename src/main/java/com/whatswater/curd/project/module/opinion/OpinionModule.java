package com.whatswater.curd.project.module.opinion;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.opinion.OpinionService;
import com.whatswater.curd.project.module.workflow.flowEngine.FlowEngineService;
import com.whatswater.curd.project.sys.permission.UserTokenService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class OpinionModule implements Module {
    OpinionService opinionService = new OpinionService();
    Router router;


    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_ENGINE, "flowEngineService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_PERMISSION, "userTokenService");

        moduleInfo.exportObject("opinionService", opinionService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            opinionService.setOpinionSQL(new OpinionSQL(SQLExecute.createMySQL(pool)));
            consumer.exportObject("opinionService", opinionService);
        } else if ("router".equals(name)) {
            router = (Router) obj;

            OpinionRest rest = new OpinionRest(opinionService);
            RestRouter.register(router, rest);
        } else if ("flowEngineService".equals(name)) {
            opinionService.setFlowEngineService((FlowEngineService) obj);
        } else if ("userTokenService".equals(name)) {
            opinionService.setUserTokenService((UserTokenService) obj);
        }
    }
}
