package com.github.freshchen.keeping;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ThreadLocalCurrentTraceContext;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author darcy
 * @since 2022/03/20
 **/
@Slf4j
public class Callback {


    // Tracer 是 Zipkin Brave 的核心类
    private static Tracer tracer = Tracing.newBuilder()
            // TraceContext 放在 ThreadLocal
            .currentTraceContext(ThreadLocalCurrentTraceContext.create()).build().tracer();

    public static void echo(int in, Consumer<Map<Object, Object>> onStart, Consumer<Map<Object, Object>> onFinish) {
        HashMap<Object, Object> attr = Maps.newHashMap();
        attr.put("in", in);
        onStart.accept(attr);
        log.info("执行业务 {}", in);
        if (in < 3) {
            echo(++in, onStart, onFinish);
        }
        onFinish.accept(attr);
    }

    public static void main(String[] args) {
        Consumer<Map<Object, Object>> onStart = attr -> {
            ScopedSpan scopedSpan = tracer.startScopedSpan("callback-" + attr.get("in"));
            attr.put("span", scopedSpan);
        };
        Consumer<Map<Object, Object>> onFinish = attr -> {
            ScopedSpan scopedSpan = (ScopedSpan) attr.get("span");
            scopedSpan.finish();
        };
        Callback.echo(1, onStart, onFinish);
    }

}
