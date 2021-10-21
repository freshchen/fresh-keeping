---
begin: 2021-10-20
status: ongoing
rating: 1
---

# HashMap源码

## 数据结构

数组+链表+红黑树

> 红黑树是在 JDK1.8 版本才引入的，目的是加快链表的查询效率

链表 element >8 变为红黑树，红黑树 element <6 转回链表

![](image/Pasted%20image%2020211020112905.png)


## 创建

- initialCapacity：初始化容量，从而计算出 threshold（扩容阈值）
- loadFactor：加载因子


## put过程

- 根据对象key 计算 hash = `(key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);`
- 第一次 put，hash table 数组未初始化，需要根据创建参数创建 table `tab = resize()`
- 定位数组桶位置 `i = (n - 1) & hash`
- 如果桶不存在先创建桶 `tab[i] = newNode(hash, key, value, null);`
- 如果 hash 和原来桶的 hash 一样，且值也相等，则不操作
- 根据桶的形态，插入红黑树或者链表

## 参考链接

https://www.cnblogs.com/niumoo/p/12602849.html


##### 标签
#java #interview 