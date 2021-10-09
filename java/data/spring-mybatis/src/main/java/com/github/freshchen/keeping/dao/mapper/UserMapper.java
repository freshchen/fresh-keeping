package com.github.freshchen.keeping.dao.mapper;

import com.github.freshchen.keeping.dao.po.UserPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author darcy
 * @since 2021/10/08
 **/
@Mapper
public interface UserMapper {

    UserPo getUser();

}
