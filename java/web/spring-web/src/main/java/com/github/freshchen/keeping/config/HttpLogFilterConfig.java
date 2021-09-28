package com.github.freshchen.keeping.config;


import com.github.freshchen.keeping.filter.HttpLogFilter;
import com.github.freshchen.keeping.filter.RequestReplaceFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author darcy
 * @since 2020/08/09
 **/
@Configuration
@ConditionalOnProperty(name = "fresh.spring.http.log.enable", matchIfMissing = true)
public class HttpLogFilterConfig {

    @Bean
    public HttpLogFilter httpLogFilter() {
        return new HttpLogFilter();
    }

    @Bean
    public RequestReplaceFilter requestReplaceFilter() {
        return new RequestReplaceFilter();
    }

    @Bean
    public FilterRegistrationBean requestReplaceFilterBean(RequestReplaceFilter requestReplaceFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(requestReplaceFilter);
        registration.addUrlPatterns("/*");
        registration.setName("requestReplaceFilter");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean httpLogFilterBean(HttpLogFilter httpLogFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(httpLogFilter);
        registration.addUrlPatterns("/*");
        registration.setName("httpLogFilterBean");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}
