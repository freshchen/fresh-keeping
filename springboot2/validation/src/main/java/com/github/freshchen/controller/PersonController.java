package com.github.freshchen.controller;

import com.github.freshchen.model.Person;
import com.github.freshchen.service.PersonService;
import model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static model.Result.ok;

/**
 * @author darcy
 * @since 2020/02/19
 **/
@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * 注解 @Valid 和 @Validated 都用于参数校验，都实现了 JSR-303 前者是标准注解，后者是 Spring 特有
     * SpringMVC中 @RequestBody 注解的 bean 在数据绑定的时候同时会触发校验，校验结果绑定在 BindingResult 中
     * 既然是用 Spring 框架， 除了练级校验的时候只能用 @Valid 其他都用 @Validated
     * @see com.github.freshchen.aop.ValidationExceptionHandler 这里我么用 aop 统一处理
     * @see MethodValidationPostProcessor springboot已经默认开启了方法层面的拦截可以对方法的参数返回拦截,如果没有需要装配进容器
     * 由于需要生成代理类 @Validated 直接加在类上即可, 特别注意被 @Overide 注解的方法，代理类中 @Validated 注解会丢失
     * @param person
     * @return
     */
    @PostMapping("/valid")
    public Result valid(@Validated @RequestBody Person person) {
        personService.hello1();
        personService.hello(null);
        return ok();
    }
}
