package com.github.freshchen.keeping.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.common.lib.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 * @since 2021/10/19
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private Cache<Integer, User> userCache;

    @GetMapping("/set/1")
    public JsonResult<Void> getUser() {
        User user = new User();
        user.setId(1);
        user.setUserName("test1");
        userCache.put(1, user);
        return JsonResult.ok();
    }

    @GetMapping("/get/1")
    public JsonResult<User> getUser1() {
        User user = userCache.getIfPresent(1);
        CacheStats stats = userCache.stats();
        System.out.println(stats);
        return JsonResult.ok(user);
    }

    @GetMapping("/topic/set")
    public JsonResult<Void> getUser2() {
        return JsonResult.ok();
    }

    @GetMapping("/topic/push")
    public JsonResult<Void> getUser3() {
        return JsonResult.ok();
    }

    @GetMapping("/bloom/set")
    public JsonResult<Void> getUser4() {
        return JsonResult.ok();
    }

    @GetMapping("/bloom/get")
    public JsonResult<Void> getUser5() {
        return JsonResult.ok();
    }

    @GetMapping("/bloom/init")
    public JsonResult<Void> getUser6() {
        return JsonResult.ok();
    }

}
