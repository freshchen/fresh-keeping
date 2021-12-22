---
begin: 2021-12-17
status: done
rating: 1
---

# ThreadLocal源码分析

## 简介

ThreadLocal 提供了线程私有的局部变量，实现了线程的数据隔离，是线程安全的。主要运用场景：
- 存储登录信息等上下文
- 存储日志上下文
- 存储一些创建开销较大的线程不安全对象，例如 SimpleDateFormat

# 源码分析

>JDK版本1.8

## 1 ThreadLocal#set

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

### 1.1 ThreadLocal#getMap

```java
ThreadLocalMap getMap(Thread t) {
	// 1.1.1 所有变量保存在 Thread 线程类的 threadLocals 中
	return t.threadLocals;
}
```

#### 1.1.1 Thread.threadLocals

```java
// 1.1.2 threadLocals 又是 ThreadLocal的一个内部类，所以变量虽然保存在 Thread 从而实现了线程隔离的效果，但还是由 ThreadLocal管理
ThreadLocal.ThreadLocalMap threadLocals = null;
```

#### 1.1.2 ThreadLocal.ThreadLocalMap

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
#### 内存泄漏

这里分析下 ThreadLocal 的内存泄漏问题，进行可达性分析

- 首先分析 ThreadLocal Ref
	- ThreadLocal Ref 存在，说明正在使用不会被回收
	- ThreadLocal Ref 不存在， ThreadLocalMap 中 Entry 的 key 是弱引用，因此发生 GC 后堆上的 ThreadLocal 就被回收掉了

- 再分析 Current Thread Ref， 前提是 ThreadLocal Ref 已经不存在了
	- Current Thread Ref 存在，由于 ThreadLocalMap 中 Entry 的 key 已经被回收了，value 没回收却再也不会被引用到，因此出现了内存泄漏
	-  Current Thread Ref 不存在，也就是线程销毁了，jvm 会调用 Thread#init 方法，将 threadLocals 以及 inheritableThreadLocals 都设置为 null，帮助垃圾回收掉 Current Thread Ref 这条链路

综上只有 Current Thread Ref 存在，ThreadLocal Ref 不存在时才会发生内存泄漏，换句话说只有线程池或者守护线程会存在这个问题，因此在使用线程池时一定要注意 ThreadLocal 的主动清理

### 1.2 ThreadLocalMap#set

继续 set 方法中的 map.set

```java
private void set(ThreadLocal<?> key, Object value) {
	Entry[] tab = table;
	int len = tab.length;
	// 类似 hashmap 算 hash 定位桶，threadLocalHashCode 每次递增 0x61c88647（斐波那契数），使得分布更加均匀
	int i = key.threadLocalHashCode & (len-1);
	// 通过开放地址法处理碰撞，直到找到null的桶
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
			// 此时说明 Entry 过期了，也就是 ThreadLocal 被 GC 回收了，进行一次整理，释放掉泄漏的 value，然后占用这个桶
			replaceStaleEntry(key, value, i);
			return;
		}
	}

	tab[i] = new Entry(key, value);
	int sz = ++size;
	// 统一清理一次过期的 Entry，如果还是满了，则执行2倍扩容
	if (!cleanSomeSlots(i, sz) && sz >= threshold)
		rehash();
}
```

### 1.3 ThreadLocal#createMap

继续看 set 方法中的 createMap

```java
void createMap(Thread t, T firstValue) {
	// key 是当前 ThreadLocal 引用，value 是 set 传入的值
	t.threadLocals = new ThreadLocalMap(this, firstValue);
}
```

至此 set 方法基本清楚了，下面看 get

## 2 ThreadLocal#get

```java
public T get() {
	Thread t = Thread.currentThread();
	ThreadLocalMap map = getMap(t);
	if (map != null) {
		// 2.1 用当前 ThreadLocal 引用获取值
		ThreadLocalMap.Entry e = map.getEntry(this);
		if (e != null) {
			@SuppressWarnings("unchecked")
			T result = (T)e.value;
			return result;
		}
	}
	// 2.2 如果 Entry 中查不到，查询当前 ThreadLocal 默认值，默认返回是 null
	return setInitialValue();
}
```

### 2.1 ThreadLocalMap#getEntry


```java
private Entry getEntry(ThreadLocal<?> key) {
	int i = key.threadLocalHashCode & (table.length - 1);
	Entry e = table[i];
	if (e != null && e.get() == key)
		// 定位到 Entry 的 key 就是当前 ThreadLocal 直接返回
		return e;
	else
		// 2.1.1 此时说明发生了碰撞或者已经被 GC 回收了
		return getEntryAfterMiss(key, i, e);
}
```

