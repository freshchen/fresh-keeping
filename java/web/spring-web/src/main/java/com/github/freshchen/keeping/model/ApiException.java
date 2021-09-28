package com.github.freshchen.keeping.model;

import com.github.freshchen.keeping.common.lib.model.Error;
import lombok.Getter;

/**
 * 接入层异常，包括参数检验异常、授权异常等。
 */
public class ApiException extends RuntimeException {

    @Getter
    private int code;

    public ApiException(Error error) {
        super(error.getMessage());

        this.code = error.getCode();
    }

    public ApiException(Error error, Throwable cause) {
        super(cause);

        this.code = error.getCode();
    }

    public ApiException(Error error, String message) {
        super(message);

        this.code = error.getCode();
    }

    public ApiException(Error error, String message, Throwable cause) {
        super(message, cause);

        this.code = error.getCode();
    }
}
