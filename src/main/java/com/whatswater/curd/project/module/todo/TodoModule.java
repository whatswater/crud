package com.whatswater.curd.project.module.todo;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.module.workflow.flowEngine.FlowEngineService;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.uid.UidGeneratorService;
import com.zandero.rest.RestRouter;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class TodoModule implements Module {
    TodoService todoService = new TodoService();

    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_UID, "uidGeneratorService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_FLOW_ENGINE, "flowEngineService");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_EMPLOYEE, "employeeService");

        moduleInfo.exportObject("todoService", todoService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            todoService.setTodoSQL(new TodoSQL(SQLExecute.createMySQL(pool)));
        } else if ("router".equals(name)) {
            Router router = (Router) obj;
            TodoRest rest = new TodoRest(todoService);
            RestRouter.register(router, rest);
        } else if ("uidGeneratorService".equals(name)) {
            UidGeneratorService uidGeneratorService = (UidGeneratorService) obj;
            todoService.setUidGeneratorService(uidGeneratorService);
        } else if ("flowEngineService".equals(name)) {
            todoService.setFlowEngineService((FlowEngineService) obj);
        } else if ("employeeService".equals(name)) {
            todoService.setEmployeeService((EmployeeService) obj);
        }
    }
}
