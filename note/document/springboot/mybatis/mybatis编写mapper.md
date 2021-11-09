---
begin: 2021-10-14
status: done
rating: 1
---

# mybatis编写mapper

### xml顶级标签

sql语句

- `insert` – 映射插入语句。
- `update` – 映射更新语句。
- `delete` – 映射删除语句。
- `select` – 映射查询语句。

其他

- `cache` – 该命名空间的缓存配置。
- `cache-ref` – 引用其它命名空间的缓存配置。
- `resultMap` – 描述如何从数据库结果集中加载对象，是最复杂也是最强大的元素。
- `sql` – 可被其它语句引用的可重用语句块。

### 动态SQL标签

- if
- choose、when、otherwise
- trim、where、set
- foreach : 可以将任何可迭代对象（如 List、Set 等）、Map 对象或者数组对象作为集合参数传递给  foreach。当使用可迭代对象或者数组时，index 是当前迭代的序号，item 的值是本次迭代获取到的元素。当使用 Map 对象（或者 Map.Entry 对象的集合）时，index 是键，item 是值
- bind


## 参考链接

https://mybatis.org/mybatis-3/zh/sqlmap-xml.html

##### 标签
#orm #database