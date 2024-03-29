package com.github.freshchen.keeping.test;

import com.github.freshchen.keeping.annotations.SpiInterface;

/**
 * @author darcy
 * @since 2021/12/28
 */
@SpiInterface(defaultSpiService = EmailNotificationImpl.class)
public interface Notification {

    void send(String msg);
}
