package com.github.freshchen.keeping.formatter;

import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;

/**
 * @author darcy
 * @since 2020/06/27
 **/
public class CustomFormatterRegistrar implements FormatterRegistrar {
    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringTrimmerConverter(false));
        registry.addConverter(new OffsetDateTimeConverter());
    }
}
