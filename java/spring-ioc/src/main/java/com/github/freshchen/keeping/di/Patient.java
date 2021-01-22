package com.github.freshchen.keeping.di;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author darcy
 * @since 2021/1/22
 **/
@Component
public class Patient {
    /**
     * By name
     */
    @Getter
    @Resource
    private User user;
}
