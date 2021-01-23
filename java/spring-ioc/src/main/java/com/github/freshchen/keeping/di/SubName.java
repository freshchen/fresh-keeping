package com.github.freshchen.keeping.di;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
// 通过 Intro 注入时候，由于有两个bean实现，再根据 type 注入时需要指定主实现
// 或者 @Qualifier 指定bean name
@Primary
public class SubName implements Intro {

    @Override
    public void intro() {
        System.out.println("SubName intro");
    }
}
