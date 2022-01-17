---
begin: 2022-01-16
status: ongoing
rating: 1
---

# 分布式链路追踪系统 Zipkin 架构简介

## 简介

[OpenZipkin · A distributed tracing system](https://zipkin.io/)
[GitHub - openzipkin/zipkin: Zipkin is a distributed tracing system](https://github.com/openzipkin/zipkin)

Zipkin是一个分布式追踪系统。它有助于收集数据帮助定位问题或者分析系统性能瓶颈。其主要设计思想借鉴于谷歌的一篇论文 [分布式链路追踪论文-Dapper阅读笔记](分布式链路追踪论文-Dapper阅读笔记.md) 
可以将 traceId 记录在日志中，出现问题时通过日志文件中的 traceId 在 Zipkin 搜索到整条调用链。此外，还可以根据服务名、操作名称、标签、持续时间等属性进行查询。如图一所示可以看出 Zipkin 可以记录 http、memcache、mysql等多种调用

![](image/Pasted%20image%2020220116202455.png)

如图二所示 Zipkin 还可以基于 trace 数据绘制出整个系统的依赖关系，可以帮助分析问题，新人快速了解和学习系统。
![](image/Pasted%20image%2020220116202748.png)


## 总体架构

简介中可以看出 Zipkin 的 UI 还是相当美观的，下面从整体架构来分析下时如何实现的。
Zipkin 通过一组仪表点检测基础库，收集并串联 Span 元数据信息。各个检测库对业务透明，并且开销很小，默认仅传递 span 相关的核心数据，例如 traceId 和 spanId。

Trace 由检测基础库生成完之后，由 Reporter 上报到 Zipkin 的收集器 Collectors，收集器将 trace 信息存储，Zipkin Web UI 页面通过 Zipkin API 服务实现 trace 的检索功能。总流程如下图所示：
![](image/Pasted%20image%2020220116210226.png)

再看一个具体的 Http 请求的采集流程案例：
```
┌─────────────┐ ┌───────────────────────┐  ┌─────────────┐  ┌──────────────────┐
│ User Code   │ │ Trace Instrumentation │  │ Http Client │  │ Zipkin Collector │
└─────────────┘ └───────────────────────┘  └─────────────┘  └──────────────────┘
       │                 │                         │                 │
           ┌─────────┐
       │ ──┤GET /foo ├─▶ │ ────┐                   │                 │
           └─────────┘         │ record tags
       │                 │ ◀───┘                   │                 │
                           ────┐
       │                 │     │ add trace headers │                 │
                           ◀───┘
       │                 │ ────┐                   │                 │
                               │ record timestamp
       │                 │ ◀───┘                   │                 │
                             ┌─────────────────┐
       │                 │ ──┤GET /foo         ├─▶ │                 │
                             │X-B3-TraceId: aa │     ────┐
       │                 │   │X-B3-SpanId: 6b  │   │     │           │
                             └─────────────────┘         │ invoke
       │                 │                         │     │ request   │
                                                         │
       │                 │                         │     │           │
                                 ┌────────┐          ◀───┘
       │                 │ ◀─────┤200 OK  ├─────── │                 │
                           ────┐ └────────┘
       │                 │     │ record duration   │                 │
            ┌────────┐     ◀───┘
       │ ◀──┤200 OK  ├── │                         │                 │
            └────────┘       ┌────────────────────────────────┐
       │                 │ ──┤ asynchronously report span     ├────▶ │
                             │                                │
                             │{                               │
                             │  "traceId": "aa",              │
                             │  "id": "6b",                   │
                             │  "name": "get",                │
                             │  "timestamp": 1483945573944000,│
                             │  "duration": 386000,           │
                             │  "annotations": [              │
                             │--snip--                        │
                             └────────────────────────────────┘
```

综上 Zipkin server 端四大核心组件是：
-   collector
-   storage
-   search
-   web UI

## 仪表点检测库选择

Zipkin官方以及社区提供了很多开箱即用的仪表点检测库，可以参照官网按修获取。

[Tracers and Instrumentation · OpenZipkin](https://zipkin.io/pages/tracers_instrumentation.html)

## Zipkin Server 端选择

上报方式  http、kafka、rabbitmq 可供选择。
链路数据的后端存储 MySQL、 Cassandra、Elasticsearch 可供选择。
	Zipkin 还可以与社区中其他活跃的链路收集系统集成 Skywalking、Jaeger、Pitchfork 可供选择

## 开发接口

[Swagger UI](https://zipkin.io/zipkin-api/#/)

## 仪表点检测库核心概念

仪表点检测库主要包括以下几个部分
- 核心数据：需要上报给 Zipkin 的核心链路信息
- 标识符：也就是 trace 或 span 的唯一ID
- 时间戳和持续时间

### 核心数据

**注解**：主要用于标注事件发送的时间
注解主要分为四种类型
- cs：客户端发起请求
- sr：服务端接受请求
- ss：服务端发送响应
- cr：客户端接受响应
如果不是 rpc 或者 http 而是消息队列通讯，那又可以定义两种注解类型
- ms：消息发送
- mr：消息接受

**二进制注解**：主要标识一些辅助信息，可以帮助定位问题或者检索 trace。例如请求的 url

**Span**：跨度又一系列注解以及二进制注解组成，主要包括 traceId, spanId, parentId 和 span name。切忌在 span 中存储与链路追踪无关的数据。

**Trace**：调用链树，由多个 Span 组成，多个 Span 拥有相同的 traceId，并且通过 parentId 串联层级关系

### 标识符

**Trace Id**：64 或者 128 位的 ID，同链路 Span 共享

**Span Id**：64 或者 128 位的 ID，Span Id 和 Trace Id 可能相同

**Parent Id**：如果 Span 有层级关系，此字段记录父 Span Id

### 数据传播

在服务间传播的数据主要包括三个标识符，是否采样，是否调试的 Flag。默认的通用的 B3 传播标准如下所述

[GitHub - openzipkin/b3-propagation: Repository that describes and sometimes implements B3 propagation](https://github.com/openzipkin/b3-propagation)

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

### 时间戳和持续时间

为了保证精度 Zipkin 所有的时间戳都是微秒，跨度的持续时间依然是微秒。对于客户端和服务端重复上报的 span，因为客户端时发起者，所以在客户端记录时间戳和持续时间。

### 消息类型链路

消息链路和 rpc 或者 http 调用不同。因为可能有多个消费者，因此生产者和消费者不共享 spanId。并且消息也不存在响应，因此只有消息发送ms 和消息接受 mr。针对消息链路的上报流程如下所示，生产者并不等待请求记录持续时间，而是直接上报，然后消费者们生成子 span，完成后直接上报，由 Zipkin Server 去计算耗时。

```
   Producer Tracer                                    Consumer Tracer
+------------------+                               +------------------+
| +--------------+ |     +-----------------+       | +--------------+ |
| | TraceContext |======>| Message Headers |========>| TraceContext | |
| +--------------+ |     +-----------------+       | +--------------+ |
+--------||--------+                               +--------||--------+
   start ||                                                 ||
         \/                                          finish ||
span(context).annotate("ms")                                \/
             .address("ma", broker)          span(context).annotate("mr")
                                                          .address("ma", broker)
```

## 参考链接


##### 标签
#trace 
