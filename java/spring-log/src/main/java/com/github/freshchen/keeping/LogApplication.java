package com.github.freshchen.keeping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author darcy
 * @since 2021/9/15
 */
@SpringBootApplication
@Slf4j
public class LogApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
        log.info("test info");
        log.debug("test debug");
        log.trace("test trace");
        log.warn("test warn");
        log.error("test error");
    }
}
