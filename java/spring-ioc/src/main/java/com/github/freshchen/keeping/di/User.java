package com.github.freshchen.keeping.di;

import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
public class User {

    @Getter
    private Name name;

    /**
     * 构造参数注入
     * 如果Name没有注册进 Ioc 会报
     * Parameter 0 of constructor User required a bean of type 'Name' that could not be found.
     *
     * @param name
     */
    public User(Name name) {
        this.name = name;
    }
}
