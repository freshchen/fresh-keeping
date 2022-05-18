package com.github.freshchen.keeping.flink.jobs;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author darcy
 * @since 2022/05/08
 **/
@Slf4j
public abstract class BaseJob implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        config(env);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String jobName = this.getClass().getSimpleName();
            log.info(jobName + " Started");
            try {
                env.execute(jobName);
            } catch (Exception e) {
                log.error(jobName, e);
            }
        });
    }

    protected abstract void config(StreamExecutionEnvironment env);

}
