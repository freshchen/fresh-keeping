package com.github.freshchen.keeping.spring.mybatis.plus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.freshchen.keeping.spring.mybatis.plus.mapper.UserMapper;
import com.github.freshchen.keeping.spring.mybatis.plus.po.UserPo;
import com.github.freshchen.keeping.spring.mybatis.plus.type.TypeA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author darcy
 * @since 2021/10/08
 **/
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/hello")
    public void getUser() {
        log.info("insert");
        UserPo userPo = createUserPo();
        userMapper.insert(userPo);
//
//        log.info("select one");
//        UserPo byId = userMapper.selectById(1);
//
//        log.info("update one");
//        byId.setUserName("update");
//        userMapper.updateById(byId);
//
//        log.info("select custom");
//        LambdaQueryWrapper<UserPo> query = Wrappers.lambdaQuery();
//        query.like(UserPo::getUserName, "44");
//        List<UserPo> userPos = userMapper.selectList(query);

        log.info("select page");
        LambdaQueryWrapper<UserPo> query = Wrappers.lambdaQuery();
        query.like(UserPo::getCountryId, 1);
        IPage<UserPo> page = userMapper.selectPage(new Page<>(2, 2), query);
        log.info("page {} {} {} {}", page.getTotal(), page.getCurrent(), page.getPages(), page.getRecords());
    }

    public UserPo createUserPo() {
        String uuid = UUID.randomUUID().toString();
        UserPo userPo = new UserPo();
        userPo.setUserName(uuid);
        userPo.setEmail(uuid);
        userPo.setCountryId(1);
        userPo.setGender(TypeA.A);
        return userPo;
    }
}
