package com.github.freshchen.keeping.di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
public class Name implements Intro {

    @Autowired
    /**
     * 如果循环依赖不可避免可以使用 lazy 解决，会先加载完 Name , 然后完成 User 加载，最后再把 User 注入回来
     *
     */
    @Lazy
    private User user;

    @Value("${intro.name}")
    private String string;

    @Override
    public void intro() {
        System.out.println("Name intro " + string);
    }
}
