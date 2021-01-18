package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.data.UserDTO;
import com.github.freshchen.keeping.model.JsonResult;
import com.github.freshchen.keeping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    private JsonResult<UserDTO> getUser(@PathVariable("id") Integer id) {
        return JsonResult.ok(userService.getUser(id));
    }

}
