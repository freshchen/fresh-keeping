---
begin: 2022-01-18
status: ongoing
rating: 1
---

# Zipkin B3 链路传播规范源码分析
## 简介
[GitHub - openzipkin/b3-propagation: Repository that describes and sometimes implements B3 propagation](https://github.com/openzipkin/b3-propagation)

阅读 [分布式链路追踪系统 Zipkin 架构简介](https://juejin.cn/post/7054206638382645278) 熟悉基本链路追踪概念后，我们知道链路能够串联的关键就是通过 traceId，parentId，spanId 等标识符，如果每个检测库实现都用不同的标识，有的叫 traceId 有的叫 tid 那就很难打通，因此需要一个标准规范，大家都遵循就便捷互通。
B3 就是 ZIpkin 制定的链路上下文跨服务边界的传播规范。

## B3 标识符规范

B3 主要有两种形式，多标识符便于解析和debug。单标识符网络开销小

### 多标识符

| 标识符 | 描述 | 示例 |
|:------ |:---- |:---- |
|    X-B3-TraceId    | 16进制的数字，最大 128 比特（32位） | 463ac35c9f6413ad48485a3953bb6124 |
|    X-B3-ParentSpanId    | 16进制的数字，最大 64 比特（16位） | a2fb4a1d1a96d312 |
|    X-B3-SpanId    | 16进制的数字，最大 64 比特（16位） | a2fb4a1d1a96d312 |
|    X-B3-Sampled    | 1:采样 0:不采样 | 1 |
|    X-B3-Flags    | 1:debug级别，需要强制采样 | 1 |


### 单标识符
- b3：上述所有 ”x-b3-“ 为前缀的标识构的汇总，格式为 b3={TraceId}-{SpanId}-{SamplingState}-{ParentSpanId} 最后两个字段可选，具体拼接规则如下 [拼接规则](https://github.com/openzipkin/b3-propagation#single-header)

## 源码分析

> spring-boot：2.5.6
> spring-cloud：2020.0.4
> spring-cloud-sleuth：3.0.4
> zipkin-brave：5.13.2

以下源码分析均以 http 请求为例，spring cloud sleuth 注册了一个 servlet 过滤器 TracingFilter，其中调用 BraveHttpServerHandler#handleReceive 方法开始接受处理链路。直到进入 brave 相关代码如下所示：
```java
public Span handleReceive(HttpServerRequest request) {
	Span span = this.nextSpan(this.defaultExtractor.extract(request), request);
	return this.handleStart(request, span);
}
```

### 1 提取标识符 extract
handleReceive 中 this.defaultExtractor 的 默认实现是 B3Extractor。让我们一起看下是如何提取请求的

```java
@Override 
// R 为 HttpSevletRequest，返回结果是链路上下文
public TraceContextOrSamplingFlags extract(R request) {
  if (request == null) throw new NullPointerException("request == null");

  // getter 为 brave.http.HttpServerRequest#GETTER，从 request 请求头中取出 “b3” 的值
  String b3 = getter.get(request, B3);
  // 如果 “b3” 有内容尝试进行单描述符提取 {TraceId}-{SpanId}-{SamplingState}-{ParentSpanId}
  TraceContextOrSamplingFlags extracted = b3 != null ? parseB3SingleFormat(b3) : null;
  if (extracted != null) return extracted;

  // 从 request 请求头中取出 ”X-B3-Sampled“ 的值，先判断是不是要采样
  String sampled = getter.get(request, SAMPLED);
  Boolean sampledV;
  if (sampled == null) {
	sampledV = null;
  } else if (sampled.length() == 1) { // 可能是1或0
	char sampledC = sampled.charAt(0);

	if (sampledC == '1') {
	  sampledV = true;
	} else if (sampledC == '0') {
	  sampledV = false;
	} else {
	  Platform.get().log(SAMPLED_MALFORMED, sampled, null);
	  return TraceContextOrSamplingFlags.EMPTY; // 不是1也不是0可能是业务误传，开启一个新的空链路
	}
  } else if (sampled.equalsIgnoreCase("true")) { // 兼容老客户端
	sampledV = true;
  } else if (sampled.equalsIgnoreCase("false")) { // 兼容老客户端
	sampledV = false;
  } else {
	Platform.get().log(SAMPLED_MALFORMED, sampled, null);
	return TraceContextOrSamplingFlags.EMPTY; // 可能是业务误传，开启一个新的空链路上下文
  }

  // 从 request 请求头中取出 ”X-B3-Flags“ 的值，判断是不是 debug 模式
  boolean debug = "1".equals(getter.get(request, FLAGS));

  String traceIdString = getter.get(request, TRACE_ID);

  // 如果没有传traceId
  if (traceIdString == null) {
	if (debug) return TraceContextOrSamplingFlags.DEBUG;
	if (sampledV != null) {
	  return sampledV
		  ? TraceContextOrSamplingFlags.SAMPLED
		  : TraceContextOrSamplingFlags.NOT_SAMPLED;
	}
  }

  // 把 
  TraceContext.Builder result = TraceContext.newBuilder();
  if (result.parseTraceId(traceIdString, TRACE_ID) // 检查 traceId 是不是 32 字符以内
	  && result.parseSpanId(getter, request, SPAN_ID) // 从 request 请求头中取出 ”X-B3-SpanId“ 的值，并检查是不是 16 字符以内
	  && result.parseParentId(getter, request, PARENT_SPAN_ID)) { // 从 request 请求头中取出 ”X-B3-ParentSpanId“ 的值，并检查是不是 16 字符以内
	if (sampledV != null) result.sampled(sampledV.booleanValue());
	if (debug) result.debug(true);
	return TraceContextOrSamplingFlags.create(result.build());
  }
  return TraceContextOrSamplingFlags.EMPTY; // 如果都不符合条件开启一个新的空链路上下文
}

```

### 2 注入标识符 inject
以 Apache HttpClient 为例，发现项目中引入 HttpClient 依赖后，sleuth 会缺省注册一个 HttpClientBuilder 的实现 TracingHttpClientBuilder。业务代码注入 HttpClientBuilder 创建客户端从而进行 http 调用。

TracingHttpClientBuilder 重写了 decorateProtocolExec 和 decorateMainExec 对请求进行包装处理，注入标识符到请求头的逻辑在 decorateMainExec 中，让我们来看对应的装饰类 TracingMainExec。

TracingMainExec 实现 ClientExecChain 重写了 execute 方法，在真正发起调用前会从 HttpClientContext 中取出当前的链路上下文，然后交给 brave.http.HttpClientHandler 对请求进行统一处理。

HttpClientHandler 中的 defaultInjector 默认是 CompositePropagationFactory。关注一下注入器的选择过程 CompositePropagationFactory#injector

```java
@Override
// Setter 是 HttpClientRequest.SETTER
public <R> TraceContext.Injector<R> injector(Setter<R, String> setter) {
	return (traceContext, request) -> {
		// 默认 type 是 PropagationType.B3。mapping get 为 B3Propagation。
		this.types.stream().map(this.mapping::get)
				//  执行具体 inject 注入方法的 InjectorFunction 具体实现为 brave.propagation.B3Propagation.Format#MULTI
				.forEach(p -> p.getValue().injector(setter).inject(traceContext, request));
	};
}
```

最后可以看到具体的设置请求头动作全在这个方法中 brave.propagation.B3Propagation.Format#MULTI

```java
// 默认多标识符请求头
MULTI() {
  @Override public List<String> keyNames() {
	// TRACE_ID, SPAN_ID, PARENT_SPAN_ID, SAMPLED, FLAGS
	return MULTI_KEY_NAMES;
  }

  @Override public <R> void inject(Setter<R, String> setter, TraceContext context, R request) {
	// 设置 X-B3-TraceId 请求头
	setter.put(request, TRACE_ID, context.traceIdString());
	// 设置 X-B3-SpanId 请求头
	setter.put(request, SPAN_ID, context.spanIdString());
	String parentId = context.parentIdString();
	// 设置 X-B3-ParentSpanId 请求头
	if (parentId != null) setter.put(request, PARENT_SPAN_ID, parentId);
	if (context.debug()) {
	// 设置 X-B3-Flags 请求头
	  setter.put(request, FLAGS, "1");
	} else if (context.sampled() != null) {
	// 设置 X-B3-Sampled 请求头
	  setter.put(request, SAMPLED, context.sampled() ? "1" : "0");
	}
  }
}
```



## 参考链接

[分析Zipkin/Brave中的B3](http://www.360doc.com/content/21/0111/14/39821762_956315245.shtml)

##### 标签
#trace 