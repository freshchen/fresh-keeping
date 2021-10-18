package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.common.lib.model.User;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        RBucket<User> bucket = redissonClient.getBucket("1", JsonJacksonCodec.INSTANCE);
        User user = new User();
        user.setUserName("test");
        bucket.set(user, 100, TimeUnit.SECONDS);
        return JsonResult.ok();
    }

}
