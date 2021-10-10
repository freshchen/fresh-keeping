package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.common.lib.enums.Gender;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.common.lib.util.Asserts;
import com.github.freshchen.keeping.dao.mapper.UserMapper;
import com.github.freshchen.keeping.dao.po.UserDetailsPo;
import com.github.freshchen.keeping.dao.po.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author darcy
 * @since 2021/10/08
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public JsonResult<UserPo> getUser() {
        return JsonResult.ok(userMapper.getUser());
    }

    @GetMapping("/details")
    public JsonResult<UserDetailsPo> getUserDetails() {
        return JsonResult.ok(userMapper.getUserDetails());
    }

    @PostMapping
    public JsonResult<Void> createUser() {
        int effect = userMapper.createUser(createUserPo());
        Asserts.isTrue(effect == 1);
        return JsonResult.ok();
    }

    UserPo createUserPo() {
        String uuid = UUID.randomUUID().toString();
        UserPo userPo = new UserPo();
        userPo.setUserName(uuid);
        userPo.setEmail(uuid);
        userPo.setGender(Gender.MAN);
        userPo.setCountryId(1);
        return userPo;
    }
}
