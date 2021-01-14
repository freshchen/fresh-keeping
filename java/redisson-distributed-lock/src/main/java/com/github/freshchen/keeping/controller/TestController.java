package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.model.JsonResult;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@RestController
public class TestController {

    private Integer money = 10000;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("minus")
    public JsonResult<Integer> minus() {
        RLock lock = redissonClient.getLock("11");
        boolean b;
        try {
            b = lock.tryLock(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return JsonResult.error(1, "等待");
        }
        if (b) {
            try {
                Thread.sleep(60000);
                money--;
                System.out.println(money);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } else {
            return JsonResult.error(1, "等待");
        }


        return JsonResult.ok(money);
    }

}
