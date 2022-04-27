package com.github.freshchen.keeping.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author darcy
 * @since 2021/11/19
 */
@Configuration
@Slf4j
public class TheadConfig {

    @Bean(destroyMethod = "shutdown", name = "asyncExecutor1")
    public ScheduledExecutorService asyncExecutor1() {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        return executor;
    }
}
