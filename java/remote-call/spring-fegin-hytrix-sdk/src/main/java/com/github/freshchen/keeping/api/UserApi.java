package com.github.freshchen.keeping.api;


import com.github.freshchen.keeping.dto.UserDTO;
import com.github.freshchen.keeping.marker.Create;
import com.github.freshchen.keeping.marker.Update;
import com.github.freshchen.keeping.model.JsonResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.github.freshchen.keeping.Router.USER;
import static com.github.freshchen.keeping.Router.USER_DELETE;

/**
 * @author darcy
 * @since 2020/08/09
 **/
@Validated
public interface UserApi {

    @PostMapping(USER)
    JsonResult<Void> create(@RequestBody @Validated(Create.class) UserDTO dto);

    @PutMapping(USER)
    JsonResult<Void> update(@RequestBody @Validated(Update.class) UserDTO dto);

    @GetMapping(USER)
    JsonResult<List<UserDTO>> find();

    @PostMapping(USER_DELETE)
    JsonResult<Void> deleteById(@PathVariable("id") @NotNull Long id);

}
