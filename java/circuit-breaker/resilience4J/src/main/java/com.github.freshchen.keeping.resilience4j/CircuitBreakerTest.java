package com.github.freshchen.keeping.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * @author darcy
 * @since 2022/4/19
 */
@Slf4j
public class CircuitBreakerTest {

    public static void main(String[] args) {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        CircuitBreaker circuitBreaker = registry.circuitBreaker("CircuitBreakerTest");
        String s = circuitBreaker.executeSupplier(() -> "echo");
        log.info(s);
    }
}
