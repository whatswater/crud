package com.whatswater.curd.project.common;


import com.whatswater.curd.CrudConst;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class RestResult<T> {
    public static final String PROPERTY_CODE = "code";
    public static final String PROPERTY_MSG = "msg";
    public static final String PROPERTY_DATA = "data";
    public static final String PROPERTY_SUCCESS = "success";

    private String code;
    private String msg;
    private T data;

    private RestResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private RestResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static RestResult<Void> success() {
        return new RestResult<>(CrudConst.SUCCESS_CODE, CrudConst.SUCCESS_MSG);
    }

    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(CrudConst.SUCCESS_CODE, CrudConst.SUCCESS_MSG, data);
    }

    public static <T> RestResult<T> success(String msg, T data) {
        return new RestResult<>(CrudConst.SUCCESS_CODE, msg, data);
    }

    public static <T> RestResult<T> success(String msg, String code, T data) {
        return new RestResult<>(code, msg, data);
    }

    public static <T> RestResult<T> fail() {
        return fail(CrudConst.FAIL_CODE, CrudConst.FAIL_MSG);
    }

    public static <T> RestResult<T> fail(String msg) {
        return fail(CrudConst.FAIL_CODE, msg);
    }

    public static <T> RestResult<T> fail(BusinessException e) {
        return fail(e.getCode(), e.getMessage());
    }

    public static <T> RestResult<T> fail(String code, String message) {
        return new RestResult<>(code, message);
    }

    public boolean isSuccess() {
        return code.startsWith(CrudConst.SUCCESS_CODE);
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static RestResult<String> stringFromJsonObject(JsonObject jsonObject) {
        String code = jsonObject.getString(PROPERTY_CODE);
        String msg = jsonObject.getString(PROPERTY_MSG);
        if (code.startsWith(CrudConst.SUCCESS_CODE)) {
            String data = jsonObject.getString(PROPERTY_DATA);
            return new RestResult<>(code, msg, data);
        }
        return new RestResult<>(code, msg);
    }

    public static <T> RestResult<T> dataFromJsonObject(JsonObject jsonObject, Class<T> dataClass) {
        String code = jsonObject.getString(PROPERTY_CODE);
        String msg = jsonObject.getString(PROPERTY_MSG);
        if (code.startsWith(CrudConst.SUCCESS_CODE)) {
            JsonObject dataJson = jsonObject.getJsonObject(PROPERTY_DATA);
            return new RestResult<>(code, msg, dataJson.mapTo(dataClass));
        }
        return new RestResult<>(code, msg);
    }

    public static <T> RestResult<List<T>> listFromJsonObject(JsonObject jsonObject, Class<T> dataClass) {
        String code = jsonObject.getString(PROPERTY_CODE);
        String msg = jsonObject.getString(PROPERTY_MSG);
        if (code.startsWith(CrudConst.SUCCESS_CODE)) {
            JsonArray dataJson = jsonObject.getJsonArray(PROPERTY_DATA);
            List<T> dataList = new ArrayList<>(dataJson.size());
            for (int i = 0; i < dataJson.size(); i++) {
                dataList.add(dataJson.getJsonObject(i).mapTo(dataClass));
            }
            return new RestResult<>(code, msg, dataList);
        }
        return new RestResult<>(code, msg);
    }
}
