package com.github.freshchen.keeping.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * @author darcy
 * @since 2021/1/18
 **/
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String USER_CACHE_NAME = "USER_CACHE_NAME";

    public static final String UNLESS_NULL = "#result==null";
}
