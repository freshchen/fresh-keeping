package com.github.freshchen.keeping.dao.mapper;

import com.github.freshchen.keeping.dao.po.CountryPo;
import com.github.freshchen.keeping.dao.po.UserDetailsPo;
import com.github.freshchen.keeping.dao.po.UserPo;
import com.github.freshchen.keeping.dao.po.UserTagPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author darcy
 * @since 2021/10/08
 **/
@Mapper
public interface UserMapper {

    UserPo getUser();

    int createUser(@Param("userPo") UserPo userPo);

    UserDetailsPo getUserDetails();

    CountryPo getCountryById(@Param("id") Integer id);

    List<UserTagPo> getTagsByUserId(@Param("userId") Integer userId);
}
