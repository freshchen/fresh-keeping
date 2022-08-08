package com.github.freshchen.keeping.spring.mybatis.plus.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.freshchen.keeping.spring.mybatis.plus.type.TypeA;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author darcy
 * @since 2021/10/09
 **/
@Data
@TableName("user")
public class UserPo {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer countryId;
    private String userName;
    private String email;
    private TypeA gender;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;

}
