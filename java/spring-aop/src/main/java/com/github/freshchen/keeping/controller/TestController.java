package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 * @since 2021/1/27
 **/
@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping()
    public String name() {
        return userService.getName();
    }

    @GetMapping("/1")
    public String name1() {
        try {
            return userService.getNameWithError();
        } catch (Exception e) {

        }
        return "";
    }
}
