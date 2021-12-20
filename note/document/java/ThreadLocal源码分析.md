---
begin: 2021-12-17
status: ongoing
rating: 1
---

# ThreadLocal源码分析

## 简介

ThreadLocal 提供了线程私有的局部变量，实现了线程的数据隔离，是线程安全的。主要运用场景：
- 存储登录信息等上下文
- 存储日志上下文
- 存储一些创建开销较大的线程不安全对象，例如 SimpleDateFormat

## 源码分析

>JDK版本1.8

### 1 ThreadLocal#set

既然 ThreadLocal 是线程变量，那么就从其赋值方法说起

```java
public void set(T value) {
	Thread t = Thread.currentThread();
	// 1.1 获取当前线程变量 Map
	ThreadLocalMap map = getMap(t);
	if (map != null)
		// 1.2 真正的设置值操作
		map.set(this, value);
	else
		// 1.3 如果没有初始化则创建线程变量 Map
		createMap(t, value);
}
```

#### 1.1 ThreadLocal#getMap

```java
ThreadLocalMap getMap(Thread t) {
	// 1.1.1 所有变量保存在 Thread 线程类的 threadLocals 中
	return t.threadLocals;
}
```

##### 1.1.1 Thread.threadLocals

```java
// 1.1.2 threadLocals 又是 ThreadLocal的一个内部类，所以变量虽然保存在 Thread 从而实现了线程隔离的效果，但还是由 ThreadLocal管理
ThreadLocal.ThreadLocalMap threadLocals = null;
```

##### 1.1.2 ThreadLocal.ThreadLocalMap

```java
// 类似 hashmap
static class ThreadLocalMap {
	// 弱引用帮助 ThreadLocal 能随着线程不用后被 GC 回收
	static class Entry extends WeakReference<ThreadLocal<?>> {
		Object value;
		Entry(ThreadLocal<?> k, Object v) {
			super(k);
			value = v;
		}
	}
	// 默认初始化大小
	private static final int INITIAL_CAPACITY = 16;
	private Entry[] table;
}
```

至此我们可以了解到栈上通过 ThreadLocal Ref 操作 ThreadLocal 修改当前 Thread  变量 threadLocals 。如下图所示：

![](image/Pasted%20image%2020211220164453.png)
##### 内存泄漏

这里分析下 ThreadLocal 的内存泄漏问题，进行可达性分析

- 首先分析 ThreadLocal Ref
	- ThreadLocal Ref 存在，说明正在使用不会被回收
	- ThreadLocal Ref 不存在， ThreadLocalMap 中 Entry 的 key 是弱引用，因此发生 GC 后堆上的 ThreadLocal 就被回收掉了

- 再分析 Current Thread Ref， 前提是 ThreadLocal Ref 已经不存在了
	- Current Thread Ref 存在，由于 ThreadLocalMap 中 Entry 的 key 已经被回收了，value 没回收却再也不会被引用到，因此出现了内存泄漏
	-  Current Thread Ref 不存在，也就是线程销毁了，那图中所有对象全都会被 GC 回收

综上只有 Current Thread Ref 存在，ThreadLocal Ref 不存在时才会发生内存泄漏，换句话说只有线程池或者守护线程会存在这个问题，因此在使用线程池时一定要注意 ThreadLocal 的主动清理

#### 1.2 ThreadLocalMap#set

继续 set 方法中的 map.set

```java
private void set(ThreadLocal<?> key, Object value) {
	Entry[] tab = table;
	int len = tab.length;
	// 类似 hashmap 算 hash 定位桶，threadLocalHashCode 每次递增 0x61c88647（斐波那契数），使得分布更加均匀
	int i = key.threadLocalHashCode & (len-1);
	// 处理碰撞
	for (Entry e = tab[i];
		 e != null;
		 e = tab[i = nextIndex(i, len)]) {
		ThreadLocal<?> k = e.get();
		// 当前 ThreadLocal 之前设置过值，那就用最新值覆盖
		if (k == key) {
			e.value = value;
			return;
		}
		// 
		if (k == null) {
			replaceStaleEntry(key, value, i);
			return;
		}
	}

	tab[i] = new Entry(key, value);
	int sz = ++size;
	if (!cleanSomeSlots(i, sz) && sz >= threshold)
		rehash();
}
```

#### 1.3 ThreadLocal#createMap

继续看 set 方法中的 createMap

```java
void createMap(Thread t, T firstValue) {
	// key 是当前 ThreadLocal 引用，value 是 set 传入的值
	t.threadLocals = new ThreadLocalMap(this, firstValue);
}
```

至此 set 方法基本清楚了，下面看 get

## 2 get

```java
public T get() {
	Thread t = Thread.currentThread();
	ThreadLocalMap map = getMap(t);
	if (map != null) {
		// 用当前 ThreadLocal 引用获取值
		ThreadLocalMap.Entry e = map.getEntry(this);
		if (e != null) {
			@SuppressWarnings("unchecked")
			T result = (T)e.value;
			return result;
		}
	}
	// 2.1 如果 Entry 中查不到，查询当前 ThreadLocal 默认值，默认返回是 null
	return setInitialValue();
}
```

### 2.1 setInitialValue

```java
private T setInitialValue() {
	// initialValue 默认返回 null
	T value = initialValue();
	// 以下类似 set 逻辑
	Thread t = Thread.currentThread();
	ThreadLocalMap map = getMap(t);
	if (map != null)
		map.set(this, value);
	else
		createMap(t, value);
	return value;
}
```


## 参考链接

[面试官：小伙子，听说你看过ThreadLocal源码？（万字图文深度解析ThreadLocal） - SegmentFault 思否](https://segmentfault.com/a/1190000022663697)

##### 标签
#blog
