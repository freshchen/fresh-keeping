package com.github.freshchen.keeping.config;


import com.github.freshchen.keeping.filter.LogbackTraceIdFilter;
import org.springframework.beans.factory.annotation.Value;
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
public class LogbackTraceIdFilterConfig {

    @Value("${fresh.spring.trace.logTraceIdFilterBeanOrder:-999}")
    private Integer logTraceIdFilterBeanOrder;

    @Bean
    public LogbackTraceIdFilter logTraceIdFilter() {
        return new LogbackTraceIdFilter();
    }

    @Bean
    public FilterRegistrationBean<LogbackTraceIdFilter> logTraceIdFilterBean(LogbackTraceIdFilter logTraceIdFilter) {
        FilterRegistrationBean<LogbackTraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(logTraceIdFilter);
        registration.addUrlPatterns("/*");
        registration.setName("logTraceIdFilterBean");
        registration.setOrder(logTraceIdFilterBeanOrder);
        return registration;
    }

}
