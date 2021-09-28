package com.github.freshchen.keeping;

import com.github.freshchen.keeping.api.UserApi;
import com.github.freshchen.keeping.dto.UserDTO;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author darcy
 * @since 2021/01/16
 **/
@RestController
public class UserController implements UserApi {
    @Override
    public JsonResult<Void> create(UserDTO dto) {
        return JsonResult.ok();
    }

    @Override
    public JsonResult<Void> update(UserDTO dto) {
        return JsonResult.ok();
    }

    @Override
    public JsonResult<List<UserDTO>> find() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(0L);
        userDTO.setName("0");
        userDTO.setAge(0);
        userDTO.setEmail("0");
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setName("1");
        userDTO1.setAge(1);
        userDTO1.setEmail("1");
        return JsonResult.ok(List.of(userDTO, userDTO1));
    }

    @Override
    public JsonResult<Void> deleteById(@NotNull Long id) {
        return JsonResult.ok();
    }
}
