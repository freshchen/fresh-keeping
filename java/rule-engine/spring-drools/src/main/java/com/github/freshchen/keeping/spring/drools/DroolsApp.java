package com.github.freshchen.keeping.spring.drools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author darcy
 * @since 2022/4/28
 */
@SpringBootApplication
@EnableScheduling
public class DroolsApp {

    public static void main(String[] args) {
        SpringApplication.run(DroolsApp.class);
    }
}
