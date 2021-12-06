---
begin: 2021-11-10
status: done
rating: 1
---

# TransmittableThreadLocal

用于线程池等线程复用情况下上下文信息传递。

[Github](https://github.com/alibaba/transmittable-thread-local)

代码中使用 TransmittableThreadLocal 定义线程变量，并且通过 java agent 增强所有 Java 线程，线程池

## 源码分析

类重要信息：
- TransmittableThreadLocal 继承自 InheritableThreadLocal，创建线程时，会被带入子线程 inheritableThreadLocals 字段
- TransmittableThreadLocal 中 holder 属性用于保存线程相关所有的 TransmittableThreadLocal
	- holder 本身是InheritableThreadLocal。用 WeakHashMap 方便垃圾回收
	- key 是 TransmittableThreadLocal 本身
	- value 默认null，无意义

```java
private static final InheritableThreadLocal<WeakHashMap<TransmittableThreadLocal<Object>, ?>> holder =
	new InheritableThreadLocal<WeakHashMap<TransmittableThreadLocal<Object>, ?>>() {... ...};
```


核心流程如下：

![](image/Pasted%20image%2020211118181011.png)



- 1 createTtl
	- new TransmittableThreadLocal<>()
	- TransmittableThreadLocal 继承自 InheritableThreadLocal，创建线程时，会被带入子线程 inheritableThreadLocals 字段
- 2 setTtlValue

```java
@Override  
public final void set(T value) {  
	 if (!disableIgnoreNullValueSemantics && null == value) {  
	 // 设置null就清空 inheritableThreadLocals ，holder中删除自己
	 	 remove();  
	 } else { 
		 // 放入 inheritableThreadLocals
		 super.set(value);  
		 // 把自己放入 holder
		 addThisToHolder();  
	 }  
}
```

- 3 createBizTaskRunnable 正常创建需要异步执行的任务 Runnable
- 4 createTtlRunnableWrapper 创建 TtlRunnable 包装 Runnable
	- 4.1 catureAllTtlValues 调用 Transmitter.capture 生成快照 Snapshot
		- Snapshot.ttl2Value = captureTtlValues() 遍历 holder 的 key，复制所有 TransmittableThreadLocal
			- ttl2Value.key 为 TransmittableThreadLocal
			- ttl2Value.value 为 TransmittableThreadLocal 中值，浅拷贝
				- 4.1.1 [get](#^c6418f) 
				- 4.1.2 copy
		- Snapshot.threadLocal2Value = captureThreadLocalValues() 复制所有业务方主动注册的 ThreadLocal
	- 快照 Snapshot 放入 TtlRunnable 中 capturedRef 属性 


```java
@Override
public final T get() {
	// inheritableThreadLocals 中取
	T value = super.get();
	// 把自己放入 holder
	if (disableIgnoreNullValueSemantics || null != value) addThisToHolder();
	return value;
}

```

^c6418f

- 5 submitTtlRunnableToThreadPool 提交给线程池执行
- 6 run 线程池执行 TtlRunnable [run](#^23eb42) 方法
	- 6.1 capturedRef 属性中取出 Snapshot 快照
	- 6.2 replayCaturedTtlValues 重放 Snapshot 快照
		- holder 中 所有 TransmittableThreadLocal 备份 backup
		- 删掉 holder 中非 capturedRef 的 TransmittableThreadLocal
		- 删掉 非 capturedRef 的 inheritableThreadLocals 对应值 
		- 调用 TransmittableThreadLocal 提供的 扩展 beforeExecute
	- 6.3 运行 Runnable run 方法
	- 6.4 restoreTtlValues，通过 backup 复原
		- 调用 TransmittableThreadLocal 提供的 扩展 afterExecute
		- 删掉 holder 中非 backup 的 TransmittableThreadLocal
		- 删掉 非 backup 的 inheritableThreadLocals 对应值 


```java
public void run() {
	final Object captured = capturedRef.get();
	if (captured == null || releaseTtlValueReferenceAfterRun && !capturedRef.compareAndSet(captured, null)) {
		throw new IllegalStateException("TTL value reference is released after run!");
	}

	final Object backup = replay(captured);
	try {
		runnable.run();
	} finally {
		restore(backup);
	}
}
```

^23eb42

## 日志Trace

[logback-mdc-ttl/TtlMDCAdapter.java at master · ofpay/logback-mdc-ttl · GitHub](https://github.com/ofpay/logback-mdc-ttl/blob/master/src/main/java/org/slf4j/TtlMDCAdapter.java)
[GitHub - dromara/TLog: Lightweight distributed log label tracking framwork](https://github.com/dromara/TLog)
[TTL 用户反馈收集](https://github.com/alibaba/transmittable-thread-local/issues/73#issuecomment-300665308)

## 参考链接

[使用TransmittableThreadLocal实现异步场景日志链路追踪 - 掘金](https://juejin.cn/post/6981831233911128072)

##### 标签
#java 