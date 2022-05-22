package com.github.freshchen.keeping.spring.elasticsearch.service;

import com.github.freshchen.keeping.spring.elasticsearch.data.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author darcy
 * @since 2022/05/21
 **/
public interface UserRepository extends CrudRepository<User, String> {
    List<User> findByNameLike(String name);
}
