package com.github.freshchen.keeping.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/1/27
 **/
@Aspect
@Component
public class AfterReturningGetterInServiceAspect {

    /**
     * 正常返回才生效，异常不生效，会在 finally 后面执行
     *
     * @param joinPoint
     */
    @AfterReturning(pointcut = "com.github.freshchen.keeping.common.Pointcuts.publicMethod() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.getter() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.inService()",
            returning = "result")
    public void getter(JoinPoint joinPoint, String result) {
        System.out.println("AfterReturningGetterInServiceAspect " + joinPoint.toShortString() + " result " + result);
    }
}
