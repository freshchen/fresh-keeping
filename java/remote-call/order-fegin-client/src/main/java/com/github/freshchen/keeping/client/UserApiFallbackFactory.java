package com.github.freshchen.keeping.client;

import com.github.freshchen.keeping.api.UserApi;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.dto.UserDTO;

import java.util.List;

/**
 * @author darcy
 * @since 2021/12/12
 **/
public class UserApiFallbackFactory extends BaseFallbackFactory<UserApi> {
    @Override
    public UserApi createFallback() {
        return new UserApi() {
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
                return JsonResult.error(500, "error");
            }
        };
    }
}
