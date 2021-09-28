package com.github.freshchen.keeping;

import com.github.freshchen.keeping.api.UserApi;
import com.github.freshchen.keeping.dto.UserDTO;
import com.github.freshchen.keeping.model.JsonResult;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author darcy
 * @since 2021/01/16
 **/
@Component
@Slf4j
public class UserFallbackFactory implements FallbackFactory<UserApi> {
    @Override
    public UserApi create(Throwable throwable) {
        log.error("远程调用失败" + throwable.getMessage());
        return new UserFallback();
    }

    public static class UserFallback implements UserApi {

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
            return null;
        }

        @Override
        public JsonResult<Void> deleteById(@NotNull Long id) {
            return null;
        }
    }
}
