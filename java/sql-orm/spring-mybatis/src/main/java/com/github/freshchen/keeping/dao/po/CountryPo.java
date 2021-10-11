package com.github.freshchen.keeping.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author darcy
 * @since 2021/10/10
 **/
@Data
public class CountryPo {

    public Integer id;
    public String name;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
    public Boolean deleted;
}
