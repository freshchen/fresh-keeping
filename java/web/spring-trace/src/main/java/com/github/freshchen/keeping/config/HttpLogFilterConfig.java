package com.github.freshchen.keeping.config;


import com.github.freshchen.keeping.filter.LogTraceIdFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author darcy
 * @since 2020/08/09
 **/
@Configuration
@ConditionalOnProperty(name = "fresh.spring.trace.enable", matchIfMissing = true)
public class HttpLogFilterConfig {

    @Bean
    public LogTraceIdFilter logTraceIdFilter() {
        return new LogTraceIdFilter();
    }

    @Bean
    public FilterRegistrationBean<LogTraceIdFilter> logTraceIdFilterBean(LogTraceIdFilter logTraceIdFilter) {
        FilterRegistrationBean<LogTraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(logTraceIdFilter);
        registration.addUrlPatterns("/*");
        registration.setName("logTraceIdFilterBean");
        registration.setOrder(1);
        return registration;
    }

}
