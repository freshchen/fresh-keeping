package com.github.freshchen.keeping.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author darcy
 * @since 2021/11/19
 */
@Service
@Slf4j
public class TestService {

    @Async
    public void test() {
        log.info("子1");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Async("asyncExecutor1")
    public void test1() {
        log.info("子2");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
