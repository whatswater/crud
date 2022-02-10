package com.whatswater.curd.project.common;

import com.zandero.rest.exception.ExceptionHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonObject;

public class RestExceptionHandler implements ExceptionHandler<Throwable> {

    @Override
    public void write(Throwable result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        result.printStackTrace();
        response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        if (result instanceof BusinessException) {
            BusinessException businessException = (BusinessException) result;
            RestResult<?> restResult = RestResult.fail(businessException);
            JsonObject jsonObject = toJsonObject(restResult);
            response.end(jsonObject.encode());
        } else if (result instanceof NoStackTraceThrowable) {
            NoStackTraceThrowable businessException = (NoStackTraceThrowable) result;
            RestResult<?> restResult = RestResult.fail(businessException.getMessage());
            JsonObject jsonObject = toJsonObject(restResult);
            response.end(jsonObject.encode());
        } else {
            RestResult<?> restResult = RestResult.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.toString());
            JsonObject jsonObject = toJsonObject(restResult);
            response.end(jsonObject.encode());
        }
    }

    private static JsonObject toJsonObject(RestResult<?> restResult) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(RestResult.PROPERTY_CODE, restResult.getCode())
            .put(RestResult.PROPERTY_MSG, restResult.getMsg())
            .put(RestResult.PROPERTY_SUCCESS, false);
        return jsonObject;
    }
}
