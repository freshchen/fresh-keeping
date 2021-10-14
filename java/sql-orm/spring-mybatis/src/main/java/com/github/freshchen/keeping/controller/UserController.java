package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.common.lib.enums.Gender;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.common.lib.util.Asserts;
import com.github.freshchen.keeping.dao.mapper.UserMapper;
import com.github.freshchen.keeping.dao.po.UserDetailsPo;
import com.github.freshchen.keeping.dao.po.UserPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @author darcy
 * @since 2021/10/08
 **/
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public JsonResult<UserPo> getUser() {
        return JsonResult.ok(userMapper.getUser());
    }

    @GetMapping("/list")
    public JsonResult<List<UserPo>> getUsers() {
        return JsonResult.ok(userMapper.getUsers(List.of(1,3,4)));
    }

    @GetMapping("/where")
    public JsonResult<List<UserPo>> getUserWhere() {
        log.info("userMapper.getUserChoose(null, null);");
        userMapper.getUserWhere(null, null);
        log.info("userMapper.getUserChoose(null, 1);");
        userMapper.getUserWhere(null, 1);
        log.info("userMapper.getUserChoose(1, 1);");
        return JsonResult.ok(userMapper.getUserWhere(1, 1));
    }

    @GetMapping("/gender/{gender}")
    public JsonResult<List<UserPo>> getUserByGender(@PathVariable("gender") Integer gender) {
        return JsonResult.ok(userMapper.getUserByGender(gender));
    }

    @GetMapping("/gender/null")
    public JsonResult<List<UserPo>> getUserByGender() {
        return JsonResult.ok(userMapper.getUserByGender(null));
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
