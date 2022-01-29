---
begin: 2022-01-18
status: ongoing
rating: 1
---

# Zipkin B3 链路上下文传播规范
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
- b3：上述所有 ”x-b3-“ 为前缀的标识构的汇总，具体拼接规则如下 [拼接规则](https://github.com/openzipkin/b3-propagation#single-header)




## 参考链接

[分析Zipkin/Brave中的B3](http://www.360doc.com/content/21/0111/14/39821762_956315245.shtml)

##### 标签
#trace 