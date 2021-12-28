---
begin: 2021-12-22
status: ongoing
rating: 1
---

# TransmittableThreadLocal源码

## 简介

[TTL Github 地址](https://github.com/alibaba/transmittable-thread-local)

上文 [ThreadLocal源码分析](ThreadLocal源码分析.md) 对 ThreadLocal 以及 InheritableThreadLocal 有大致的了解，本文探讨 TransmittableThreadLocal 究竟是如何解决线程池上下文传递问题的。

[https://github.com/alibaba/transmittable-thread-local/blob/master/docs/performance-test.md](https://github.com/alibaba/transmittable-thread-local/blob/master/docs/performance-test.md)

# 源码分析

>transmittable-thread-local 2.12.2

## 1 TransmittableThreadLocal

TransmittableThreadLocal 继承 InheritableThreadLocal，并增加了 disableIgnoreNullValueSemantics 和 holder 两个属性

### 1.1  disableIgnoreNullValueSemantics

disableIgnoreNullValueSemantics 默认为 false，也就说当我们设置 `transmittableThreadLocal.set(null)` 时不会传递到子线程中，阿里编码规约 null 没有意义，建议传递有意义的值。这一点和 ThreadLocal 语义不同，如果想保持相同语义，可以构造函数传入 true。

[对于 set(null) 保持和 InheritableThreadLocal 一致语义？ · Issue #157 · alibaba/transmittable-thread-local · GitHub](https://github.com/alibaba/transmittable-thread-local/issues/157)

### 1.2 holder

```java
private static final InheritableThreadLocal<WeakHashMap<TransmittableThreadLocal<Object>, ?>> holder =
	new InheritableThreadLocal<WeakHashMap<TransmittableThreadLocal<Object>, ?>>() {
		@Override
		protected WeakHashMap<TransmittableThreadLocal<Object>, ?> initialValue() {
			return new WeakHashMap<TransmittableThreadLocal<Object>, Object>();
		}

		@Override
		protected WeakHashMap<TransmittableThreadLocal<Object>, ?> childValue(WeakHashMap<TransmittableThreadLocal<Object>, ?> parentValue) {
			return new WeakHashMap<TransmittableThreadLocal<Object>, Object>(parentValue);
		}
	};
```

## 2 TransmittableThreadLocal#set

首先看赋值操作

```java
public final void set(T value) {
	// 默认设置了 null 值，需要从 
	if (!disableIgnoreNullValueSemantics && null == value) {
		// may set null to remove value
		remove();
	} else {
		super.set(value);
		addThisToHolder();
	}
}
```


## 参考链接


##### 标签
#blog 