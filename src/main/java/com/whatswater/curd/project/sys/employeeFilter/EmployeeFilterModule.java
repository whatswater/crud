package com.whatswater.curd.project.sys.employeeFilter;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.sys.employeeFilter.EmployeeFilterService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class EmployeeFilterModule implements Module {
    EmployeeFilterDataService employeeFilterDataService = new EmployeeFilterDataService();
    EmployeeFilterService employeeFilterService;
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
            employeeFilterDataService.setEmployeeFilterDataSQL(new EmployeeFilterDataSQL(SQLExecute.createMySQL(pool)));

            employeeFilterService = new EmployeeFilterService(pool);
            employeeFilterService.setEmployeeFilterDataService(employeeFilterDataService);
            consumer.exportObject("employeeFilterService", employeeFilterService);
            consumer.exportObject("employeeFilterDataService", employeeFilterDataService);
        } else if ("router".equals(name)) {
            router = (Router) obj;
        }

        if (router != null && employeeFilterService != null) {
            EmployeeFilterRest rest = new EmployeeFilterRest(employeeFilterService);
            RestRouter.register(router, rest);
        }
    }
}
