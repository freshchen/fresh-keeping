package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.dao.mapper.UserMapper;
import com.github.freshchen.keeping.dao.po.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 * @since 2021/10/08
 **/
@RestController
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("user")
    public JsonResult<UserPo> getUser() {
        return JsonResult.ok(userMapper.getUser());
    }
}
