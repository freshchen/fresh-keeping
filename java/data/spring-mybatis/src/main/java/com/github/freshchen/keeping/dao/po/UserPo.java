package com.github.freshchen.keeping.dao.po;

import com.github.freshchen.keeping.common.lib.enums.Gender;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author darcy
 * @since 2021/10/09
 **/
@Data
public class UserPo {

    private Integer id;
    private Integer countryId;
    private String userName;
    private String email;
    private Gender gender;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;

}
