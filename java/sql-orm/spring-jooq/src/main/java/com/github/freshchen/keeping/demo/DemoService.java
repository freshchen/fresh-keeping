package com.github.freshchen.keeping.demo;


import com.github.freshchen.keeping.generated.tables.pojos.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author darcy
 */
public interface DemoService {
    /**
     * 删除
     */
    @Transactional
    public void delete(int id);

    /**
     * 增加
     */
    @Transactional
    public void insert(User user);

    /**
     * 更新
     */
    @Transactional
    public int update(User user);

    /**
     * 查询单个
     */
    public User selectById(int id);

    /**
     * 查询全部列表
     */
    public List<User> selectAll();
}
