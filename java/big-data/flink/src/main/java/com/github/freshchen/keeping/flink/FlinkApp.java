package com.github.freshchen.keeping.flink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author darcy
 * @since 2022/05/08
 **/
@SpringBootApplication
@Slf4j
public class FlinkApp {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(FlinkApp.class, args);
        log.info("FlinkApp Started");
        Thread.currentThread().join();
    }
}
