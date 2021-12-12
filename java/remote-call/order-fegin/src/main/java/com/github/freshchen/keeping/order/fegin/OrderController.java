package com.github.freshchen.keeping.order.fegin;

import com.github.freshchen.keeping.api.UserApi;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author darcy
 * @since 2021/12/11
 **/
@RestController
public class OrderController implements UserApi {

    @Value("${order.time.out}")
    private Integer time;

    @Override
    public JsonResult<Void> create(UserDTO dto) {
        return null;
    }

    @Override
    public JsonResult<Void> update(UserDTO dto) {
        return null;
    }

    @Override
    public JsonResult<List<UserDTO>> find() {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(0L);
        userDTO.setName("");
        userDTO.setAge(0);
        userDTO.setEmail("");

        return JsonResult.ok(List.of(userDTO));
    }
}