### 2.1.1 ThreadLocalMap#getEntryAfterMiss


```java
private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
	Entry[] tab = table;
	int len = tab.length;
	// 开放地址法，循环找
	while (e != null) {
		ThreadLocal<?> k = e.get();
		if (k == key)
			// 找到了返回
			return e;
		if (k == null)
			// 发现有过期的 Entry 触发清理操作
			expungeStaleEntry(i);
		else
			i = nextIndex(i, len);
		e = tab[i];
	}
	return null;
}
```


### 2.2 ThreadLocal#setInitialValue

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


## 3 ThreadLocal#remove

最后是 remove 删除操作

```java
public void remove() {
 ThreadLocalMap m = getMap(Thread.currentThread());
 if (m != null)
	 // 3.1 删掉当前 ThreadLocal 对应 Entry
	 m.remove(this);
}
```

### 3.1 ThreadLocalMap#remove


```java
private void remove(ThreadLocal<?> key) {
	Entry[] tab = table;
	int len = tab.length;
	int i = key.threadLocalHashCode & (len-1);
	for (Entry e = tab[i];
		 e != null;
		 e = tab[i = nextIndex(i, len)]) {
		if (e.get() == key) {
			// 清理 Entry 弱引用
			e.clear();
			// 触发清理操作
			expungeStaleEntry(i);
			return;
		}
	}
}
```

## 4 ThreadLocal总结

- ThreadLocal 中实现的 Map 与 HashMap 实现上的区别
	- ThreadLocalMap Entry 是弱引用
	- 处理 hash 冲突 ThreadLocalMap 用开放地址法，HashMap 用链地址法
- ThreadLocal 实际只是一个工具类，不存储数据，数据存储在 Thread 中
- ThreadLocal 一般使用 	`static final` 作为常量创建，因此生命周期很长，不会被 GC 回收，不会内存泄漏
- 在使用线程池时，要防止用错 ThreadLocal，需要在任务跑完之后主动 remove 掉任务期间生成的 ThreadLocal
- ThreadLocal 的 get、set、remove 都会进行过期 Entry 清理，因此只要不是创建之后就不用了，不会内存泄漏
- 当我们想把当前线程变量传递到子线程时可以使用 InheritableThreadLocal

## 5 InheritableThreadLocal

### 5.1 InheritableThreadLocal
InheritableThreadLocal 继承自 ThreadLocal 代码很少

```java
public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    // 父线程取值赋给子线程，也就是父子值相等
    protected T childValue(T parentValue) {
        return parentValue;
    }

    // 所有 set get remove 从操作 Thread.threadLocals 变成操作 Thread.inheritableThreadLocals
    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }

    // 创建 ThreadLocalMap 放到 Thread.inheritableThreadLocals
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}
```

### 5.2 Thread#init

通过上面代码，我们知道对于  InheritableThreadLocal 来说所有变量都放在了 Thread.inheritableThreadLocals 中，那是怎么传递的子线程的呢，在线程创建的时候会调用 init 方法

```java
// 省略无关代码
private void init(ThreadGroup g, Runnable target, String name,
				  long stackSize, AccessControlContext acc,
				  // 除了指定 AccessControlContext 场景是 false，其他都是 true
				  boolean inheritThreadLocals) {
	// 如果使用的是 InheritableThreadLocal 父线程中就会有 inheritableThreadLocals
	if (inheritThreadLocals && parent.inheritableThreadLocals != null)
		// 5.2.1 复制到子线程 inheritableThreadLocals 中
		this.inheritableThreadLocals =
			ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
}
```

#### 5.2.1 ThreadLocalMap()

```java
private ThreadLocalMap(ThreadLocalMap parentMap) {
	Entry[] parentTable = parentMap.table;
	int len = parentTable.length;
	setThreshold(len);
	table = new Entry[len];
	// 从父线程中复制
	for (int j = 0; j < len; j++) {
		Entry e = parentTable[j];
		if (e != null) {
			@SuppressWarnings("unchecked")
			ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
			if (key != null) {
				// 对于 InheritableThreadLocal value 等于 e.value
				Object value = key.childValue(e.value);
				// key是引用传递
				Entry c = new Entry(key, value);
				int h = key.threadLocalHashCode & (len - 1);
				while (table[h] != null)
					h = nextIndex(h, len);
				table[h] = c;
				size++;
			}
		}
	}
}
```

## 参考链接

[面试官：小伙子，听说你看过ThreadLocal源码？（万字图文深度解析ThreadLocal） - SegmentFault 思否](https://segmentfault.com/a/1190000022663697)

##### 标签
#blog
