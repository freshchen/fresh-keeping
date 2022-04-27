package com.github.freshchen.keeping.disruptor.test;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author darcy
 * @since 2022/4/27
 */
@Data
public class OrderCreatedEvent {

    private LocalDateTime localDateTime;
    private String orderNo;

}
