---
begin: 2021-11-10
status: done
rating: 5
---

# ThreadLocal

### 作用

ThreadLocal 提供了线程私有的局部变量，实现了线程的数据隔离，是线程安全的。


### 工作中应用场景

- 存储登录信息等上下文
- 存储一些创建开销较大的线程不安全对象，例如 SimpleDateFormat

### Spring中ThreadLocal的应用

Spring提供了事务相关的操作，而我们知道事务是得保证一组操作同时成功或失败的，意味着我们一次事务的所有操作需要在同一个数据库连接上。Spring就是用的ThreadLocal来实现，Map中的key 是DataSource，value 是Connection，确保了同线程同数据源使用的是同一个数据库连接。


### 源码结构分析

^53afde
- 内部类
	- SuppliedThreadLocal：用于初始化
	- ThreadLocalMap：Thread 中 threadLocals 字段实际引用
		- 内部类
			- Entry extends WeakReference\<ThreadLocal\<?\>\> 又通过弱引用指向 ThreadLocal
- 方法
	- 增删改都是基于当前线程操作线程中的 threadLocals 引用


所以 ThreadLocal 为 Thread 提供了一个私有变量容器（类似Map）。然后作为门面为线程提供了本地变量容器的方法。

![](image/Pasted%20image%2020211118162453.png)


###  为什么 ThreadLocalMap.Entry 的 key 不是 Thread

- 一个线程可能有多个变量
- ThreadLocalMap 维护所有线程的话会一直膨胀，并且生命周期和 Thread 脱离了
	
###  内存泄漏

如 [源码结构](#^53afde) 中图所示，ThreadLocal 有两个引用，1栈上强引用 2 ThreadLocalMap.Entry 中弱引用。
- 原理
	- 只要栈上强引用断了，且发生GC，被弱引用的 ThreadLocal 就会被回收，此时 value 还在却不会被使用了，造成内存泄漏。
- 触发场景
	- ThreadLocal 栈上强引用断了（ThreadLocal 被置为 null）
	- ThreadLocal 是跟随 Thread 生命周期的，所以只有线程被复用（线程池）才会长期泄漏
	- ThreadLocal 的 get set remove 方法都会检测并清除泄漏的 value，只有再也不使用的时候才会泄漏
- 为什么ThreadLocalMap.Entry不用强引用
	- 如果用强引用，整个ThreadLocal都不被回收，内存泄漏更大了，弱引用就是帮助自动清理的
- 如何防止
	- 一般建议将其声明为`static final`的，避免频繁创建ThreadLocal实例
	-  尽量避免存储大对象
	- 在访问完成后及时调用`remove()`

	
### 如何传递到子线程

ThreadLocal 支持传递到子线程
	
[InheritableThreadLocal](InheritableThreadLocal.md)
	
## 参考链接
https://zhuanlan.zhihu.com/p/343254289


##### 标签
#interview #java #source_code 