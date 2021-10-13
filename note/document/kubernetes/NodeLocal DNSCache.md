---
begin: 2021-10-13
status: ongoing
rating: 1
---

# NodeLocal DNSCache

## 简介

NodeLocal DNSCache 通过在群集节点上作为DaemonSet运行dns缓存代理来提高群集DNS性能。 在当今的体系结构中，处于ClusterFirst DNS模式的Pod可以连接到kube-dns serviceIP进行DNS查询。 通过kube-proxy添加的iptables规则将其转换为kube-dns / CoreDNS端点。 借助这种新架构，Pods将可以访问在同一节点上运行的dns缓存代理，从而**避免了iptables DNAT规则和连接跟踪**。 本地缓存代理将查询 kube-dns 服务以获取集群主机名的缓存缺失（默认为 cluster.local 后缀）。


## 参考链接

[官网](https://kubernetes.io/zh/docs/tasks/administer-cluster/nodelocaldns/)


##### 标签
#k8s