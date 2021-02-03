package com.github.freshchen.keeping.annotation;

import java.lang.annotation.*;

/**
 * @author darcy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface VisitLog {

    String value() default "";

    String action() default "";

    String[] params() default {};

}
