package com.github.freshchen.keeping.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/27
 **/
@Aspect
@Component
public class BeforeGetterInServiceAspect {

    @Before("com.github.freshchen.keeping.common.Pointcuts.publicMethod() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.getter() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.inService()")
    public void getter(JoinPoint joinPoint) {
        System.out.println("BeforeGetterInDataAspect " + joinPoint.toShortString());
    }
}
