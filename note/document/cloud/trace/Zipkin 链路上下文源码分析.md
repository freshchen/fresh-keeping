---
begin: 2022-02-03
status: ongoing
rating: 1
---

# Zipkin 链路上下文源码分析

## 简介

上文 [Zipkin B3 链路传播规范源码分析](https://juejin.cn/post/7059756331451809822)  讨论了使用 B3 链路传播规范客户端服务端的如何传输数据，inject 前从哪里获取标识符， extract 后标识符存在哪里等问题没有涉及，接着看 Zipkin 传播的示意图。可以看到标识符都是放在 TraceContext 这个上下文中。

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

## 链路上下文三种实现

查看官方文档 [propagation](https://github.com/freshchen/brave/tree/master/brave#propagation) 可以看到 链路上下文 主要分三种情况：
- `TraceContext`：有 traceId，spanId
- `TraceIdContext`：只有 traceId
- `SamplingFlags`：没有 traceId 也没有 spanId

了解上述三种上下文对我们自研链路采集库很有帮助。brave 提供了 `TraceContextOrSamplingFlags` 类屏蔽上述三种上下文的区别，使用方式如下

```java
SamplingFlags samplingFlags = SamplingFlags.SAMPLED;
TraceContextOrSamplingFlags.create(samplingFlags);

TraceIdContext traceIdContext = TraceIdContext.newBuilder().traceId(1L).sampled(true).build();
TraceContextOrSamplingFlags.create(traceIdContext);

TraceContext traceContext = TraceContext.newBuilder().traceId(1L).spanId(1L).parentId(1L).sampled(true).build();
TraceContextOrSamplingFlags.create(traceContext);

```

## 链路上下文使用

通过链路上下文可以创建 span，使用方式如下

```java
TraceIdContext traceIdContext = TraceIdContext.newBuilder().traceId(1L).sampled(true).build();
TraceContextOrSamplingFlags traceContextOrSamplingFlags = TraceContextOrSamplingFlags.create(traceIdContext);
Span span = tracer.nextSpan(traceContextOrSamplingFlags);
```

# 链路上下文源码分析

> spring-boot：2.5.6
> spring-cloud：2020.0.4
> spring-cloud-sleuth：3.0.4
> zipkin-brave：5.13.2


## SamplingFlags

主要用于存储是否采样，或者是否debug，只有一个核心字段
`final int flags`
另外还提供了四个常用单例
```java
public static final SamplingFlags EMPTY = new SamplingFlags(0);
public static final SamplingFlags NOT_SAMPLED = new SamplingFlags(FLAG_SAMPLED_SET);
public static final SamplingFlags SAMPLED = new SamplingFlags(NOT_SAMPLED.flags | FLAG_SAMPLED);
public static final SamplingFlags DEBUG = new SamplingFlags(SAMPLED.flags | FLAG_DEBUG);
```

是否采样判断，是否抽样在初次生成了 traceId 后就决定了，例如采样 1% 可以根据traceId十进制的后两位决定，因为 traceId 是整条链路统一的，从而保证是否采样的决定在后续的跨度中是一致的。
```java
/**  
 * @return true:需要采样上报到zipkin | false:不采样上报到zipkin | null：推迟决定是否上报  
 */
@Nullable public final Boolean sampled() {
	return (flags & FLAG_SAMPLED_SET) == FLAG_SAMPLED_SET
	  ? (flags & FLAG_SAMPLED) == FLAG_SAMPLED
	  : null;
```

是否debug判断
```java
public final boolean debug() {  
 return debug(flags);  
}

static boolean debug(int flags) {  
 return (flags & FLAG_DEBUG) == FLAG_DEBUG;  
}
```

## TraceIdContext

TraceIdContext 继承了 SamplingFlags。提供了一个 Builder 帮助创建上下文，相较于 SamplingFlags 新增了两个属性
`final long traceIdHigh, traceId;`
设置了 traceId不就行了，traceIdHigh 是干嘛的？继续查看源码

```java
  /**
   * 返回的是通过 Builder 设置的 traceId 生成的十六进制
   * @return
   */
  public String traceIdString() {
    String r = traceIdString;
    // 延迟加载
    if (r == null) {
      r = toTraceIdString(traceIdHigh, traceId);
      traceIdString = r;
    }
    return r;
  }

  static String toTraceIdString(long traceIdHigh, long traceId) {
    // 设置了 traceIdHigh 就用 traceIdHigh 和 traceId 一起生成
    if (traceIdHigh != 0) {
      char[] result = RecyclableBuffers.parseBuffer();
      writeHexLong(result, 0, traceIdHigh);
      writeHexLong(result, 16, traceId);
      // traceIdHigh 128 字节，返回 32 个字符的十六进制
      return new String(result, 0, 32);
    }
    // traceId 64 字节，此时返回 16 个字符的十六进制
    return toLowerHex(traceId);
```

## TraceContext
TraceContext 是三种上下文中最常使用的。TraceContext 同样继承了 SamplingFlags。提供了一个 Builder 帮助创建上下文，相较于 SamplingFlags 新增了如下属性
```java
final long traceIdHigh, traceId, localRootId, parentId, spanId;  
final List<Object> extraList;
```

其中 traceIdHigh 和 traceId 和 TraceIdContext 中一样，localRootId 记录 trace 的第一个 spanId，parentId 记录夫跨度 ID，spanId 记录当前跨度 ID。此外还提供了 extraList 用于伴随链路传播携带一些扩展信息。

TraceContext 还引入了一些新的方法，比较重要的是 spanIdString，此方法和 TraceIdContext 中 traceIdString 类似用于生成16个字符的十六进制字符串，其他则为对应字段的 getter 和 比较方法。值得一提的是 TraceContext 对 flag 新增了 shared 是否共享的判断，主要用于 RPC 远程调用跨度的加入场景。

最后与上文传播中 inject 和 extract 两个方法对应，TraceContext 提供了两个接口用于注入和提取上下文

```java
  public interface Injector<R> {
     // 将上下文注入到请求中
    void inject(TraceContext traceContext, R request);
  }

  public interface Extractor<R> {
    // 提取请求中的上下文，提取的结果是上文提到的三种上下文门面类
    TraceContextOrSamplingFlags extract(R request);
  }
```

## 参考链接


##### 标签
