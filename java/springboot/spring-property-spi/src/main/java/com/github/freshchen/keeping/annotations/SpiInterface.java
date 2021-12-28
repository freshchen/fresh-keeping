package com.github.freshchen.keeping.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lingchen02
 * @since 2021/12/28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpiInterface {

    /**
     * 默认实现
     */
    Class<?> defaultSpiService();

}
