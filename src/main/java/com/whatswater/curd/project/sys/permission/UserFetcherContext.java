package com.whatswater.curd.project.sys.permission;


import io.vertx.core.Future;

public interface UserFetcherContext {
    Future<String> getVariableValue(String variableName);
}
