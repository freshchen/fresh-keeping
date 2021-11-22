package com.github.freshchen.keeping.config;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * @author darcy
 * @since 2021/11/19
 */
@EnableAsync
@Slf4j
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor();
    }

    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(2048);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("asyncExecutor-");
        executor.setRejectedExecutionHandler((r, e) -> {
            String errMsg = String.format("Task %s rejected from %s", r.toString(), e.toString());
            log.error(errMsg);
            throw new RejectedExecutionException(errMsg);
        });
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor);
    }
}
