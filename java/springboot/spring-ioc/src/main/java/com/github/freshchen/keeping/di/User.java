package com.github.freshchen.keeping.di;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
public class User {

    @Getter
    private Intro intro;

    /**
     * 构造参数注入
     * 如果Name没有注册进 Ioc 会报
     * Parameter 0 of constructor User required a bean of type 'Name' that could not be found.
     * 可以用 @Qualifier 根据 bean name完成注入
     *
     * @param intro
     */
    public User(@Qualifier("name") Intro intro) {
        this.intro = intro;
    }
}
