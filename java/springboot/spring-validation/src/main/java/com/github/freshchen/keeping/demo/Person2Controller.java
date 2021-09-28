package com.github.freshchen.keeping.demo;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * @author darcy
 * @since 2020/02/19
 **/
@RestController
public class Person2Controller {

    /**
     * 会执行 创建动作相关空值校验，Api应用校验
     *
     * @param person
     */
    @PostMapping("/person")
    public void create(@RequestBody @Validated({Group.Create.class, Group.Api.class}) PersonDTO person) {
    }

    /**
     * 会执行 默认校验，只有 @Min(message = "年龄不能小于0", value = 0) 会生效
     *
     * @param person
     */
    @PostMapping("/person2")
    public void create2(@RequestBody @Valid PersonDTO person) {
    }

    /**
     * 会执行 更新动作相关空值校验
     *
     * @param person
     */
    @PutMapping("/person")
    public void update(@RequestBody @Validated({Group.Update.class}) PersonDTO person) {
    }

}
