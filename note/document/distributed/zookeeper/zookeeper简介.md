---
begin: 2021-10-05
status: ongoing
rating: 1
---

# zookeeper简介

## 什么是zookeeper

> A Distributed Coordination Service for Distributed Applications

zookeeper 是一个开源的集中式的分布式协同服务系统，是由雅虎研究院借鉴 [Chubby](../chubby/Chubby简介.md) 完成的一个开源实现。解决了分布式应用协同过程中都会遇到的单点问题。

### 特点

- 一致性 consistency
- 高可用 high performance
- 高性能  highly available
- 顺序性  ordered access
- 简单 simple
- 等待无关 wait-free

### 使用场景

**适合场景**
- 配置管理
- 名字服务（naming）
- 组成员组信息服务（group service）
- 分布式同步
- 分布式锁
- 投票选举

**不适合场景**
- 作为数据库使用，存储大数据量


### 使用案例

- Hadoop：NameNode 的高可用
- HBase：选举，保存集群中 RegionServer 信息，保存 hbase:meta 元数据信息表的位置
- Kafka（高版本已经逐步移除 zookeeper）：集群组成员管理，控制节点选举
- HDFS: HA方案
- YARN: HA方案
- Flume: 负载均衡,单点故障

## 架构

zookeeper 是典型的 C/S 架构，分为 service 层 和 client 层。

**service 层**

- zookeeper servers 之间需要互相联系，它们在内存中维护其他 server 的信息包括状态，并通过事务日志和快照进行持久化
- zookeeper 必须由一个 leader ，初次启动过程中完成选举
- 只要超过半数 server 处于可用状态 zookeeper 集群就能正常访问

![](image/Pasted%20image%2020211005172352.png)


## 参考链接

[官网](https://zookeeper.apache.org/)
[架构简介](https://zookeeper.apache.org/doc/r3.6.3/zookeeperOver.html#sc_designGoals)

##### 标签
#zookeeper #distributed #interview 