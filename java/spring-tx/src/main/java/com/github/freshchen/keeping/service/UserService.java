package com.github.freshchen.keeping.service;

import com.github.freshchen.keeping.dao.UserDao;
import com.github.freshchen.keeping.data.User;
import com.github.freshchen.keeping.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author darcy
 * @since 2021/2/4
 **/
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public List<User> findAll() {
        return userDao.findAll();
    }

    @Transactional
    public void create(User user) {
        boolean repeat = userDao.existsByName(user.getName());
        Asserts.isTrue(!repeat, "用户重复");
        userDao.save(user);
    }
}
