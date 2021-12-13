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
