package com.github.freshchen.keeping.service;

import org.springframework.stereotype.Service;

/**
 * @author darcy
 * @since 2021/10/11
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public void call(String string) {
        System.out.println("UserServiceImpl");
    }
}
