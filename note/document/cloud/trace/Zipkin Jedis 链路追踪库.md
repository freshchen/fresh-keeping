---
begin: 2022-03-02
status: ongoing
rating: 1
---

# Zipkin Jedis 链路追踪采集器

## 简介

Zipkin 的 brave 库为 Java 开发者提供了各种常用组件链路追踪采集器
[GitHub - openzipkin/brave: Java distributed tracing implementation compatible with Zipkin backend services.](https://github.com/openzipkin/brave)

但是在 [instrumentation](https://github.com/openzipkin/brave/tree/master/instrumentation "instrumentation") 中没有 Jedis 相关的采集器，因此我们得自己开发。

## 实践

废话不多说，直接开始，首先基于 brave 开发，版本如下:

> brave：5.13.2

首先我们使用 Jedis 库时都是通过 Jedis 类操作，因此我们可以从 Jedis 类这个入口进行增强，因此选择装饰器模式，我们以 2.6.3 版本的 jedis 为例。部分代码如下：

```java
public class TraceableJedis263 extends Jedis {

    private final Jedis delegate;
    private final JedisTracerHelper helper;

    @Override
    public Long append(byte[] key, byte[] value) {
		// 开启一个跨度
        Span span = helper.startNextJedisSpan("append", key);
        span.tag("value", Arrays.toString(value));
		// 委托给原先的 Jedis 类处理，处理在过程是在独立作用域内的
        return helper.executeInScope(span, () -> delegate.append(key, value));
    }

		...  ...
}
```

下面来看 startNextJedisSpan 和  executeInScope 这两个关键方法

```java
public Span startNextJedisSpan(String command, byte[] key) {
	// 根据当前上下文生成新的 span
	Span span = tracer.nextSpan();
	span.kind(Span.Kind.CLIENT);  
	// span 名称为 jedis 方法名
	span.name(command);  
	span.remoteServiceName("reids");
	span.tag(KEY, Arrays.toString(key));
	return span;
}

public <T> T executeInScope(Span span, Supplier<T> supplier) {
	try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
		return supplier.get();
	} catch (RuntimeException | Error e) {
		span.error(e);
		throw e;
	} finally {
		span.finish();
	}
}
```

是不是很简单，一个 jedis 的链路采集库就制作好了，下面让我们看看如何使用。

```java
// tracer 构造比较负责，一般作为单例，例如 Spring Bean 注入
Jedis jedis = new TraceableJedis263(new Jedis("localhost"), new JedisTracerHelper(tracer));
jedis.get("key");
```

如果使用 spring 进行开发，可以通过例如 BeanPostProcessor 对 Jedis 进行包装，从而做到业务透明。

上述代码现维护到了 github，目前代码还比较简单，欢迎大家一起扩展维护。

[GitHub - freshchen/zipkin-instrumentation: openzipkin 官方 instrumentation 的补充。目前包含 jedis、redisson](https://github.com/freshchen/zipkin-instrumentation)

## 参考链接


##### 标签
