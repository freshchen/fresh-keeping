package com.github.freshchen.keeping.common;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author darcy
 * @since 2021/1/27
 * <p>
 * 切入点类型：execution
 * 切入点范围：within
 * 切入点上下文：this  target  @annotation
 * <p>
 * 一个好的切入点至少包含类型和范围，最好能指定上下文
 **/
@Aspect
public class Pointcuts {

    /**
     * 任何共有方法
     */
    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    /**
     * 任何共有无参方法
     */
    @Pointcut("execution(public * *())")
    public void publicMethodWithoutParam() {
    }

    /**
     * 任何 set 单参数方法
     */
    @Pointcut("execution(* set*(*))")
    public void setter() {
    }

    /**
     * 任何 get 无参数方法
     */
    @Pointcut("execution(* get*())")
    public void getter() {
    }

    /**
     * 代理 controller 包下所有类中的方法
     */
    @Pointcut("within(com.github.freshchen.keeping.controller..*)")
    public void inController() {
    }

    @Pointcut("within(com.github.freshchen.keeping.service..*)")
    public void inService() {
    }

}
