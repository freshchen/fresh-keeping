package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.aop.ValidationExceptionHandler;
import com.github.freshchen.keeping.marker.ValidMarker;
import com.github.freshchen.keeping.model.Cat;
import com.github.freshchen.keeping.model.Group;
import com.github.freshchen.keeping.model.Person;
import com.github.freshchen.keeping.model.JsonResult;
import com.github.freshchen.keeping.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.github.freshchen.keeping.model.JsonResult.ok;


/**
 * @author darcy
 * @since 2020/02/19
 **/
@RestController
@RequestMapping("/person1")
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * 注解 @Valid 和 @Validated 都用于参数校验，都实现了 JSR-303 前者是标准注解，后者是 Spring 特有
     * SpringMVC中 @RequestBody 注解的 bean 在数据绑定的时候同时会触发校验，校验结果绑定在 BindingResult 中
     * 既然是用 Spring 框架， 除了联嵌套校验的时候只能用 @Valid 其他都用 @Validated
     *
     * @see ValidationExceptionHandler 这里我么用 aop 统一处理
     * @see MethodValidationPostProcessor springboot已经默认开启了方法层面的拦截可以对方法的参数返回拦截,如果没有需要装配进容器
     * 除了 Controller层 需要使用校验，需要在类上加上 @Validated 生成代理类, 特别注意被 @Overide 注解的方法，代理类中 @Validated 注解会丢失
     */

    @PostMapping("/valid")
    public JsonResult valid(@Validated @RequestBody Person person) {
        System.out.println(person);
        personService.hello1();
        personService.hello(null);
        return ok();
    }

    /**
     * 指定了Group之后没有指定Group的不会生效
     */

    @PostMapping("/valid/group/create")
    public JsonResult validGroupCreate(@Validated(ValidMarker.Create.class) @RequestBody Group group) {
        System.out.println(group);
        return ok();
    }

    @PostMapping("/valid/group/update")
    public JsonResult validGroupUpdate(@Validated(ValidMarker.Update.class) @RequestBody Group group) {
        System.out.println(group);
        return ok();
    }

    @PostMapping("/valid/group")
    public JsonResult validGroup(@Validated @RequestBody Group group) {
        System.out.println(group);
        return ok();
    }

    /**
     * 自定义校验注解
     */

    @PostMapping("/valid/cat")
    public JsonResult validCostom(@Validated @RequestBody Cat cat) {
        System.out.println(cat);
        return ok();
    }
}
