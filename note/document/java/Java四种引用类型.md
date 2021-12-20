---
begin: 2021-12-20
status: ongoing
rating: 3
---

# Java四种引用类型

## 强引用

`StringBuffer buffer = new StringBuffer();`

变量 buffer 强引用堆上 new 出来的 StringBuffer 对象，对于强引用 buffer 在我们使用完也就是设置 buffer 为 null前，垃圾收集器都不会回收 StringBuffer 对象。
大部分业务开发过程中我们都应该使用强引用，


## 参考链接

[Understanding Weak References](https://web.archive.org/web/20061130103858/http://weblogs.java.net/blog/enicholas/archive/2006/05/understanding_w.html)

[Java四种引用类型原理你真的搞明白了吗？五分钟带你深入理解！ - SegmentFault 思否](https://segmentfault.com/a/1190000039994284)

##### 标签
#interview 