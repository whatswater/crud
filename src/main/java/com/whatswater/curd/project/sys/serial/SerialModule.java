package com.whatswater.curd.project.sys.serial;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.sys.serial.SerialService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class SerialModule implements Module {
    
    SerialService serialService;
    Router router;
    
    
    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
    }
    
    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            serialService = new SerialService(pool);
            consumer.exportObject("serialService", serialService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }
        
        if (router != null && serialService != null) {
            SerialRest rest = new SerialRest(serialService);
            RestRouter.register(router, rest);
        }
    }
}
