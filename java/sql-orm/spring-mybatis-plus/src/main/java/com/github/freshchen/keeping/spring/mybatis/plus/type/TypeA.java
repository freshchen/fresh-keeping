package com.github.freshchen.keeping.spring.mybatis.plus.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author darcy
 * @since 2022/7/11
 */
@AllArgsConstructor
@Getter
public enum TypeA {
    A(1),
    B(2);

    private Integer code;
}
