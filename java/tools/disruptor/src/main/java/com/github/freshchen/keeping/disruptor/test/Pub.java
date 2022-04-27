package com.github.freshchen.keeping.disruptor.test;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author darcy
 * @since 2022/4/27
 */
@Slf4j
public class Pub {

    public static void main(String[] args) throws InterruptedException {
        Disruptor<OrderCreatedEvent> disruptor = new Disruptor<>(
                OrderCreatedEvent::new,
                2,
                DaemonThreadFactory.INSTANCE,
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );

        disruptor.handleEventsWith(new Sub1());
        disruptor.start();


        for (int i = 0; i < 100; i++) {
            disruptor.getRingBuffer().publishEvent((event, sequence, object) -> {
                event.setLocalDateTime(LocalDateTime.now());
                event.setOrderNo(UUID.randomUUID().toString());
                log.info("pub sequence {}", sequence);
            });
        }

        Thread.currentThread().join();

    }


}
