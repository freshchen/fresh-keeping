package com.github.freshchen.keeping.dao.mapper;

import com.github.freshchen.keeping.dao.po.UserDetailsPo;
import com.github.freshchen.keeping.dao.po.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author darcy
 * @since 2021/10/08
 **/
@Mapper
public interface UserMapper {

    UserPo getUser();

    int createUser(@Param("userPo") UserPo userPo);

    UserDetailsPo getUserDetails();
}
