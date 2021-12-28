package com.github.freshchen.keeping.annotations;

import com.github.freshchen.keeping.spi.OnSpiCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

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
@Conditional(OnSpiCondition.class)
@Component
public @interface SpiService {
}
