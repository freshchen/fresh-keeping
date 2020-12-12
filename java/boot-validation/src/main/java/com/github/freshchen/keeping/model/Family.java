package com.github.freshchen.keeping.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author darcy
 * @since 2020/02/20
 **/
@Data
public class Family {

    @NotNull(message = "家庭住址不能为空")
    private String address;
}
