package com.github.freshchen.keeping.spring.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.freshchen.keeping.spring.mybatis.plus.po.UserPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author darcy
 * @since 2022/7/8
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPo> {

    public void insetssss();

}
