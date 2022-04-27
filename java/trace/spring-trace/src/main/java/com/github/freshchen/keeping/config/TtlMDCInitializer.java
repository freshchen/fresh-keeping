package com.github.freshchen.keeping.config;

import com.github.freshchen.keeping.logger.TtlLogbackMDCAdapter;
import lombok.SneakyThrows;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;

/**
 * @author darcy
 * @since 2021/11/19
 */
public class TtlMDCInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SneakyThrows
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Field mdcAdapter = MDC.class.getDeclaredField("mdcAdapter");
        mdcAdapter.setAccessible(true);
        mdcAdapter.set(null, new TtlLogbackMDCAdapter());
    }
}
