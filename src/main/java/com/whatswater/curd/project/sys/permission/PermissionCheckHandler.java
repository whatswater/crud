package com.whatswater.curd.project.sys.permission;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.CrudConst;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;


public class PermissionCheckHandler implements Handler<RoutingContext> {
    private final UserTokenService userTokenService;
    private AuthService authService;

    public PermissionCheckHandler(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    @Override
    public void handle(RoutingContext rc) {
        String path = rc.request().path();
        if ("/login/admin".equals(path) || "/login/employee".equals(path) || "/business/namespace/list".equals(path)) {
            rc.next();
            return;
        }
        if (path.startsWith("/html/") || path.startsWith("/static/") || path.startsWith("/favicon.ico")) {
            rc.next();
            return;
        }

        String token = rc.request().getHeader(CrudConst.HEADER_TOKEN);
        if (StrUtil.isEmpty(token)) {
            rc.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code());
            rc.end();
            return;
        }

        if (CrudConst.WORKFLOW_TOKEN.equals(token)) {
            rc.next();
            return;
        }

        UserToken userToken = userTokenService.getUserToken(token);
        if (userToken == null) {
            rc.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code());
            rc.end();
            return;
        }

        long current = System.currentTimeMillis();
        if (userToken.isExpired(current)) {
            rc.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code());
            rc.end();
            return;
        }
        Future<Boolean> checkResult = authService.authPermission(path);
        checkResult.onSuccess(r -> {
            if (!r) {
                rc.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code());
                rc.end();
                return;
            }

            userToken.setUpdateTime(current);
            rc.data().put(CrudConst.RC_KEY_USER_TOKEN, userToken);
            rc.next();
        });
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
