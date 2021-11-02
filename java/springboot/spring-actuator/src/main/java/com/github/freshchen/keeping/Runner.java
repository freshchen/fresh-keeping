package com.github.freshchen.keeping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author darcy
 * @since 2021/11/2
 */
@Component
@Slf4j
public class Runner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("Runner begin");
        // 停顿 10 秒，就绪探针不会就绪
        Thread.sleep(10000);
        log.info("Runner end");
    }
}
