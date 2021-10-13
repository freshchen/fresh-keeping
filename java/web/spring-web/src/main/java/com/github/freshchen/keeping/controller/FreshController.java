package com.github.freshchen.keeping.controller;

import com.github.freshchen.keeping.common.lib.marker.Create;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author darcy
 * @since 2020/11/29
 **/
@RestController
@Slf4j
@Api("用户")
public class FreshController {

    @PostMapping("/user")
    @ApiOperation("创建用户")
    public JsonResult<String> create(@RequestBody @Validated(Create.class) UserDTO dto) {
        log.info(dto.toString());
        return JsonResult.ok(dto.getName());
    }

    @GetMapping("/testInterceptor")
    public String testInterceptor() {
        return "testInterceptor";
    }
}
