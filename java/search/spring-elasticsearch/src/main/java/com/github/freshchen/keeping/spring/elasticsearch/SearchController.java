package com.github.freshchen.keeping.spring.elasticsearch;

import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.spring.elasticsearch.data.User;
import com.github.freshchen.keeping.spring.elasticsearch.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author darcy
 * @since 2022/05/21
 **/
@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{name}")
    public JsonResult<List<User>> user(@PathVariable("name") String name) {
        return JsonResult.ok(userRepository.findByNameLike(name));
    }
}
