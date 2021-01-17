package com.github.freshchen.keeping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author darcy
 * @since 2021/01/17
 **/
@Getter
@AllArgsConstructor
public enum Gender {

    MALE(0),
    FEMALE(1);
    private int value;
}
