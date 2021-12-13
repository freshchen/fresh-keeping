package com.github.freshchen.keeping.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author darcy
 * @since 2021/12/12
 **/
@Slf4j
public abstract class BaseFallbackFactory<T> implements FallbackFactory<T> {
    @Override
    public T create(Throwable cause) {
        log.warn("fallback trigger " + getClass().getName(), cause);
        return createFallback();
    }

    public abstract T createFallback();
}
