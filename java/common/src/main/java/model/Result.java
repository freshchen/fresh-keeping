package model;

import lombok.Data;

import java.util.Optional;

/**
 * @author darcy
 * @since 2020/02/19
 **/
@Data
public class Result<T> {

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
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setData(Optional.of(data));
        return result;
    }

    /**
     * 生成服务调用成功响应
     * @return
     */
    public static Result ok() {
        Result result = new Result<>();
        result.setSuccess(true);
        return result;
    }

    /**
     * 生成服务调用错误响应
     *
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(Integer errorCode, String errorMessage) {
        Result<T> result = new Result<>();
        result.setErrCode(Optional.of(errorCode));
        result.setErrMessage(Optional.ofNullable(errorMessage));
        return result;
    }

}
