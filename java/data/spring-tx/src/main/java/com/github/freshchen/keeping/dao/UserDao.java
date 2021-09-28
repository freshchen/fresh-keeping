package com.github.freshchen.keeping.dao;

import com.github.freshchen.keeping.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author darcy
 * @since 2021/2/4
 **/
public interface UserDao extends JpaRepository<User, Integer> {
    boolean existsByName(String name);
}
