package com.github.freshchen.keeping.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明接口为SPI接口，支持通过配置替换实现
 * @author darcy
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpiInterface {

    /**
     * 默认实现，Class 需要使用 SpiService 标识，并且实现 SpiInterface 标识的接口
     * @see SpiService
     */
    Class<?> defaultSpiService();

}
