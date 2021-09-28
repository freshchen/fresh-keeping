package com.github.freshchen.keeping.imports.selector;

import com.github.freshchen.keeping.imports.ImportSelector1;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author darcy
 * @since 2021/2/5
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ImportSelector1.class)
public @interface EnableDataConfig {

    int order() default Ordered.LOWEST_PRECEDENCE;
}
