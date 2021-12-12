package com.github.freshchen.keeping.api;


import com.github.freshchen.keeping.common.lib.marker.Create;
import com.github.freshchen.keeping.common.lib.marker.Update;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.dto.UserDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * @author darcy
 * @since 2020/08/09
 **/
@Validated
public interface UserApi {

    @PostMapping("/api/vi/user")
    JsonResult<Void> create(@RequestBody @Validated(Create.class) UserDTO dto);

    @PutMapping("/api/vi/user")
    JsonResult<Void> update(@RequestBody @Validated(Update.class) UserDTO dto);

    @GetMapping("/api/vi/user")
    JsonResult<List<UserDTO>> find();

}
