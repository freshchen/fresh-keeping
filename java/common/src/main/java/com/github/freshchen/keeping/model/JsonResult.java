package com.github.freshchen.keeping.model;

import lombok.Data;

import java.util.Optional;

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
    private Optional<T> data = Optional.empty();

    /**
     * 错误码
     */
    private Optional<Integer> errCode = Optional.empty();

    /**
     * 错误消息
     */
    private Optional<String> errMessage = Optional.empty();

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
        jsonResult.setData(Optional.of(data));
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
        jsonResult.setErrCode(Optional.of(errorCode));
        jsonResult.setErrMessage(Optional.ofNullable(errorMessage));
        return jsonResult;
    }

}
