package com.github.freshchen.keeping.mall.fegin;

import com.github.freshchen.keeping.client.UserClient;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author darcy
 * @since 2021/12/11
 **/
@RestController
public class MallController {

    @Autowired
    private UserClient userClient;

    @GetMapping("/test")
    public JsonResult<List<UserDTO>> find() {
       return userClient.find();
    }

}
