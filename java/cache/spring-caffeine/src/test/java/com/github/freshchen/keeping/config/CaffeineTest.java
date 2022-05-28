package com.github.freshchen.keeping.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author darcy
 * @since 2022/5/28
 */
class CaffeineTest {

    @Test
    @DisplayName("手动加载")
    public void test1() {
        Cache<String, String> cache = Caffeine.newBuilder().build();
        // 查找一个缓存元素， 没有查找到的时候返回null
        System.out.println(cache.getIfPresent("name"));
        cache.put("name", "darcy");
        System.out.println(cache.getIfPresent("name"));
        cache.invalidate("name");
        System.out.println(cache.getIfPresent("name"));
    }

    @Test
    @DisplayName("自动加载")
    public void test2() {
        // 要用 LoadingCache，不存在的话会自动 build
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .build(key -> {
                    String value = key + "-value";
                    System.out.println("build: " + value);
                    return value;
                });
        // 查找一个缓存元素， 没有查找到的时候返回null
        System.out.println(cache.get("name"));
    }

    @SneakyThrows
    @Test
    @DisplayName("容量驱逐加载")
    public void test3() {
        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(1)
                .build();
        cache.put("name", "darcy");
        cache.put("home", "sh");
        // 有个清理过程，太快会来不及清理
        Thread.sleep(1000);
        System.out.println(cache.asMap().keySet());
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));

    }

    @SneakyThrows
    @Test
    @DisplayName("时间驱逐加载 expireAfterAccess")
    public void test4() {
        // 一个元素在上一次读写操作后一段时间之后，在指定的时间后没有被再次访问将会被认定为过期项。
        // 在当被缓存的元素时被绑定在一个session上时，当session因为不活跃而使元素过期的情况下，这是理想的选择。
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.SECONDS)
                .build();
        cache.put("name", "darcy");
        cache.put("home", "sh");
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));
        Thread.sleep(500);
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));
        Thread.sleep(500);
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));
        Thread.sleep(500);
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));
        Thread.sleep(1000);
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));

    }

    @SneakyThrows
    @Test
    @DisplayName("时间驱逐加载 expireAfterWrite")
    public void test5() {
        // 一个元素将会在其创建或者最近一次被更新之后的一段时间后被认定为过期项。
        // 在对被缓存的元素的时效性存在要求的场景下，这是理想的选择。
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build();
        cache.put("name", "darcy");
        cache.put("home", "sh");
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));
        Thread.sleep(500);
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));
        Thread.sleep(500);
        System.out.println(cache.getIfPresent("name"));
        System.out.println(cache.getIfPresent("home"));

    }

    @SneakyThrows
    @Test
    @DisplayName("时间驱逐加载 refreshAfterWrite")
    public void test6() {
        // 将会使在写操作之后的一段时间后允许key对应的缓存元素进行刷新，
        // 但是只有在这个key被真正查询到的时候才会正式进行刷新操作
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .refreshAfterWrite(1, TimeUnit.SECONDS)
                .build(key -> {
                    String value = key + System.currentTimeMillis();
                    return value;
                });
        System.out.println(cache.get("name"));
        Thread.sleep(500);
        System.out.println(cache.get("name"));
        Thread.sleep(500);
        System.out.println(cache.get("name"));
        Thread.sleep(50);
        System.out.println(cache.get("name"));
        cache.put("name", "custom");
        System.out.println(cache.get("name"));
        Thread.sleep(500);
        System.out.println(cache.get("name"));
        Thread.sleep(500);
        System.out.println(cache.get("name"));
        Thread.sleep(50);
        System.out.println(cache.get("name"));

    }

}