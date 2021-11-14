---
begin: 2021-10-20
status: ongoing
rating: 1
---



# HashMap源码

## 数据结构

数组+链表+红黑树

> 红黑树是在 JDK1.8 版本才引入的，目的是加快链表的查询效率

[链表红黑树转换规则](HashMap源码.md#链表红黑树转换规则)

数组最大容量 2的30次方

![](image/Pasted%20image%2020211020112905.png)


## 创建

- initialCapacity：初始化容量，从而计算出 threshold（扩容阈值）向上取2的次方
- loadFactor：加载因子


## put过程

- 根据对象key 计算 [hash](HashMap源码.md#计算hash)
- 第一次 put，hash table 数组未初始化，需要根据创建参数创建 table  = [扩容resize](HashMap源码.md#扩容resize)
- 定位数组桶位置 `i = (n - 1) & hash`
- 如果桶不存在先创建桶 `tab[i] = newNode(hash, key, value, null);`
- 如果 hash 和原来桶的 hash 一样，且值也相等，则不操作
- 根据桶的形态，插入红黑树或者链表
	- 插入链表使用尾部插法，元素个数大于8转红黑树
	- 如果key一样，返回过去的value 🔚
- 如果超过了 threshold 阈值进行 [扩容resize](HashMap源码.md#扩容resize)
- 返回 null 🔚

## get过程
- 根据对象key 计算 [hash](HashMap源码.md#计算hash)
- 如果 table 为空，或者桶中没数据，返回null🔚
- 判断桶第一个元素是否所需结果，是则返回🔚
- 否则根据桶类型，进行红黑树查询或者链表遍历

## 扩容resize

-  如果当前长度已经大于最大容量2的30次方，则不扩容
- 确认新容量 capacity 和阈值 threshold
	- 如果创建指定了容量，则 capacity = 创建适合计算的 threshold
	- 如果创建没指定，则默认 capacity = 16，threshold = 12（capacity ✖ 加载因子0.75）
	- 如果扩大两倍之后小于最大容量，capacity *= 2，threshold *= 2
	- threshold = capacity ✖ 加载因子
- 根据 capacity 创建新 table，如果是初始化之间返回 table 🔚
- 如果是链表，原来元素要么还在原来的桶上，要么就到原来桶 + 原来capacity的桶上

## 计算hash
使用key计算，其中 ^异或高16位，增加扰动，可以减少低位相同时数据插入冲突的概率，使得 hash 值分配更加均匀
```
static final int hash(Object key) {  
    int h;  
 	return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);  
}
```


## 桶定位

[计算hash](HashMap源码.md#计算hash) 结果为 hash

```
桶位置 = (n - 1) & hash
```



## 容量为什么 2 的次方

[桶定位](HashMap源码.md#桶定位) 使用 & 与操作提高性能，并且降低碰撞的概率

## 链表红黑树转换规则

链表 element >8 变为红黑树，红黑树 element <6 转回链表
为了避免来回转换，所以6和8间隔了1

## 线程安全问题

- put 更新丢失
	- A 定位到桶，准备插入，此时时间片用完
	- B 进入并定位到同一个桶，B 写入
	- A 进入，通过过期桶引用覆盖了 B 的写入
- resize 死循环（jdk1.8之前）
	- 因为使用头插法扩容
	- 可能导致链表倒置
	- 下一次 get 到此桶，死循环，耗尽CPU

## 参考链接

https://www.cnblogs.com/niumoo/p/12602849.html


##### 标签
#java #interview #source_code 