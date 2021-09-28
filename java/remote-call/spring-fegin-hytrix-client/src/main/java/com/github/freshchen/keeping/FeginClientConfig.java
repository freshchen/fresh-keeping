package com.github.freshchen.keeping;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author darcy
 * @since 2020/12/19
 **/
@Configuration
@EnableFeignClients
public class FeginClientConfig {

    @Primary
    @Bean
    Logger.Level getLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Primary
    @Bean
    Logger getFeignLogger() {
        return new FreshFeignLogger();
    }
}
