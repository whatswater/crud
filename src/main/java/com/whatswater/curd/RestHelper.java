package com.whatswater.curd;


import com.zandero.rest.RestRouter;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public abstract class RestHelper {
    public static final String TEMP_PATH = "/ub9ZdPO3KI4dwUa";

    public static Router register(Router router, String pathPrefix, Object restApi) {
        router = RestRouter.register(router, restApi);
        Route route = router.get(TEMP_PATH + "/list");
        return router;
    }
}
