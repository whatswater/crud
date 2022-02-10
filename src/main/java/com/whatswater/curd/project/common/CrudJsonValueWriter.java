package com.whatswater.curd.project.common;

import com.zandero.rest.writer.HttpResponseWriter;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class CrudJsonValueWriter extends ObjectMapperHolder implements HttpResponseWriter<Object> {
    @Override
    public void write(Object result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
        response.setStatusCode(HttpResponseStatus.OK.code());
        response.end(mapper.writeValueAsString(result));
    }
}
