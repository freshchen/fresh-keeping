package com.github.freshchen.keeping.common.lib.model;

import lombok.Data;

/**
 * @author darcy
 * @since 2020/02/19
 **/
@Data
public class JsonResult<T> {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应结果
     */
    private T data;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 生成服务调用成功响应
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> JsonResult<T> ok(T data) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setSuccess(true);
        jsonResult.setData(data);
        return jsonResult;
    }

    /**
     * 生成服务调用成功响应
     *
     * @return
     */
    public static JsonResult ok() {
        JsonResult jsonResult = new JsonResult<>();
        jsonResult.setSuccess(true);
        return jsonResult;
    }

    /**
     * 生成服务调用错误响应
     *
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> JsonResult<T> error(Integer errorCode, String errorMessage) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setCode(errorCode);
        jsonResult.setMessage(errorMessage);
        return jsonResult;
    }

    public static <T> JsonResult<T> error(Error error) {
        return error(error.getCode(), error.getMessage());
    }

}
