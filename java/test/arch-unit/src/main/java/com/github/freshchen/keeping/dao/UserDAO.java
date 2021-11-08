package com.github.freshchen.keeping.dao;

import com.github.freshchen.keeping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author darcy
 * @since 2021/11/06
 **/
@Repository
public class UserDAO {

    @Autowired
    private UserService userService;

    public void getUser() {
        userService.getUser();
    }
}
