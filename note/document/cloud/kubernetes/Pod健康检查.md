---
begin: 2021-10-27
status: ongoing
rating: 1
---

# Pod健康检查

## 简介

Health Probe Pattern 健康检查模式，是构建高可用、弹性系统的重要环节。健康检查不仅仅监控容器(POD)的状态，还需要检查容器请求和响应情况。

## 前提

数据源来源于容器，需要业务方根据规范配合完成

[容器规范High Observability Principle](容器规范High%20Observability%20Principle.md)

## 实现

k8s的控制器，例如 Deployment，会根据pod的状态决定是否重启或者调度到其他的node。pod的状态是kubelet不断检查容器进程决定的，如果应用出错就会停止容器进程那么k8s基于进程的检查就已经胜任了。但更多错误并不是重启就能解决，比如服务下游挂了。k8s提供了探针来解决这些问题。

![](image/Pasted%20image%2020211027150428.png)

## k8s探针详解

## 主要探针种类


| 种类 |  作用   |  检测不健康后行为   |   使用场景举例  | 
| ---- | --- | --- | --- |
|livenessProbe|检查容器何时重启|根据重启策略重启|发生死锁，重启容器有助于让应用程序自我修复|
|readinessProbe|检查容器何时准备好接受流量|从 Service 的 endpoint 中剔除|依赖的数据库无法访问，则等待数据库可以访问再对外提供服务   | 

### 探测方式

| 方式      | 行为                   | 健康条件                 |
| --------- | ---------------------- | ------------------------ |
| exec      | 容器内执行shell命令    | shell命令 返回 0         |
| httpGet   | 向容器发送HTTP GET请求 | 200 <= http status < 400 |
| tcpSocket | 建立tcp连接            | 能够建立连接             |

## 探针通用配置

| 配置名              | 含义                                               | 备注                                          | 默认值 | 最小值 |
| ------------------- | -------------------------------------------------- | --------------------------------------------- | ------ | ------ |
| initialDelaySeconds | 容器启动后要等待多少秒后存活和就绪探测器才被初始化 |                                               | 0      | 0      |
| periodSeconds       | 执行探测的时间间隔                                 |                                               | 10     | 1      |
| timeoutSeconds      | 探测的超时后等待多少秒                             | k8s 1.20 版本前 exec 探测方式此参数失效）     | 1      | 1      |
| successThreshold    | 探测器在失败后，容器状态改为成功的最小连续成功数   | livenessProbe和readinessProbe的这个值必须是 1 | 1      | 1      |
| failureThreshold    | 当探测失败时，重试次数                             |                                               | 3      | 1      |

## 分析

通过上文对健康检查有了基本的了解，下面探讨如何结合当前应用使用。

目前大多数应用没有使用探针，仅能监控容器进程，每个应用都应该加上 livenessProbe 和 readinessProbe

探针不能占用过多的资源，且不能占用过长的时间，否则所有资源都在做健康检查，这就没有意义了。例如Java应用，就最好用HTTP GET方式，如果用Exec方式，JVM启动就占用了非常多的资源。

### 探针使用场景

#### livenessProbe

容器重启消耗资源较多，如果应用重启不能从错误中恢复，则没必要重启，因此 livenessProbe 的敏感度应该低一些

典型场景：

- 线程夯住，死锁
- 内存泄漏，OOM

#### readinessProbe

readinessProbe 不同于 livenessProbe，当应用不能对外提供正确服务时，就应该不被外界访问，因此敏感度应该高一点

典型场景：

- 重要依赖不可用，连不上数据库
- 忙于消费消息，暂时拒绝流量
- 尚未启动成功，暂时拒绝流量
- 启动完成后初始化配置未完成，例如springboot CommandRunner，暂时拒绝流量

#### 探针应该运行在主进程中

如果不在同一上下文，可能主进程其实连接池满了已经不能接受请求了，但探针一切正常

#### 其他相关改动

日志收集和流量统计要忽略健康检查流量

## 容器测改动业界实现

springboot 2.3.0 版本开始支持 k8s 探针，并且此版本支持优雅关机（kill -9 变成 kill -2）

https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/html/production-ready-features.html#production-ready-kubernetes-probes

springboot 启动过程

| Application startup phase | Liveness State | Readiness State     | Notes                                                        |
| ------------------------- | -------------- | ------------------- | ------------------------------------------------------------ |
| Starting                  | `BROKEN`       | `REFUSING_TRAFFIC`  | Kubernetes checks the "liveness" Probe and restarts the application if it takes too long. |
| Started                   | `CORRECT`      | `REFUSING_TRAFFIC`  | The application context is refreshed. The application performs startup tasks and does not receive traffic yet. |
| Ready                     | `CORRECT`      | `ACCEPTING_TRAFFIC` | Startup tasks are finished. The application is receiving traffic. |

springboot 关机过程

| Application shutdown phase | Liveness State | Readiness State | Notes                                                        |
| -------------------------- | -------------- | --------------- | ------------------------------------------------------------ |
| Running                    | live           | ready           | Shutdown has been requested.                                 |
| Graceful shutdown          | live           | unready         | If enabled, [graceful shutdown processes in-flight requests](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/html/spring-boot-features.html#boot-features-graceful-shutdown). |
| Shutdown complete          | broken         | unready         | The application context is closed and the application cannot serve traffic. |

springboot actuator 默认提供了 liveness，readiness 端点的实现供探针使用。默认实现只会检查容器本身的状态，也就是上面两表中的状态。

- livenessProbe 默认实现即可
- readinessProbe 应该检查服务的关键依赖，springboot actuator 提供了主要中间件的扩展，例如数据库，redis等。可以进行动态配置，相关依赖的检查方法几乎都是创建一个连接即可。

[项目代码](https://github.com/freshchen/fresh-keeping/tree/master/java/springboot/spring-actuator)


## 参考链接

https://kubernetes.io/zh/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/

https://www.magalix.com/blog/kubernetes-and-containers-best-practices-health-probes

[https://segmentfault.com/a/1190000008232770](https://segmentfault.com/a/1190000008232770)

##### 标签
#k8s 
