package com.github.freshchen.keeping.spring.mybatis.plus.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author darcy
 * @since 2021/10/10
 **/
@Data
public class UserTagPo {

    public Integer id;
    public Integer userId;
    public String tag;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
    public Boolean deleted;
}
