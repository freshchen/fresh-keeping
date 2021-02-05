package com.github.freshchen.keeping;

import com.github.freshchen.keeping.imports.selector.EnableDataConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@SpringBootApplication
@EnableDataConfig
@EnableAsync
public class SpringIocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringIocApplication.class, args);
    }
}
