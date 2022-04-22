package com.whatswater.curd.project.module.todo;


import cn.hutool.core.io.FileUtil;
import com.whatswater.async.ClassNameAndData;
import com.whatswater.async.Transformer;
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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TodoModule implements Module {
    TodoService todoService = new TodoService();
    ITodoAwaitService todoAwaitService;
    TodoClassLoader todoClassLoader = new TodoClassLoader(this.getClass().getClassLoader());

    public TodoModule() {
        String path = "com/whatswater/curd/project/module/todo/TodoAwaitService";
        try {
            Transformer transformer = new Transformer(path);
            List<ClassNameAndData> dataList = transformer.transform();

            Map<String, byte[]> classDataMap = new TreeMap<>();
            for (ClassNameAndData data: dataList) {
                FileUtil.writeBytes(data.getData(), "D:/code/classes/" + data.getClassName() + ".class");
                classDataMap.put(data.getClassName(), data.getData());
            }
            todoClassLoader.setData(classDataMap);
            Class<?> cls = todoClassLoader.loadClass("com.whatswater.curd.project.module.todo.TodoAwaitService");
            todoAwaitService = (ITodoAwaitService) cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            todoAwaitService = new TodoAwaitService();
        }
//        todoAwaitService = new TodoAwaitService();
    }

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
            TodoSQL todoSQL = new TodoSQL(SQLExecute.createMySQL(pool));
            todoService.setTodoSQL(todoSQL);
            todoAwaitService.setTodoSQL(todoSQL);
        } else if ("router".equals(name)) {
            Router router = (Router) obj;
            TodoRest rest = new TodoRest(todoService, todoAwaitService);
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
