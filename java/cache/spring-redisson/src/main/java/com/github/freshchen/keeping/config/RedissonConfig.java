package com.github.freshchen.keeping.config;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author darcy
 * @since 2021/10/19
 */
@Configuration
@ConditionalOnClass(RedissonAutoConfiguration.class)
public class RedissonConfig {

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return new RedissonCustomizer();
    }
}
