package com.github.freshchen.keeping.test;

import com.github.freshchen.keeping.annotations.SpiService;

/**
 * @author lingchen02
 * @since 2021/12/28
 */
@SpiService
public class SmsNotificationImpl implements Notification {
    @Override
    public void send(String msg) {
        System.out.println("SmsNotificationImpl " + msg);
    }
}
