package com.github.freshchen.keeping.spring.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author darcy
 * @since 2022/05/29
 **/
@Component
@Slf4j
public class DemoConsumer {

    @KafkaListener(topics = {"demo"})
    public void onMessage(ConsumerRecord<?, ?> consumerRecord) {
        Optional.ofNullable(consumerRecord.value())
                .ifPresent(msg -> log.info("message:{}", msg));
    }
}
