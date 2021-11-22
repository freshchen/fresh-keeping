package com.github.freshchen.keeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@SpringBootApplication
public class SpringWebApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SpringWebApplication.class, args);
    }
}
