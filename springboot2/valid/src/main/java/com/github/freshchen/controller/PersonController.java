package com.github.freshchen.controller;

import com.github.freshchen.model.PersonQO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author darcy
 * @since 2020/02/19
 **/
@RestController
@RequestMapping("/person")
public class PersonController {

    /**
     * 注解 @Valid 可以帮助我们做校验
     * 值得注意的是我们不能在被 @Override 修饰的方法上做校验，没法生成代理会报异常
     * @param person
     * @return
     */
    @PostMapping("/valid")
    public String valid(@Valid @RequestBody PersonQO person) {
        return "ok";
    }
}
