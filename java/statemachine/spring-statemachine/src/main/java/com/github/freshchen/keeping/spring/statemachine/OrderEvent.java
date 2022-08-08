package com.github.freshchen.keeping.spring.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author darcy
 * @since 2022/6/1
 */
@AllArgsConstructor
@Getter
public enum OrderEvent {

    CREATE(0, "创建"),
    PAY(1, "支付"),
    PAY_SUCCESS(2, "支付成功"),
    PAY_FAILURE(2, "支付失败");

    private int value;
    private String name;

}
