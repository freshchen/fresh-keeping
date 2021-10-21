---
begin: 2021-10-19
status: ongoing
rating: 1
---

# caffeine简介

[官方wiki](https://github.com/ben-manes/caffeine/wiki)

Caffeine是一个基于Java8开发的提供了近乎最佳命中率的高性能的缓存库。

缓存和 ConcurrentMap 有点相似，但还是有所区别。最根本的区别是 ConcurrentMap 将会持有所有加入到缓存当中的元素，直到它们被从缓存当中手动移除。但是，Caffeine的缓存`Cache` 通常会被配置成自动驱逐缓存中元素，以限制其内存占用。在某些场景下，`LoadingCache`和`AsyncLoadingCache` 因为其自动加载缓存的能力将会变得非常实用。

Caffeine提供了灵活的构造器去创建一个拥有下列特性的缓存：

-   自动加载元素到缓存当中，异步加载的方式也可供选择
-   当达到最大容量的时候可以使用基于就近度和频率的算法进行基于容量的驱逐
-   将根据缓存中的元素上一次访问或者被修改的时间进行基于过期时间的驱逐
-   当向缓存中一个已经过时的元素进行访问的时候将会进行异步刷新
-   key将自动被弱引用所封装
-   value将自动被弱引用或者软引用所封装
-   驱逐(或移除)缓存中的元素时将会进行通知
-   写入传播到一个外部数据源当中
-   持续计算缓存的访问统计指标

### 术语

-   **驱逐** 缓存元素因为策略被移除
-   **失效** 缓存元素被手动移除
-   **移除** 由于驱逐或者失效而最终导致的结果

### 加载方式
- 手动加载
- 自动加载
- 手动异步加载
- 自动异步加载

### 驱逐方式
- 容量
	- 大小
	- 权重
- 时间
	- 访问
	- 更新
	- 自定义
- 引用

### 移除

- 显示移除
- 移除监听器

### 统计信息

`CacheStats{hitCount=0, missCount=1, loadSuccessCount=0, loadFailureCount=0, totalLoadTime=0, evictionCount=0, evictionWeight=0}`

支持集成 [Prometheus](https://prometheus.io/)

### 清理

超时之后默认不会立刻清除，等待下次写操作到来才部分清除，也可以指定单独线程去清理。

## 参考链接

##### 标签
