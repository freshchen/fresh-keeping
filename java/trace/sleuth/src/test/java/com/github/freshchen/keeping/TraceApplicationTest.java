package com.github.freshchen.keeping;

import brave.propagation.CurrentTraceContext;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class TraceApplicationTest {

    @Test
    @DisplayName("测试 ThreadLocalCurrentTraceContext")
    public void test1() {
        CurrentTraceContext currentTraceContext = ThreadLocalCurrentTraceContext.create();
        // 当前无上下文
        Assertions.assertEquals(null, currentTraceContext.get());
        TraceContext tc1 = TraceContext.newBuilder().traceId(1).spanId(1).build();
        // maybeScope 会调用 newScope
        try (CurrentTraceContext.Scope scope1 = currentTraceContext.maybeScope(tc1)) {
            // 先前无作用域所以是 RevertToNullScope
            Assertions.assertEquals("RevertToNullScope", scope1.getClass().getSimpleName());
            // try 块内就是作用域内, 上下文内应该都是 tc1
            Assertions.assertEquals(tc1, currentTraceContext.get());
            TraceContext tc2 = TraceContext.newBuilder().traceId(2).spanId(2).build();
            // 嵌套作用域
            try (CurrentTraceContext.Scope scope2 = currentTraceContext.maybeScope(tc2)) {
                // 先前有作用域所以是 RevertToPreviousScope
                Assertions.assertEquals("RevertToPreviousScope", scope2.getClass().getSimpleName());
                // try 块内就是作用域内, 上下文内应该都是 tc2
                Assertions.assertEquals(tc2, currentTraceContext.get());
                // 测试跨线程，使用默认包装
                Executor executor = currentTraceContext.executor(Executors.newFixedThreadPool(1));
                // 这里隐式的又嵌套了一个作用域，Runnable 前设置了 tc2，Runnable 内上下文内应该也是 tc2
                executor.execute(() -> Assertions.assertEquals(tc2, currentTraceContext.get()));
                // 跨线程隐式的上下文结束，检查上下文是否还原回 tc2
                Assertions.assertEquals(tc2, currentTraceContext.get());
            }
            // 作用域结束了，因为先前有作用域所以会还原
            Assertions.assertEquals(tc1, currentTraceContext.get());
        }
        // 作用域结束了，因为先前无作用域所以还是 null
        Assertions.assertEquals(null, currentTraceContext.get());
    }

}
