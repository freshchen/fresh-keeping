package com.github.freshchen.keeping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author darcy
 * @since 2020/8/7
 **/
@AllArgsConstructor
@Getter
public enum TypeDTO {

    ONE(1),
    TWO(2);
    private int value;
}
