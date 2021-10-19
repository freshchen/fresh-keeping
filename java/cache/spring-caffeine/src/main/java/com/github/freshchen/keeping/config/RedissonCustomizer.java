package com.github.freshchen.keeping.config;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;

/**
 * @author darcy
 * @since 2021/10/19
 */
public class RedissonCustomizer implements RedissonAutoConfigurationCustomizer {

    @Override
    public void customize(Config configuration) {
        configuration.setCodec(JsonJacksonCodec.INSTANCE);
    }
}
