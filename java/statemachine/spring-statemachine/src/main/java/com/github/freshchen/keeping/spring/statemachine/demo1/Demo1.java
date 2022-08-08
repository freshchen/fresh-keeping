package com.github.freshchen.keeping.spring.statemachine.demo1;

import com.github.freshchen.keeping.spring.statemachine.OrderEvent;
import com.github.freshchen.keeping.spring.statemachine.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author darcy
 * @since 2022/6/1
 */
@Component
public class Demo1 implements ApplicationRunner {

    @Autowired
    private StateMachine<OrderStatus, OrderEvent> demo1StateMachine;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        demo1StateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(OrderEvent.PAY).build())).subscribe();
        demo1StateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(OrderEvent.PAY_SUCCESS).build())).subscribe();
    }
}
