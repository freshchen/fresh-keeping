package com.github.freshchen.keeping.disruptor.test;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author darcy
 * @since 2022/4/27
 */
@Slf4j
public class Sub1 implements EventHandler<OrderCreatedEvent> {
    @Override
    public void onEvent(OrderCreatedEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("sub1 event [{}] sequence [{}] endOfBatch [{}]", event, sequence, endOfBatch);
        Thread.sleep(1000);
    }
}
