package com.github.freshchen.keeping.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author lingchen02
 * @since 2021/11/18
 */
@RestController
@Slf4j
public class TestController {

    private static final Executor e = Executors.newFixedThreadPool(5);

    @GetMapping("/test")
    public void test() {
        log.info("1主");
        e.execute(() -> {
            log.info("子");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        log.info("2主");
    }
}
