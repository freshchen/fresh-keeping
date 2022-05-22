---
begin: 2022-03-07
status: done
rating: 1
---

# Zipkin 跨度 Span 源码分析

## 前言

先前我们通过 [分布式链路追踪系统 Zipkin 简介](分布式链路追踪系统%20Zipkin%20简介.md) 了解了链路追踪的基本概念，并且通过 [Zipkin 链路上下文源码分析](Zipkin%20链路上下文源码分析.md) [Zipkin 链路上下文作用域源码分析](Zipkin%20链路上下文作用域源码分析.md) 了解了链路上下文以及其作用域的相关代码，今天我们继续看跨度 Span 相关的代码。简单回顾一下，一个跨度可以理解成一次调用，也就是 Zipkin UI 上的一行。

首先让我们回顾 [分布式链路追踪论文-Dapper阅读笔记](分布式链路追踪论文-Dapper阅读笔记.md) 中对于跨度的描述，如图所示就是一次调用在链路收集系统比较关键的信息，Span 是对跨度的抽象，因此可想而知就是记录图中信息的。
![图3](image/Pasted%20image%2020220115230819.png)

# 源码分析

> spring-boot：2.5.6
> spring-cloud：2020.0.4
> spring-cloud-sleuth：3.0.4
> zipkin-brave：5.13.2

## SpanCustomizer

跨度的最顶层是 SpanCustomizer 接口，主要包含几个关键信息修改方法

```java
public interface SpanCustomizer {
  // 设置跨度名称
  SpanCustomizer name(String name);

  // 增加标签
  SpanCustomizer tag(String key, String value);

  // 增加注解
  SpanCustomizer annotate(String value);
}
```

从图中可以更为直接的了解我们设置的值将如何展示，另外 Zipkin UI 是支持根据标签查询的，因此标签非常有用

![](image/Pasted%20image%2020220308000133.png)


## Span

Span 是个抽象类，实现了 SpanCustomizer 接口。Span 中包含了一个枚举，列出了跨度的四种类型

```java
public enum Kind {
    CLIENT, // 服务调用方上传的跨度类型
    SERVER, // 服务提供方上传的跨度类型
    PRODUCER, // 消息生产者上传的跨度类型
    CONSUMER // 消息消费者上传的跨度类型
  }
```

Span 中的抽象方法

```java
// 是否丢弃，不采样
public abstract boolean isNoop();
// Span 中包含先前文章中提到的链路上下文
public abstract TraceContext context();
// 返回修改接口
public abstract SpanCustomizer customizer();
// 开始计时
public abstract Span start();
public abstract Span start(long timestamp);
// 实现 SpanCustomizer 方法，设置跨度名称
@Override public abstract Span name(String name);
// 设置跨度类型
public abstract Span kind(@Nullable Kind kind);
// 实现 SpanCustomizer 方法，增加注解
@Override public abstract Span annotate(String value);
public abstract Span annotate(long timestamp, String value);
// 实现 SpanCustomizer 方法，增加标签
@Override public abstract Span tag(String key, String value);
// 记录调用过程中发生的异常，链路采集记录异常后会再抛出
public abstract Span error(Throwable throwable);
// 服务提供方的服务名称
public abstract Span remoteServiceName(String remoteServiceName);
// 服务提供方的地址
public abstract boolean remoteIpAndPort(@Nullable String remoteIp, int remotePort);
// 结束计时并上报
public abstract void finish();
// 丢弃，不上报
public abstract void abandon();
// 结束计时并上报
public abstract void finish(long timestamp);
// 不等结束，强制上报，一般不使用
public abstract void flush();
```

## ScopedSpan

ScopedSpan 也是一个抽象类，用于不用 try-with-resource 写法设置作用域的写法。ScopedSpan 的实现类和 Span 的实现类大同小异，主要是把作用域集成到 Span 中，并在代码中显式地开启、关闭。因此不过多介绍，下面主要介绍 Span 的三个具体实现类

## NoopSpan

如果决定不采样，链路上下文就会转为 NoopSpan，对应 Span 方法均直接返回，不执行实际操作
```java

final class NoopSpan extends Span {

  final TraceContext context;

  NoopSpan(TraceContext context) {
    this.context = context;
  }

  @Override public SpanCustomizer customizer() {
    return NoopSpanCustomizer.INSTANCE;
  }

  。。。 。。。
}
```

## LazySpan

LazySpan 的作用是，有时候我们通过 Tracer （zipkin brave 的核心 api，后续继续介绍）的 currentSpan 获取当前跨度，获取当前跨度后不一定要继续执行操作有可能只是看一下先前是不是有跨度了。

```java
final class LazySpan extends Span {
  // brave 核心 api
  final Tracer tracer;
  TraceContext context;
  Span delegate;

  LazySpan(Tracer tracer, TraceContext context) {
    this.tracer = tracer;
    this.context = context;
  }

  。。。 。。。
  //所有接口实现都使用这种方式先调用内部 span 方法获取真实 span 再操作
  @Override public boolean isNoop() {
    return span().isNoop();
  }

  // 核心方法
  Span span() {
    Span result = delegate;
    // 懒初始化 
    if (result != null) return result;
    // 结果可能是 NoopSpan 或者 RealSpan
    delegate = tracer.toSpan(context);
    context = delegate.context();
    return delegate;
  }

}
```


