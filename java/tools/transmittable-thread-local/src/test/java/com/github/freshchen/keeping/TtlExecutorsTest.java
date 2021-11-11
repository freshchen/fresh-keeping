package com.github.freshchen.keeping;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

/**
 * @author darcy
 * @since 2021/11/10
 */
public class TtlExecutorsTest {

    @Test
    @DisplayName("ThreadLocal 不会传递")
    public void testThreadLocal() throws InterruptedException {
        ThreadLocal<String> context = new ThreadLocal<>();
        context.set("主线程上下文");
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        IntStream.range(0, 10).forEach(value -> {
            pool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " context:" + context.get());
            });

        });
        Thread.sleep(100000);
    }

    @Test
    @DisplayName("ThreadLocal 可以传递，但是设置新值不会感应" +
        "因为线程池是重复利用的线程的，只有创建线程时 InheritableThreadLocal 才生效")
    public void testInheritableThreadLocal() throws InterruptedException {
        InheritableThreadLocal<String> context = new InheritableThreadLocal<>();
        context.set("主线程上下文");
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        IntStream.range(0, 10).forEach(value -> {
            pool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " context:" + context.get());
            });

        });
        context.set("新的主线程上下文");
        IntStream.range(0, 10).forEach(value -> {
            pool.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " context:" + context.get());
            });

        });
        Thread.sleep(100000);
    }

    @Test
    @DisplayName("TransmittableThreadLocal 可以每次都把上下文带进去")
    public void testTransmittableThreadLocal() throws InterruptedException {
        TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();
        context.set("主线程上下文");
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        Executor ttlExecutor = TtlExecutors.getTtlExecutor(pool);
        IntStream.range(0, 10).forEach(value -> {
            ttlExecutor.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " context:" + context.get());
            });

        });
        context.set("新的主线程上下文");
        IntStream.range(0, 10).forEach(value -> {
            ttlExecutor.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " context:" + context.get());
            });

        });
        Thread.sleep(100000);
    }
}
