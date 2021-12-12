package com.github.freshchen.keeping.client;

import com.github.freshchen.keeping.api.UserApi;
import com.github.freshchen.keeping.common.lib.model.JsonResult;
import com.github.freshchen.keeping.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author darcy
 * @since 2021/01/16
 **/
@FeignClient(name = "UserClient", url = "${user.host}")
public interface UserClient extends UserApi {

    @Slf4j
    @Component
    class UserApiFallbackFactory implements FallbackFactory<UserApi> {

        @Override
        public UserApi create(Throwable cause) {
            log.error("fallback", cause);
            return new UserApiFallback();
        }

        static class UserApiFallback implements UserApi {

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
                return JsonResult.error(500, "降级");
            }
        }
    }
}
