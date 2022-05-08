package com.github.freshchen.keeping.spring.drools.dao;

import com.github.freshchen.keeping.spring.drools.model.Rules;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author darcy
 * @since 2021/2/4
 **/
public interface RulesDao extends JpaRepository<Rules, Integer> {
}
