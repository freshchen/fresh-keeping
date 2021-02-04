package com.github.freshchen.keeping.aspect;

import com.github.freshchen.keeping.annotation.VisitLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 */
@Component
@Aspect
public class VisitLogAspect {

    @Around("com.github.freshchen.keeping.common.Pointcuts.publicMethod() " +
            "&& com.github.freshchen.keeping.common.Pointcuts.inController()" +
            "&& @annotation(visitLog)")
    public Object getter(ProceedingJoinPoint joinPoint, VisitLog visitLog) throws Throwable {
        System.out.println("VisitLogAspect " + visitLog);
        Object proceed = joinPoint.proceed();
        return proceed;
    }

}
