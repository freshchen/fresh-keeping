package com.github.freshchen.keeping;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ThreadLocalCurrentTraceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author darcy
 * @since 2022/03/20
 **/
@Slf4j
public class SyncCall {

    // Tracer 是 Zipkin Brave 的核心类
    private static Tracer tracer = Tracing.newBuilder()
            // TraceContext 放在 ThreadLocal
            .currentTraceContext(ThreadLocalCurrentTraceContext.create()).build().tracer();


    public static void echo(int in) {
        // 创建一个新链路，如果先前有链路则生成跨度
        Span span = tracer.nextSpan().name("echo-" + in).start();
        // withSpanInScope 放入作用域，也就是每次调用都会是一个层级关系，不是平级
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            log.info("执行业务 {}", in);
            if (in < 3) {
                echo(++in);
            }
        } catch (RuntimeException | Error e) {
            span.error(e);
            throw e;
        } finally {
            // Tracing 默认构造会打印日志
            span.finish();
        }

    }

    public static void main(String[] args) {
        SyncCall.echo(1);
    }

}
