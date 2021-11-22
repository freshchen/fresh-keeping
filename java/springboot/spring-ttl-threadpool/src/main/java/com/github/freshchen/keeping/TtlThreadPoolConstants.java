package com.github.freshchen.keeping;

/**
 * @author darcy
 * @since 2021/11/19
 */
public class TtlThreadPoolConstants {

    private TtlThreadPoolConstants() {
    }

    public static final Integer CORE = Runtime.getRuntime().availableProcessors();

    public static final Integer QUEUE_CAPACITY = 2048;

}
