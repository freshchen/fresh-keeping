package com.github.freshchen.controller;

import com.github.freshchen.custom.Age;
import com.github.freshchen.marker.ValidMarker;
import com.github.freshchen.model.Cat;
import com.github.freshchen.model.Group;
import com.github.freshchen.model.Person;
import com.github.freshchen.service.PersonService;
import model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.*;

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
     * 既然是用 Spring 框架， 除了联嵌套校验的时候只能用 @Valid 其他都用 @Validated
     * @see com.github.freshchen.aop.ValidationExceptionHandler 这里我么用 aop 统一处理
     * @see MethodValidationPostProcessor springboot已经默认开启了方法层面的拦截可以对方法的参数返回拦截,如果没有需要装配进容器
     * 由于需要生成代理类 @Validated 直接加在类上即可, 特别注意被 @Overide 注解的方法，代理类中 @Validated 注解会丢失
     */

    @PostMapping("/valid")
    public Result valid(@Validated @RequestBody Person person) {
        System.out.println(person);
        personService.hello1();
        personService.hello(null);
        return ok();
    }

    /**
     * 指定了Group之后没有指定Group的不会生效
     */

    @PostMapping("/valid/group/create")
    public Result validGroupCreate(@Validated(ValidMarker.Create.class) @RequestBody Group group) {
        System.out.println(group);
        return ok();
    }

    @PostMapping("/valid/group/update")
    public Result validGroupUpdate(@Validated(ValidMarker.Update.class) @RequestBody Group group) {
        System.out.println(group);
        return ok();
    }

    @PostMapping("/valid/group")
    public Result validGroup(@Validated @RequestBody Group group) {
        System.out.println(group);
        return ok();
    }

    /**
     * 自定义校验注解
     */

    @PostMapping("/valid/cat")
    public Result validCostom(@Validated @RequestBody Cat cat) {
        System.out.println(cat);
        return ok();
    }
}
