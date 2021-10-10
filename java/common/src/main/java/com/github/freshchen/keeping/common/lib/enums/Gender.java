package com.github.freshchen.keeping.common.lib.enums;

import lombok.AllArgsConstructor;

/**
 * @author darcy
 * @since 2021/10/09
 **/
@AllArgsConstructor
public enum Gender implements IEnum {

    /**
     * 男
     */
    MAN(0, "男"),
    /**
     * 女
     */
    FEMALE(1, "女");

    private int value;
    private String description;

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
