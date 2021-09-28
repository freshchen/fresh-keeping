package com.github.freshchen.keeping.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/27
 **/
@Aspect
@Component
public class AfterThrowingGetterInServiceAspect {

    /**
     * 抛出异常后执行，会在 finally 后面执行
     *
     * @param joinPoint
     */
    @AfterThrowing(pointcut = "com.github.freshchen.keeping.common.Pointcuts.publicMethod() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.getter() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.inService()",
            throwing = "ex")
    public void getter(JoinPoint joinPoint, RuntimeException ex) {
        System.out.println("AfterThrowingGetterInServiceAspect " + joinPoint.toShortString() + " ex " + ex.getMessage());
    }
}
