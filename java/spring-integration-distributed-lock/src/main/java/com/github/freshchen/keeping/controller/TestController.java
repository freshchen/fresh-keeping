package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.model.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Lock;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@RestController
public class TestController {

    /**
     * 模拟抢红包 总共 10000 元，每次抢一元，抢完为止
     * 分析： 此场景无法做幂等，且并发场景会出现超卖
     */
    private Integer money = 10000;
    private static final String RED_PACKET_SN = "redPacketSn1";

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    /**
     * 相比 redisson 结合了本地锁，减少了redis的压力，而且使用起来更简单
     * 使用类似 https://github.com/freshchen/fresh-keeping/tree/master/java/redisson-distributed-lock
     * 缺点，没有读写锁。没有看门口狗续租，超时时间设置变得很中重要
     *
     * @return
     */
    @GetMapping("minus")
    public JsonResult<String> minus() {
        // 获取锁
        Lock lock = redisLockRegistry.obtain(RED_PACKET_SN);
        lock.lock();
        try {
            assert money > 0;
            // 可以看 redis 中 RED_PACKET_SN 会续租
            sleepSeconds(15);
            money--;
        } finally {
            lock.unlock();
        }
        return JsonResult.ok(String.valueOf(money));
    }

    private void sleepSeconds(Integer time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
