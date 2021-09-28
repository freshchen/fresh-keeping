package com.github.freshchen.keeping.common.lib.model;

import lombok.Getter;

/**
 * @author darcy
 * @since 2020/06/10
 **/
@Getter
public class Error {

    private int code;

    private String message;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Error of(int code, String message) {
        return new Error(code, message);
    }

}
