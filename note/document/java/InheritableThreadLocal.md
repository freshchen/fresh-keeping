---
begin: 2021-11-18
status: ongoing
rating: 1
---

# InheritableThreadLocal

### 如何传递到子线程

InheritableThreadLocal 继承自 ThreadLocal。InheritableThreadLocal 存放在 Thread.inheritableThreadLocals 字段中。

创建线程构造方法中，会从主线中传递，ThreadLocal.createInheritedMap 是深拷贝

```java
if (inheritThreadLocals && parent.inheritableThreadLocals != null)  
    this.inheritableThreadLocals =  
        ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);

```

### 存在问题

线程池中，因为线程被复用，不会调用构造器导致 InheritableThreadLocal 无法传递到子线程

解决方案 [TransmittableThreadLocal](TransmittableThreadLocal.md)

## 参考链接


##### 标签
#java #source_code 