---
begin: 2021-10-12
status: ongoing
rating: 1
---

# json简介

> **JSON** (**JavaScript Object Notation**) is an open standard file format and data interchange format that uses human-readable text to store and transmit data objects consisting of attribute–value pairs  (or other serializable values). It is a common data  format with a diverse range of functionality in data interchange including communication of  web applications with servers.

Json 是一种数据结构化方式，简单好用，跨平台且可读性好。被广泛运用在 web 应用开发。

### 结构规范

- 只有两种结构：对象内的键值对集合结构和数组，对象用{}表示、内部是”key”:”value”，数组用[]表示，不同值用逗号分开
- 基本数值有7个： false / null / true / object / array / number / string

[Google json 风格规范](../design/Google%20json%20风格规范.md)

### 优缺点

优点

- 可读性好
- 规范简单
- 跨平台

缺点

- 性能，json 比二进制传输消耗大
- 规范过于简单，缺乏约束

### 使用场景

比较适合场景

- 前后端交互
- 三方系统交互

一般适合场景

- 内部系统交互，性能不如二进制
- 配置描述，没有yaml方便

### 相关实现

[fastjson简介](../fastjson/fastjson简介.md)

## 参考链接

http://kimmking.github.io/2017/06/06/json-best-practice/

https://en.wikipedia.org/wiki/JSON

##### 标签
#json #api
