package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.data.UserDTO;
import com.github.freshchen.keeping.model.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 * @since 2021/01/14
 **/
@RestController
public class TestController {

    @GetMapping("/user")
    public JsonResult<UserDTO> get() {
        return JsonResult.ok();
    }


}
