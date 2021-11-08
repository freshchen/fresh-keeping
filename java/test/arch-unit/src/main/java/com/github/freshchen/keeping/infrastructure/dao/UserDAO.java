package com.github.freshchen.keeping.infrastructure.dao;

import com.github.freshchen.keeping.user.domain.service.UserDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author darcy
 * @since 2021/11/06
 **/
@Repository
public class UserDAO {

    @Autowired
    private UserDomainService userService;

    public void getUser() {
        userService.getUser();
    }
}
