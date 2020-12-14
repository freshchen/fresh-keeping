# Envoy笔记

## 简介

> Envoy 是专为大型现代 SOA（面向服务架构）架构设计的 L7 代理和通信总线。
>
> **进程外架构：**Envoy 是一个独立进程，设计为伴随每个应用程序服务运行。所有的 Envoy 形成一个透明的通信网格，每个应用程序发送消息到本地主机或从本地主机接收消息，但不知道网络拓扑。在服务间通信的场景下，进程外架构与传统的代码库方式相比，具有两大优点：
>
> - Envoy 可以使用任何应用程序语言。Envoy 可以在 Java、C++、Go、PHP、Python 等之间形成一个网格。面向服务架构使用多个应用程序框架和语言的趋势越来越普遍。Envoy 透明地弥合了它们之间的差异。
> - 任何做过大型面向服务架构的人都知道，升级部署库可能会非常痛苦。Envoy可以透明地在整个基础架构上快速部署和升级。

之前看到过这样的质疑，以著名微服务开发框架 Spring Cloud 为例，注册中心 eruka，消息总线 bus 这样的服务总线一旦出现问题将产生毁灭性的后果，这也就意味着微服务实际上仍然是由各种服务总线串联起来的巨型单体，因此这根线显得极为重要，电脑为例，即使我们拥有最强的CPU，显卡，显示器，主板一坏变成了废铁。所以这根线需要更高的性能，更贴近操作系统的编程语言，并能够与日常应用开发完全解耦。作为 istio 当之无愧的核心 Envoy 便是这根线，可以简单理解为一个代理服务器。

### 特点

- **现代 C++11 代码库**
- **L3/L4 filter 架构**
- **HTTP L7 filter 架构**
- **顶级 HTTP/2 支持**
-  **HTTP L7 路由**
-  **gRPC支持**
- **MongoDB L7 支持**
-  **DynamoDB L7 支持**
-  **服务发现和动态配置**
-  **健康检查**
- **高级负载均衡**
- **前端/边缘代理支持**
- **最佳的可观察性**

### 术语

- **HOST** ： 能独立寻址进行网络通信的 web 服务
  - 终端为例，localhost 8080 和 8081 分别起着两个 tomcat，那么对于 Envoy 来说这两个 tomcat 就是两个 Host
  - k8s为例，pod作为最小调度单位，拥有真实Ip地址，所以每个 pod 就是一个host
- **Upstream**：接收来自 Envoy 的连接和请求，并返回响应的 Host 称为上游
  - Envoy 作为一个中间人，相当于房屋中介，作为服务的真正提供者以及被调用方便是上游房东

- **Downstream**： 连接到 Envoy，发送请求并接收响应的 Host 称为下游
  - 作为服务的请求方，也就是下游找房人
- **Listener**：监听器是命名网地址（例如，端口、unix domain socket等)，可以被下游客户端连接。Envoy 暴露一个或者多个监听器给下游主机连接。
  - 下游找房人可以在各种各种找房平台上看到中介刊登的信息
- **Cluster**：集群是指 Envoy 连接到的逻辑上相同的一组上游主机。Envoy 通过[服务发现](https://www.servicemesher.com/envoy/intro/arch_overview/service_discovery.html#arch-overview-service-discovery)来发现集群的成员。可以选择通过[主动健康检查](https://www.servicemesher.com/envoy/intro/arch_overview/health_checking.html#arch-overview-health-checking)来确定集群成员的健康状态。Envoy 通过[负载均衡策略](https://www.servicemesher.com/envoy/intro/arch_overview/load_balancing.html#arch-overview-load-balancing)决定将请求路由到哪个集群成员。
  - 中介可以通过找男主人或者女主人约看房
- **Mesh**：一组 Host ，协调好以提供一致的网络拓扑。“Envoy mesh”是一组 Envoy 代理，它们构成了分布式系统的消息传递基础，这个分布式系统由很多不同服务和应用程序平台组成。
  - 整合了很多房东信息，房屋资源，训练有素的房产顾问，规范专业的服务，形成了一个大的中介网络，一个网格就是一家门店
- **Runtime configuration**：外置实时配置系统，和 Envoy 一起部署。可以更改配置设置，影响操作，而无需重启 Envoy 或更改主要配置。
  - 不解释

### 线程模型

单进程，多线程，经典一 master 多 worker ，worker 间不共享资源并发执行的 Actor 模型

### 架构

组建功能介绍可以看

- [官网](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/listeners/listeners_toc)
- [中文](https://www.servicemesher.com/envoy/intro/arch_overview/listeners.html)