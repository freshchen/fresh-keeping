package com.github.freshchen.keeping.common.lib.model;

import com.github.freshchen.keeping.common.lib.enums.Gender;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author darcy
 * @since 2021/10/09
 **/
@Data
public class User {

    private Integer id;
    private String userName;
    private String email;
    private Gender gender;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;

}
