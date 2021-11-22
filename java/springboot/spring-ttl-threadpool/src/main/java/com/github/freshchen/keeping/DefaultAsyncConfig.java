package com.github.freshchen.keeping;

import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import static com.github.freshchen.keeping.TtlThreadPoolConstants.CORE;

/**
 * @author darcy
 * @since 2021/11/19
 */
@EnableAsync
@Slf4j
@Configuration
public class DefaultAsyncConfig implements AsyncConfigurer {

    @Value("${ttl.thread.pool.default.async.core.multiple:4}")
    private Integer core;

    @Value("${ttl.thread.pool.default.async.max.multiple:64}")
    private Integer max;

    @Value("${ttl.thread.pool.default.async.queue.capacity:2048}")
    private Integer queueCapacity;

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor();
    }

    /**
     * 适合执行短任务，特殊需要可自行注册线程池，并通过 @Async("name") 指定
     */
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE * core);
        executor.setMaxPoolSize(CORE * max);
        executor.setQueueCapacity(queueCapacity);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler((r, e) -> {
            String errMsg = String.format("Task %s rejected from %s", r.toString(), e.toString());
            log.error(errMsg);
            throw new RejectedExecutionException(errMsg);
        });
        executor.initialize();
        return TtlExecutors.getTtlExecutor(executor);
    }
}
