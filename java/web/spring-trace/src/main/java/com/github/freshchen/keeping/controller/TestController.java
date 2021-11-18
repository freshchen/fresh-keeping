package com.github.freshchen.keeping.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lingchen02
 * @since 2021/11/18
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public void test() {

    }
}
