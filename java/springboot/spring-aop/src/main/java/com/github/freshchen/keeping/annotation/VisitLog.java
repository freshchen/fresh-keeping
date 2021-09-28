package com.github.freshchen.keeping.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author darcy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface VisitLog {

    @AliasFor("action")
    String value() default "";

    @AliasFor("value")
    String action() default "";

    String[] params() default {};

}
