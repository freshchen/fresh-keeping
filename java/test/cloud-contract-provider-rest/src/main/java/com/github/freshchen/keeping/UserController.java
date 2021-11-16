package com.github.freshchen.keeping;

import com.github.freshchen.keeping.common.model.JsonResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping
    public JsonResult create(@RequestBody User user) {
        return JsonResult.ok();
    }

}
