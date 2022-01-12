---
begin: 2021-12-08
status: ongoing
rating: 1
---

# Sleuth 简介

sleuth [slu:θ] 用于分布式链路追踪

主要基于 [Zipkin Brave简介](../../cloud/trace/zipkin/Zipkin%20Brave简介.md) 实现，默认支持 Zipkin

## 概念

灵感来源于 [Dapper Trace论文](../../cloud/trace/Dapper%20Trace论文.md)

- Span：基本工作单元，例如，在一个新建的span中发送一个RPC等同于发送一个回应请求给RPC，span通过一个64位ID唯一标识，trace以另一个64位ID表示，span还有其他数据信息，比如摘要、时间戳事件、关键值注释(tags)、span的ID、以及进度ID(通常是IP地址) 
- span在不断的启动和停止，同时记录了时间信息，当你创建了一个span，你必须在未来的某个时刻停止它。
- Trace：一系列spans组成的一个树状结构，例如，如果你正在跑一个分布式大数据工程，你可能需要创建一个trace。
- Annotation：用来及时记录一个事件的存在，一些核心annotations用来定义一个请求的开始和结束 
	- cs - Client Sent -客户端发起一个请求，这个annotion描述了这个span的开始
	- sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络延迟
	- ss - Server Sent -注解表明请求处理的完成(当请求返回客户端)，如果ss减去sr时间戳便可得到服务端需要的处理请求时间
	- cr - Client Received -表明span的结束，客户端成功接收到服务端的回复，如果cr减去cs时间戳便可得到客户端从服务端获取回复的所有所需时间 。

![](image/Pasted%20image%2020211208114238.png)


您可以注意到日志格式已更新为以下信息`[backend,0b6aaf642574edd3,0b6aaf642574edd3`。此条目对应于`[application name,trace id, span id]`。应用程序名称是从`SPRING_APPLICATION_NAME`环境变量中读取的。


Spring Cloud Sleuth Core 在其`api`模块中包含要由跟踪器实现的所有必要接口。该项目带有 OpenZipkin Brave 实现。您可以通过查看`org.springframework.cloud.sleuth.brave.bridge`.

最常用的接口有：

-   `org.springframework.cloud.sleuth.Tracer` - 使用跟踪器，您可以创建一个根跨度来捕获请求的关键路径。
    
-   `org.springframework.cloud.sleuth.Span`- 跨度是需要启动和停止的单个工作单元。包含计时信息以及事件和标签。
    

您还可以直接使用跟踪器实现的 API。

让我们看看以下 Span 生命周期操作。

-   [start](https://docs.spring.io/spring-cloud-sleuth/docs/3.0.4/reference/htmlsingle/#using-creating-and-ending-spans)：当你启动一个跨度时，它的名称被分配并记录开始时间戳。
    
-   [end](https://docs.spring.io/spring-cloud-sleuth/docs/3.0.4/reference/htmlsingle/#using-creating-and-ending-spans) : 跨度完成（记录跨度的结束时间），如果跨度被采样，则它有资格收集（例如到 Zipkin）。
    
-   [continue](https://docs.spring.io/spring-cloud-sleuth/docs/3.0.4/reference/htmlsingle/#using-continuing-spans)：跨度继续，例如在另一个线程中。
    
-   [使用显式父级创建](https://docs.spring.io/spring-cloud-sleuth/docs/3.0.4/reference/htmlsingle/#using-creating-spans-with-explicit-parent)：您可以创建一个新的跨度并为其设置显式父级。


其中一种情况是跳过某些客户端跨度的报告。为此，您可以设置`spring.sleuth.web.client.skip-pattern`要跳过的路径模式。另一种选择是提供您自己的自定义``org.springframework.cloud.sleuth.SamplerFunction<`org.springframework.cloud.sleuth.http.HttpRequest>``实现并定义何时`HttpRequest`不应采样给定。


如果您将以下其中一项定义为 a `Bean`，Sleuth 将调用它来自定义行为：

-   `RpcTracingCustomizer` - 用于 RPC 标记和采样策略
    
-   `HttpTracingCustomizer` - 用于 HTTP 标记和采样策略
    
-   `MessagingTracingCustomizer` - 用于消息标记和抽样策略
    
-   `CurrentTraceContextCustomizer` - 集成装饰器，例如相关性。
    
-   `BaggagePropagationCustomizer` - 用于在处理中和通过标题传播行李字段
    
-   `CorrelationScopeDecoratorCustomizer` - 用于范围装饰，例如 MDC（日志记录）字段关联



如果需要客户端/服务器采样，只需注册一个类型 `brave.sampler.SamplerFunction<HttpRequest>`的 bean 并`sleuthHttpClientSampler`为客户端采样器和`sleuthHttpServerSampler` 服务器采样器命名 bean 。

为方便起见`@HttpClientSampler`，`@HttpServerSampler` 可以使用和注释来注入正确的 bean 或通过它们的静态 String`NAME`字段引用 bean 名称。

查看 Brave 的代码以查看如何制作基于路径的采样器的 [示例 github.com/openzipkin/brave/tree/master/instrumentation/http#sampling-policy](https://github.com/openzipkin/brave/tree/master/instrumentation/http#sampling-policy)

如果您想完全重写`HttpTracing`bean，您可以使用该`SkipPatternProvider` 接口来检索`Pattern`不应采样的跨度的 URL 。下面您可以看到`SkipPatternProvider`在服务器端内部使用的示例，`Sampler<HttpRequest>`.















### 上下文传播
默认使用  [B3](https://github.com/openzipkin/b3-propagation)




### [OpenZipkin Brave Tracer Integration](https://docs.spring.io/spring-cloud-sleuth/docs/3.1.0/reference/htmlsingle/spring-cloud-sleuth.html#features-brave)

### [Log integration](https://docs.spring.io/spring-cloud-sleuth/docs/3.1.0/reference/htmlsingle/spring-cloud-sleuth.html#features-log-integration)

## 使用

主要基于 OpenZipkin Brave 实现，相关桥接代码目录 org.springframework.cloud.sleuth.brave.bridge

手动串联常用接口:
-   `org.springframework.cloud.sleuth.Tracer`
-   `org.springframework.cloud.sleuth.Span` 

手动注解串联：
- @SpanName 
- @NewSpan
- @ContinueSpan 当前span基础上追加信息 
- @SpanTag


## 参考链接
[Spring Cloud Sleuth Reference Documentation](https://docs.spring.io/spring-cloud-sleuth/docs/current/reference/html/index.html)

##### 标签
#trace
