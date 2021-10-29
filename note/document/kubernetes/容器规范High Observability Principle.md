---
begin: 2021-10-27
status: ongoing
rating: 1
---

# 容器规范High Observability Principle（高可观察性）


## The High Observability Principle (HOP)

> Containers provide a unified way for packaging and running applications by treating them like a black box. But any container aiming to become a cloud-native citizen must provide application programming interfaces (APIs) for the runtime environment to observe the container health and act accordingly. This is a fundamental prerequisite for automating container updates and life cycles in a unified way, which in turn improves the system’s resilience and user experience.

![](image/Pasted%20image%2020211027143413.png)

> In practical terms, at a very minimum, your containerized application must provide APIs for the different kinds of health checks—liveness and readiness. Even better-behaving applications must provide other means to observe the state of the containerized application. The application should log important events into the standard error (STDERR) and standard output (STDOUT) for log aggregation by tools such as Fluentd and Logstash and integrate with tracing and metrics-gathering libraries such as OpenTracing, Prometheus, and others. Treat your application as a black box, but implement all necessary APIs to help the platform observe and manage your application in the best way possible.

## 容器高可观察性规范

容器通过将应用程序视为一种统一的方式来打包和运行应用程序黑盒子。 但是任何旨在成为云原生公民的容器都必须为运行时环境提供API以观察容器的健康状况从而采取相应的策略。因此，容器提供 API 是以统一的方式自动化容器更新和生命周期的基本先决条件，这反过来又提高了系统的弹性和用户体验。
如上图所示，容器化应用程序至少必须为不同种类的健康检查，例如活跃度liveness和准备度readiness。即使对性能要求再高的应用，也应该把重要事件记录到标准错误 (STDERR) 和标准输出 (STDOUT) 中，以便通过 Fluentd 和 Logstash 等工具进行日志聚合，并与跟踪和指标收集库（例如 OpenTracing、Prometheus 等）集成。

## 参考链接

https://www.redhat.com/cms/managed-files/cl-cloud-native-container-design-whitepaper-f8808kc-201710-v3-en.pdf

##### 标签
#k8s 
