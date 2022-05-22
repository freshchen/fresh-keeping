---
begin: 2022-03-20
status: done
rating: 1
---

# 基于 Zipkin brave 制作链路采集器

## 简介

有时 Zipkin 官方 [brave/instrumentation at master · openzipkin/brave · GitHub](https://github.com/openzipkin/brave/tree/master/instrumentation) 没有我们需要的采集器此时就需要自己去实现，如下图的 Http 采样示意图，Tracer 是 brave 的核心类，客户端 Tracer 将上下文注入请求头，然后再提取到服务端 Tracer。下面我们以简单的方法调用为例参照这个流程制作自己的链路采集器。

```
   Client Tracer                                                  Server Tracer     
┌───────────────────────┐                                       ┌───────────────────────┐
│                       │                                       │                       │
│   TraceContext        │          Http Request Headers         │   TraceContext        │
│ ┌───────────────────┐ │         ┌───────────────────┐         │ ┌───────────────────┐ │
│ │ TraceId           │ │         │ X-B3-TraceId      │         │ │ TraceId           │ │
│ │                   │ │         │                   │         │ │                   │ │
│ │ ParentSpanId      │ │ Inject  │ X-B3-ParentSpanId │ Extract │ │ ParentSpanId      │ │
│ │                   ├─┼────────>│                   ├─────────┼>│                   │ │
│ │ SpanId            │ │         │ X-B3-SpanId       │         │ │ SpanId            │ │
│ │                   │ │         │                   │         │ │                   │ │
│ │ Sampling decision │ │         │ X-B3-Sampled      │         │ │ Sampling decision │ │
│ └───────────────────┘ │         └───────────────────┘         │ └───────────────────┘ │
│                       │                                       │                       │
└───────────────────────┘                                       └───────────────────────┘
```

## 同步调用

```java
@Slf4j
public class SyncCall {

    // Tracer 是 Zipkin Brave 的核心类
    private static Tracer tracer = Tracing.newBuilder()
            // TraceContext 放在 ThreadLocal
            .currentTraceContext(ThreadLocalCurrentTraceContext.create()).build().tracer();


    public static void echo(int in) {
        // 创建一个新链路，如果先前有链路则生成跨度
        Span span = tracer.nextSpan().start();
        // withSpanInScope 放入作用域，也就是每次调用都会是一个层级关系，不是平级
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            log.info("执行业务 {}", in);
            if (in < 5) {
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
```

运行结果如下，可以看到 traceId 都是相同的，查看 name、spanId、parentId，能够发现层级关系和调用顺序一致，符合预期

```shell
02:08:59.148 [main] INFO com.github.freshchen.keeping.SyncCall - 执行业务 1
02:08:59.148 [main] INFO com.github.freshchen.keeping.SyncCall - 执行业务 2
02:08:59.148 [main] INFO com.github.freshchen.keeping.SyncCall - 执行业务 3

3月 20, 2022 2:08:59 上午 brave.Tracing$LogSpanHandler end
信息: {"traceId":"1c7d80df457803bd","parentId":"e81eaaa06f3a82a2","id":"91599484a72a80f3","name":"echo-3","timestamp":1647713339146414,"duration":78,"localEndpoint":{"serviceName":"unknown","ipv4":"192.168.1.3"}}

3月 20, 2022 2:08:59 上午 brave.Tracing$LogSpanHandler end
信息: {"traceId":"1c7d80df457803bd","parentId":"1c7d80df457803bd","id":"e81eaaa06f3a82a2","name":"echo-2","timestamp":1647713339145525,"duration":35053,"localEndpoint":{"serviceName":"unknown","ipv4":"192.168.1.3"}}

3月 20, 2022 2:08:59 上午 brave.Tracing$LogSpanHandler end
信息: {"traceId":"1c7d80df457803bd","id":"1c7d80df457803bd","name":"echo-1","timestamp":1647713339138388,"duration":42757,"localEndpoint":{"serviceName":"unknown","ipv4":"192.168.1.3"}}

```


## 回调

很多时候我们需要处理回调，如下可以将开始和结束的回调放入同一个跨度

```java
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
            // 创建一个作用域中的跨度，和 try with resource 不一样 需要手动结束作用域
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
```

运行结果如下，可以看到 traceId 都是相同的，查看 name、spanId、parentId，能够发现层级关系和调用顺序一致，符合预期

```shell
02:35:41.085 [main] INFO com.github.freshchen.keeping.Callback - 执行业务 1
02:35:41.085 [main] INFO com.github.freshchen.keeping.Callback - 执行业务 2
02:35:41.085 [main] INFO com.github.freshchen.keeping.Callback - 执行业务 3

3月 20, 2022 2:35:41 上午 brave.Tracing$LogSpanHandler end
信息: {"traceId":"6e9f60f47d0b1b21","parentId":"d6e6579cc0a6d19d","id":"6bba99191aedb19f","name":"callback-3","timestamp":1647714941091342,"duration":119,"localEndpoint":{"serviceName":"unknown","ipv4":"192.168.1.3"}}

3月 20, 2022 2:35:41 上午 brave.Tracing$LogSpanHandler end
信息: {"traceId":"6e9f60f47d0b1b21","parentId":"6e9f60f47d0b1b21","id":"d6e6579cc0a6d19d","name":"callback-2","timestamp":1647714941091162,"duration":42418,"localEndpoint":{"serviceName":"unknown","ipv4":"192.168.1.3"}}

3月 20, 2022 2:35:41 上午 brave.Tracing$LogSpanHandler end
信息: {"traceId":"6e9f60f47d0b1b21","id":"6e9f60f47d0b1b21","name":"callback-1","timestamp":1647714941085600,"duration":48657,"localEndpoint":{"serviceName":"unknown","ipv4":"192.168.1.3"}}

Process finished with exit code 0

```


## 参考链接


##### 标签
#trace 