# Java读源码之ThreadLocal

## 前言

> JDK版本: 1.8 

之前在看Thread源码时候看到这么一个属性

```java
ThreadLocal.ThreadLocalMap threadLocals = null;
```

ThreadLocal实现的是每个线程都有一个本地的副本，相当于局部变量，其实ThreadLocal就是内部自己实现了一个map数据结构。

ThreadLocal确实很重要，但想到看源码还是有个小故事的，之前去美团点评面试，问我如何保存用户登录token，可以避免层层传递token？

心想这好像是在说ThreadLocal，然后开始胡说放在redis里或者搞个ThreadLocal，给自己挖坑了

面试官继续问，ThreadLocal使用时候主要存在什么问题么？

完蛋，确实只了解过，没怎么用过，凉凉，回来查了下主要存在的问题如下

- **ThreadLocal可能内存泄露？**


带着疑惑进入源码吧

## 源码

### 类声明和重要属性

```java
package java.lang;

public class ThreadLocal<T> {
    
    // hash值，类似于Hashmap，用于计算放在map内部数组的哪个index上
    private final int threadLocalHashCode = nextHashCode();
    private static int nextHashCode() { return nextHashCode.getAndAdd(HASH_INCREMENT);}
	// 初始0
    private static AtomicInteger nextHashCode = new AtomicInteger();
	// 神奇的值，这个hash值的倍数去计算index，分布会很均匀，总之很6 
    private static final int HASH_INCREMENT = 0x61c88647;
    
    static class ThreadLocalMap {

        // 注意这是一个弱引用
        static class Entry extends WeakReference<ThreadLocal<?>> {
            Object value;

            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
        // 初始容量16，一定要是2的倍数
        private static final int INITIAL_CAPACITY = 16;
        // map内部数组
        private Entry[] table;
        // 当前储存的数量
        private int size = 0;
        // 扩容指标，计算公式 threshold = 总容量 * 2 / 3，默认初始化之后为10
        private int threshold;
```

### 增改操作

让我们先来看看增改方法

```java
public void set(T value) {
    Thread t = Thread.currentThread();
    // 拿到当前Thread对象中的threadLocals引用，默认threadLocals值是null 
    ThreadLocalMap map = getMap(t);
    if (map != null)
        // 如果ThreadLocalMap已经初始化过，就把当前ThreadLocal实例的引用当key，设置值
        map.set(this, value); //下文详解
    else
        // 如果不存在就创建一个ThreadLocalMap并且提供初始值
        createMap(t, value);
}

ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}

void createMap(Thread t, T firstValue) {
    t.threadLocals = new ThreadLocalMap(this, firstValue);
}
```

让我们来看看map.set(this, value)具体怎么操作ThreadLocalMap

```java
private void set(ThreadLocal<?> key, Object value) {
	// 获取ThreadLocalMap内部数组
    Entry[] tab = table;
    int len = tab.length;
    // 算出需要放在哪个桶里
    int i = key.threadLocalHashCode & (len-1);
	// 如果当前桶冲突了，这里没有用拉链法，而是使用开放定指法，index递增直到找到空桶，数据量很小的情况这样效率高
    for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
        // 拿到目前桶中key
        ThreadLocal<?> k = e.get();
		// 如果桶中key和我们要set的key一样，直接更新值就ok了
        if (k == key) {
            e.value = value;
            return;
        }
		// 桶中key是null，因为是弱引用，可能被回收掉了，这个时候我们直接占为己有，并且进行cleanSomeSlots，当前key附近局部清理其他key是空的桶
        if (k == null) {
            replaceStaleEntry(key, value, i);
            return;
        }
    }
	// 如果没冲突直接新建
    tab[i] = new Entry(key, value);
    int sz = ++size;
    // 当前key附近局部清理key是空的桶，如果一个也没清除并且当前容量超过阈值了就扩容
    if (!cleanSomeSlots(i, sz) && sz >= threshold)
        rehash();
}


private void rehash() {
    // 这个方法会清除所有key为null的桶，清理完后size的大小会变小
    expungeStaleEntries();

    // 此时size还大于阈值的3/4就扩容
    if (size >= threshold - threshold / 4)
        // 2倍扩容
        resize();
}
```

### 为什么会内存泄漏

总算读玩了set，大概明白了为什么会发生内存泄漏，画了个图

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/sc-threadlocal.jpg)

ThreadLocalMap.Entry中的key保存了ThreadLocal实例的一个弱引用，如果ThreadLocal实例栈上的引用断了，只要GC一发生，就铁定被回收了，此时Entry的key，就是null，但是呢Entry的value是强引用而且是和Thread实例生命周期绑定的，也就是线程没结束，值就一直不会被回收，所以产生了内存泄漏。

总算明白了，为什么一个set操作要这么多次清理key为null的桶。

**既然这么麻烦，为什么key一定要用弱引用？**

继续看上面的图，如果我们的Entry中保存的是ThreadLocal实例的一个强引用，我们删掉了ThreadLocal栈上的引用，同理此时不仅value就连key也不会回收了，这内存泄漏就更大了

### 查询操作

```java
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);  //下文详解
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    // 返回null
    return setInitialValue();
}

private T setInitialValue() {
    T value = initialValue();
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        // 如果只是threadLocals.Entry是空，就设置value为null
        map.set(this, value);
    else
        // 如果threadLocals是空，就new 一个key是当前ThreadLocal，value是空的ThreadLocalMap
        createMap(t, value);
    return value;
}

protected T initialValue() {
    return null;
}
```

让我们来看看map.getEntry(this)具体怎么操作ThreadLocalMap

```java
private Entry getEntry(ThreadLocal<?> key) {
    int i = key.threadLocalHashCode & (table.length - 1);
    Entry e = table[i];
    if (e != null && e.get() == key)
        // 最好情况，定位到了Entry，并且key匹配
        return e;
    else
        // 可能是hash冲突重定址了，也可能是key被回收了
        return getEntryAfterMiss(key, i, e);
}

private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
    Entry[] tab = table;
    int len = tab.length;
	// 向后遍历去匹配key，同时清除key为null的桶
    while (e != null) {
        ThreadLocal<?> k = e.get();
        if (k == key)
            return e;
        if (k == null)
            expungeStaleEntry(i);
        else
            i = nextIndex(i, len);
        e = tab[i];
    }
    return null;
}
```

### 如何避免内存泄漏

新增，查询中无处不在的去清理key为null的Entry，是不是我们就可以放心了，大多数情况是的，但是如果我们在使用线程池，核心工作线程是不会停止的，会重复利用，这时我们的Entry中的value就永远不会被回收了这很糟糕，还好源码作者还没给我提供了remove方法，综上所述，养成良好习惯，只要使用完ThreadLocal，一定要进行remove防止内存泄漏

```java
public void remove() {
    ThreadLocalMap m = getMap(Thread.currentThread());
    if (m != null)
        m.remove(this);
}

private void remove(ThreadLocal<?> key) {
    Entry[] tab = table;
    int len = tab.length;
    int i = key.threadLocalHashCode & (len-1);
    for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
        if (e.get() == key) {
            // 主要多了这一步，让this.referent = null，GC会提供特殊处理
            e.clear();
            expungeStaleEntry(i);
            return;
        }
    }
}
```

