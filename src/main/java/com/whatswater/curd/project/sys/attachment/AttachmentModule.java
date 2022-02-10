package com.whatswater.curd.project.sys.attachment;


import com.whatswater.asyncmodule.Module;
import com.whatswater.asyncmodule.ModuleInfo;
import com.whatswater.curd.NewInstanceModuleFactory;
import com.whatswater.curd.project.sys.attachment.AttachmentService;
import com.zandero.rest.RestRouter;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLPool;

public class AttachmentModule implements Module {
    JsonObject config;
    AttachmentService attachmentService = new AttachmentService();
    Router router;


    @Override
    public void register(ModuleInfo moduleInfo) {
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, "datasource");
        moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, "router");
        moduleInfo.require("init:global", "config");

        moduleInfo.exportObject("attachmentService", attachmentService);
    }

    @Override
    public void onResolved(ModuleInfo consumer, ModuleInfo provider, String name, Object obj) {
        if ("datasource".equals(name)) {
            MySQLPool pool = (MySQLPool) obj;
            attachmentService.setAttachmentSQL(new AttachmentSQL(SQLExecute.createMySQL(pool)));
        } else if ("router".equals(name)) {
            router = (Router) obj;
            AttachmentRest rest = new AttachmentRest(attachmentService);
            RestRouter.register(router, rest);
        } else if ("config".equals(name)) {
            config = (JsonObject) obj;
            attachmentService.setUploadFileFolder(config.getString("upload.fileFolder"));
        }
    }
}
