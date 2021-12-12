package com.github.freshchen.keeping.config;

import com.github.freshchen.keeping.client.UserClient;
import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @author darcy
 * @since 2020/12/19
 **/
@Configuration
@EnableFeignClients(clients = {UserClient.class})
@PropertySource("classpath:application.properties")
@Import({UserClient.UserApiFallbackFactory.class})
public class OrderClientAutoConfig {

    @Bean
    Logger.Level getLoggerLevel() {
        return Logger.Level.FULL;
    }

}
