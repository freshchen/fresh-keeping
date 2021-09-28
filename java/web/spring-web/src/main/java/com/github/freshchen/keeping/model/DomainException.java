package com.github.freshchen.keeping.model;

import com.github.freshchen.keeping.common.lib.model.Error;
import lombok.Getter;

/**
 * 业务异常，表示业务相关错误。例如用户不存在、商品已下架等。
 */
public class DomainException extends RuntimeException {

    @Getter
    private int code;

    public DomainException(Error error) {
        super(error.getMessage());

        this.code = error.getCode();
    }

    public DomainException(Error error, Throwable cause) {
        super(cause);

        this.code = error.getCode();
    }

    public DomainException(Error error, String message) {
        super(String.format("%s, caused by DomainException: %s;", message, error.getMessage()));

        this.code = error.getCode();
    }

    public DomainException(Error error, String message, Throwable cause) {
        super(String.format("%s, caused by DomainException: %s;", message, error.getMessage()), cause);

        this.code = error.getCode();
    }
}
