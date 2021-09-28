package com.github.freshchen.keeping;

import com.github.freshchen.keeping.dto.UserDTO;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author darcy
 * @since 2021/01/16
 **/
@RestController
public class TestController {

    @Autowired
    private UserClient userClient;

    @GetMapping("/test")
    public JsonResult<List<UserDTO>> find() {
        return userClient.find();
    }
}
