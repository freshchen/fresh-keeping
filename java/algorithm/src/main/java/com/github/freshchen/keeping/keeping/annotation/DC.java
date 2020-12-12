package com.github.freshchen.keeping.keeping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author freshchen
 * 分治，递归
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface DC {
    String value() default "";
}
