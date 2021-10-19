package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.common.lib.model.User;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RMapCache;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author darcy
 * @since 2021/10/19
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RedissonClient redissonClient;


    @GetMapping("/set/1")
    public JsonResult<Void> getUser() {
        RBucket<User> bucket = redissonClient.getBucket("1");
        User user = new User();
        user.setUserName("test");
        bucket.set(user, timeToLive(100_100L), TimeUnit.MILLISECONDS);
        return JsonResult.ok();
    }

    @GetMapping("/get/1")
    public JsonResult<User> getUser1() {
        RBucket<User> bucket = redissonClient.getBucket("1");
        User user = bucket.get();
        return JsonResult.ok(user);
    }

    @GetMapping("/topic/set")
    public JsonResult<Void> getUser2() {
        consumer("consumer1");
        consumer("consumer2");
        return JsonResult.ok();
    }

    @GetMapping("/topic/push")
    public JsonResult<Void> getUser3() {
        RTopic topic = redissonClient.getTopic("topic");
        topic.publish("你好");
        return JsonResult.ok();
    }

    @GetMapping("/bloom/set")
    public JsonResult<Void> getUser4() {
        RBloomFilter<Object> bloom = redissonClient.getBloomFilter("bloom");
        bloom.add("你好");
        return JsonResult.ok();
    }

    @GetMapping("/bloom/get")
    public JsonResult<Boolean> getUser5() {
        RBloomFilter<Object> bloom = redissonClient.getBloomFilter("bloom");
        return JsonResult.ok(bloom.contains("你好"));
    }

    @GetMapping("/bloom/init")
    public JsonResult<Void> getUser6() {
        RBloomFilter<Object> bloom = redissonClient.getBloomFilter("bloom");
        bloom.tryInit(55000000L, 0.03);
        return JsonResult.ok();
    }

    @GetMapping("/map/set")
    public JsonResult<Void> getUser7() {
        RMapCache<String, String> map = redissonClient.getMapCache("map");
        map.put("1", "111", timeToLive(100_100L), TimeUnit.MILLISECONDS);
        map.put("2", "222", timeToLive(50_100L), TimeUnit.MILLISECONDS);
        return JsonResult.ok();
    }

    @GetMapping("/map/get")
    public JsonResult<String> getUser8() {
        RMapCache<String, String> map = redissonClient.getMapCache("map");
        return JsonResult.ok(map.get("1"));
    }

    private void consumer(String name) {
        RTopic topic = redissonClient.getTopic("topic");
        topic.addListener(String.class, ((channel, msg) -> {
            System.out.println(name + channel + msg);
        }));
    }

    private long timeToLive(long timeToLive) {
        long i = ThreadLocalRandom.current().nextLong(1000);
        timeToLive += i;
        System.out.println("timeToLive " + timeToLive);
        return timeToLive;
    }

}
