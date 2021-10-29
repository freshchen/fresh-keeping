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


## 参考链接

https://kubernetes.io/zh/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/

https://www.magalix.com/blog/kubernetes-and-containers-best-practices-health-probes

##### 标签
#k8s 
