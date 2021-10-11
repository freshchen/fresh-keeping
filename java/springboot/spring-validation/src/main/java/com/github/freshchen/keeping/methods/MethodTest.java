package com.github.freshchen.keeping.methods;

import com.github.freshchen.keeping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 * @since 2021/10/11
 */
@RestController
public class MethodTest {

    @Autowired
    private UserService userService;

    /**
     * 测试接口加注解
     */
    @GetMapping("interface")
    public void get() {
        userService.call(null);
    }
}