## RealSpan

RealSpan 是 zipkin brave 中需要采样上报的跨度，主要包括四个字段

```java
// 链路上下文
final TraceContext context;
// 待上报跨度
final PendingSpans pendingSpans;
// 真正存放跨度详细信息的地方
final MutableSpan state;
// 计时器
final Clock clock;
```

RealSpan 的创建缩略代码如下，可见 pendingSpans 存放了很多待上传跨度 PendingSpan，MutableSpan 来自 PendingSpan

```java
PendingSpan pendingSpan = pendingSpans.getOrCreate(parent, context, false);
TraceContext pendingContext = pendingSpan.context();
if (pendingContext != null) context = pendingContext;
new RealSpan(context, pendingSpans, pendingSpan.state(), pendingSpan.clock());
```


RealSpan 中大多数方法均为调用 MutableSpan 保存数据，例如 start、name、kind、tag、error 等。仅贴出 name 的代码

```java
 @Override public Span name(String name) {
    synchronized (state) {
      state.name(name);
    }
    return this;
  }
```

介绍 span 相关的三个方法 finish、abandon、flush 均调用 pendingSpans，以 finish 为例

```java
  @Override public void finish(long timestamp) {
    synchronized (state) {
      // pendingSpans 会通过 context 匹配到 PendingSpan，从而找到 RealSpan 对应的 MutableSpan。然后执行上报操作  
      pendingSpans.finish(context, timestamp);
    }
  }
```

最后看下比较独特的 annotate 方法，在 annotate 中自动设置了跨度的类型

```java
@Override public Span annotate(long timestamp, String value) {
    if ("cs".equals(value)) { // 客户端发起请求
      synchronized (state) {
        state.kind(Span.Kind.CLIENT);
        state.startTimestamp(timestamp);
      }
    } else if ("sr".equals(value)) { // 服务端接受请求
      synchronized (state) {
        state.kind(Span.Kind.SERVER);
        state.startTimestamp(timestamp);
      }
    } else if ("cr".equals(value)) { // 客户端接受响应
      synchronized (state) {
        state.kind(Span.Kind.CLIENT);
      }
      finish(timestamp);
    } else if ("ss".equals(value)) { // 服务端发送响应
      synchronized (state) {
        state.kind(Span.Kind.SERVER);
      }
      finish(timestamp);
    } else {
      synchronized (state) {
        state.annotate(timestamp, value);
      }
    }
    return this;
  }
```

RealSpan 主要涉及 PendingSpans、PendingSpan 和 MutableSpan 三个类，MutableSpan 主要保存数据不深入介绍

## PendingSpans

PendingSpans 维护了链路上下文和待上传链路的关系，继承 WeakConcurrentMap帮助垃圾回收

```java
public final class PendingSpans extends WeakConcurrentMap<TraceContext, PendingSpan> {
  // 默认的可修改跨度
  final MutableSpan defaultSpan;
  final Clock clock;
  // 跨度处理器（后续介绍）
  final SpanHandler spanHandler;
  final AtomicBoolean noop;

}
```

获取 PendingSpan

```java
  public PendingSpan getOrCreate(
    @Nullable TraceContext parent, TraceContext context, boolean start) {
    // Map#get
    PendingSpan result = get(context);
    if (result != null) return result;

    MutableSpan span = new MutableSpan(context, defaultSpan);
    PendingSpan parentSpan = parent != null ? get(parent) : null;
    TickClock clock;
    if (parentSpan != null) {
      TraceContext parentContext = parentSpan.context();
      if (parentContext != null) parent = parentContext;
      clock = parentSpan.clock;
      if (start) span.startTimestamp(clock.currentTimeMicroseconds());
    } else {
      long currentTimeMicroseconds = this.clock.currentTimeMicroseconds();
      clock = new TickClock(currentTimeMicroseconds, System.nanoTime());
      if (start) span.startTimestamp(currentTimeMicroseconds);
    }

    PendingSpan newSpan = new PendingSpan(context, span, clock);
    // Map#putIfProbablyAbsent
    PendingSpan previousSpan = putIfProbablyAbsent(context, newSpan);
    // 多线程出现竞争因此可能先前设置过
    if (previousSpan != null) return previousSpan;
    assert parent != null || context.isLocalRoot() :
      "Bug (or unexpected call to internal code): parent can only be null in a local root!";
    // 调用跨度处理器开始方法
    spanHandler.begin(newSpan.handlerContext, newSpan.span, parentSpan != null
      ? parentSpan.handlerContext : null);
    return newSpan;
  }
```

结束 PendingSpan，以 finish 为例

```java
  public void finish(TraceContext context, long timestamp) {
    // Map#remove
    PendingSpan last = remove(context);
    if (last == null) return;
    last.span.finishTimestamp(timestamp != 0L ? timestamp : last.clock.currentTimeMicroseconds());
    // 调用跨度处理器结束方法
    spanHandler.end(last.handlerContext, last.span, Cause.FINISHED);
  }
```


## 参考链接


##### 标签
#trace 