package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.data.User;
import com.github.freshchen.keeping.model.JsonResult;
import com.github.freshchen.keeping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public JsonResult<List<User>> findAll() {
        List<User> users = userService.findAll();
        return JsonResult.ok(users);
    }

    @PostMapping("/user")
    public JsonResult<Void> create(@RequestBody User user) {
        userService.create(user);
        return JsonResult.ok();
    }


}
