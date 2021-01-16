package com.github.freshchen.keeping;

import com.github.freshchen.keeping.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author darcy
 * @since 2021/01/16
 **/
@FeignClient(
        name = "UserClient",
        url = "${user.host}",
        fallbackFactory = UserFallbackFactory.class
)
public interface UserClient extends UserApi {
}
