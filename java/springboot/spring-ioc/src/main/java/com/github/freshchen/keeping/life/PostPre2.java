package com.github.freshchen.keeping.life;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
public class PostPre2 {

    @PostConstruct
    private void init() {
        System.out.println("PostPre2 PostConstruct");
    }
}
