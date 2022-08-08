package com.github.freshchen.keeping.spring.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author darcy
 * @since 2022/6/1
 */
@AllArgsConstructor
@Getter
public enum OrderStatus {

    CREATED(0, "已创建"),
    UNPAID(1, "待支付"),
    PAID(2, "已支付"),
    PAY_FAILED(3, "支付失败");

    private int value;
    private String name;

}
