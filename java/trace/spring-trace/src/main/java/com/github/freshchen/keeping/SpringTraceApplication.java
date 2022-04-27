package com.github.freshchen.keeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@SpringBootApplication
@EnableAsync
public class SpringTraceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTraceApplication.class, args);
    }
}
