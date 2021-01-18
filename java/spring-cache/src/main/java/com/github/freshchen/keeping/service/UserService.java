package com.github.freshchen.keeping.service;

import com.github.freshchen.keeping.data.UserDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.github.freshchen.keeping.config.CacheConfig.UNLESS_NULL;
import static com.github.freshchen.keeping.config.CacheConfig.USER_CACHE_NAME;

/**
 * @author darcy
 * @since 2021/1/18
 **/
@Service
@CacheConfig(cacheNames = {USER_CACHE_NAME})
public class UserService {

    @Cacheable(unless = UNLESS_NULL)
    public UserDTO getUser(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setRealName("name");
        userDTO.setIdCard("id123");
        return userDTO;
    }
}
