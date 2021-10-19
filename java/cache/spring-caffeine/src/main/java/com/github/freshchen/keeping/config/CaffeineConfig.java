package com.github.freshchen.keeping.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.freshchen.keeping.common.lib.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author darcy
 * @since 2021/10/19
 */
@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<Integer, User> userCache() {
        return Caffeine.newBuilder()
                .recordStats()
                .maximumSize(1000)
                .expireAfterWrite(100, TimeUnit.SECONDS)
                .build();
    }

}
