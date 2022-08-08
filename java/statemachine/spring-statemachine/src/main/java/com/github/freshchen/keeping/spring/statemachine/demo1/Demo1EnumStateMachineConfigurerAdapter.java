package com.github.freshchen.keeping.spring.statemachine.demo1;

import com.github.freshchen.keeping.spring.statemachine.OrderEvent;
import com.github.freshchen.keeping.spring.statemachine.OrderStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * @author darcy
 * @since 2022/6/1
 */
@Configuration
@EnableStateMachine
public class Demo1EnumStateMachineConfigurerAdapter
        extends EnumStateMachineConfigurerAdapter<OrderStatus, OrderEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderEvent> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states)
            throws Exception {
        states
                .withStates()
                .initial(OrderStatus.CREATED)
                .states(EnumSet.allOf(OrderStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(OrderStatus.CREATED).target(OrderStatus.UNPAID).event(OrderEvent.PAY)
                .and()
                .withExternal()
                .source(OrderStatus.UNPAID).target(OrderStatus.PAID).event(OrderEvent.PAY_SUCCESS)
                .and()
                .withExternal()
                .source(OrderStatus.UNPAID).target(OrderStatus.PAY_FAILED).event(OrderEvent.PAY_FAILURE);
    }

    @Bean
    public StateMachineListener<OrderStatus, OrderEvent> listener() {
        return new StateMachineListenerAdapter<OrderStatus, OrderEvent>() {
            @Override
            public void stateChanged(State<OrderStatus, OrderEvent> from, State<OrderStatus, OrderEvent> to) {
                System.out.printf("State change from %s to %s%n", from, to);
            }
        };
    }

}
