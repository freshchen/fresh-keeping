package com.github.freshchen.keeping.service;

import com.github.freshchen.keeping.data.UserDTO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.github.freshchen.keeping.config.CacheConfig.UNLESS_NULL;
import static com.github.freshchen.keeping.config.CacheConfig.USER_CACHE_NAME;

/**
 * @author darcy
 * @since 2021/1/18
 **/
@Service
/**
 * cacheNames 就是 @Cacheable @CacheEvict 中的 value
 */
@CacheConfig(cacheNames = {USER_CACHE_NAME})
public class UserService {

    @Cacheable(key = "#id", unless = UNLESS_NULL)
    public UserDTO getUser(Integer id) {
        System.out.println("getUser " + id);
        UserDTO userDTO = new UserDTO();
        userDTO.setRealName("name");
        userDTO.setIdCard("id123");
        return userDTO;
    }

    /**
     * 不会放入缓存
     * @param id
     * @return
     */
    @Cacheable(key = "#id", unless = UNLESS_NULL)
    public UserDTO getUserNull(Integer id) {
        System.out.println("getUserNull " + id);
        return null;
    }

    @CacheEvict(key = "#id")
    public UserDTO save(Integer id, String name) {
        System.out.println("getUser " + id);
        UserDTO userDTO = new UserDTO();
        userDTO.setRealName(name);
        userDTO.setIdCard("id123");
        return userDTO;
    }
}
