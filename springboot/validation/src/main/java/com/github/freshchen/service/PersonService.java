package com.github.freshchen.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * @author darcy
 * @since 2020/02/20
 **/
@Service
@Validated
public class PersonService {

    public void hello(@NotNull String name) {
        System.out.println(name);
    }

    public @NotNull String hello1(){
        return null;
    }
}
