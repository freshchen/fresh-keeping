package com.github.freshchen.keeping.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.freshchen.keeping.formatter.CustomEnumModule;
import com.github.freshchen.keeping.formatter.CustomFormatterRegistrar;
import com.github.freshchen.keeping.formatter.Java8Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.TimeZone;

/**
 * @author darcy
 * @since 2020/06/13
 **/
@Configuration
@EnableSpringDataWebSupport
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 时间转 OffsetDateTime，字符串去前后空格
        new CustomFormatterRegistrar().registerFormatters(registry);
    }

    @Bean
    public ObjectMapper objectMapper() {
        Java8Mapper java8Mapper = new Java8Mapper();
        // 确保反序列化时自动加上TimeZone信息
        java8Mapper.setTimeZone(TimeZone.getDefault());
        // 枚举转换，可以使用指定的枚举 "value", "name" 字段或者枚举名挨个转换
        java8Mapper.registerModule(new CustomEnumModule("value", "name"));

        return java8Mapper;
    }
}
