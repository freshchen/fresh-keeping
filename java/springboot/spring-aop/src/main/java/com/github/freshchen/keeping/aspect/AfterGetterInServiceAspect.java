package com.github.freshchen.keeping.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/27
 **/
@Aspect
@Component
public class AfterGetterInServiceAspect {

    /**
     * 不管正常返回还是异常返回都执行，会在 finally 后面执行
     *
     * @param joinPoint
     */
    @After("com.github.freshchen.keeping.common.Pointcuts.publicMethod() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.getter() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.inService()")
    public void getter(JoinPoint joinPoint) {
        System.out.println("AfterGetterInServiceAspect " + joinPoint.toShortString());
    }
}
