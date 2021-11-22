---
begin: 2021-11-22
status: ongoing
rating: 1
---

# OpenTelemetry简介

>OpenTelemetry is a set of APIs, SDKs, tooling and integrations that are designed for the creation and management of _telemetry data_ such as traces, metrics, and logs.

- OpenTelemetry 是一组api、sdk、工具。用于生成和管理遥测数据(如跟踪、度量和日志)。

- OpenTelemetry 是 CNCF 的一个可观测性项目，旨在提供可观测性领域的标准化方案，解决观测数据的数据模型、采集、处理、导出等的标准化问题

- OpenTelemetry 是一套标准，而不是具体实现的后端，比如Jaeger, Prometheus 

- OpenTelemetry 是由两个开源项目合并组成的：
	-   OpenCensus
    	-   面向 trace 和 metrics 进行数据模型标准化，并提供采集、解决、导出的工具
	-   OpenTracing
		-   面向 trace 进行数据模型标准化，并提供采集、解决、导出的工具

![](image/Pasted%20image%2020211122111314.png)

## 为什么需要OpenTelemetry

OpenTelemetry旨在提供云原生应用可观测性，从而帮助相关人员更好的了解系统运行状况，快速定位问题。


## OpenTelemetry能力

-   A single, vendor-agnostic instrumentation library per language with support for both automatic and manual instrumentation.
-   A single collector binary that can be deployed in a variety of ways including as an agent or gateway.
-   An end-to-end implementation to generate, emit, collect, process and export telemetry data.
-   Full control of your data with the ability to send data to multiple destinations in parallel through configuration.
-   Open-standard semantic conventions to ensure vendor-agnostic data collection
-   The ability to support multiple context propagation formats in parallel to assist with migrating as standards evolve.
-   A path forward no matter where you are on your observability journey. With support for a variety of open-source and commercial protocols, format and context propagation mechanisms as well as providing shims to the OpenTracing and OpenCensus projects, it is easy to adopt OpenTelemetry.

## 总体架构

- 业务应用中加入  OpenTelemetry 提供的对应工具库，用于上报数据
- 每台机器装一个 Collector Agent，接受上报数据
- Collector Service 可以是 Jaeger, Prometheus 等主流实现

![](image/Pasted%20image%2020211122105552.png)
## 系统组件

### Proto

独立于语言的标准RPC数据格式定义

[GitHub - open-telemetry/opentelemetry-proto: Protobuf definitions for the OpenTelemetry protocol (OTLP)](https://github.com/open-telemetry/opentelemetry-proto)

### Specification

定义跨语言的标准，以及 API，SDK，数据格式（OTLP）
[Specification | OpenTelemetry](https://opentelemetry.io/docs/reference/specification/)

### Collector

供应商无关的可插拔数据采集方案

### Instrumentation Libraries

嵌入到应用中的数据上报工具库


## 数据来源

[Trace简介](../trace/Trace简介.md)

[Metrics简介](../metrics/Metrics简介.md)

[分布式日志简介](../log/分布式日志简介.md)

[Baggage简介](../baggage/Baggage简介.md)

## 参考链接

[Documentation | OpenTelemetry](https://opentelemetry.io/docs/)

##### 标签
#open-telemetry #cloud-native