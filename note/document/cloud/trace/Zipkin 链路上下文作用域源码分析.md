---
begin: 2022-02-06
status: ongoing
rating: 1
---

# Zipkin 链路上下文作用域源码分析

## 简介

上文 [Zipkin 链路上下文源码分析](https://juejin.cn/post/7061505594045235214/) 介绍了链路传播的信息都是从链路上下文中提取再注入到链路上下文中的，三种链路上下文的实现中 TraceContext 是链路信息最为完整的。 

## 问题
下面思考一个问题，如果请求初次到达后生成了一个 TraceContext 称为 TC1，完成这次请求需要对系统 B 发起一次远程调用，需要创建一个新的跨度（Span）记录此次调用，创建一个新的跨度需要先前是不是已经开始追踪了，换句话说是不是已经在链路追踪的作用域中了，如果在作用域中那我们需要取得先前的链路上下文也就是 TC1 中的 traceId 从而保持统一，取得 TC1 中的 spanId 作为新跨度的 parentId 从而建立层级关系。

也就是说我们需要知道先前的跨度信息，这个问题是看似很好解决，只需要将 TraceContext 作为方法参数传递下去即可，这个方法确实可以完美达到目的，但却不可避免造成大量业务代码入侵，并且作为公共基础的能力过分依赖业务方是很难推进的。另外一个办法就是将 TraceContext 保存在 ThreadLocal 也就是线程局部变量中，没错这是个好办法，Zipkin 也默认是这样做的。

继续思考，如果我们生成了一个 TraceContext 称为 TC1 记录在 ThreadLocal，此时发起远程调用，使用 ThreadLocal 中的 TC1 生成新的 TraceContext 称为 TC2，此时我们需要将 TC2 放到 ThreadLocal 中覆盖 TC1 么？

答案是要的，假如每次服务调用前还会调用一次统一认证鉴权服务，那么这次鉴权的调用就需要在 TC2 的基础上生成 TC3，所以我们可以发现  TraceContext 虽然可以使用 ThreadLocal 存储，但其生命周期和当前线程是不一致的，因此 Zipkin 在实现过程中还引入了 Scope 上下文的概念，明确了 TraceContext 的作用范围为一次调用。

拿 Zipkin 官网的案例举例，直观的看到在一个作用范围也就是在同一层，换句话说作用范围就是从一次调用开始到一次调用结束。

![](image/Untitled%20Diagram.drawio.png)


总结：ThreadLocal 可以帮助我们完成透明传递，但 TraceContext 和 ThreadLocal 生命周期是不一致的。且引入了 ThreadLocal 就不可避免要处理跨线程传递，以及线程池线程复用等情况。下面让我们看看上述问题的具体工程实现。

# 源码分析

> spring-boot：2.5.6
> spring-cloud：2020.0.4
> spring-cloud-sleuth：3.0.4
> zipkin-brave：5.13.2

## 1 CurrentTraceContext
CurrentTraceContext 用于存放 TraceContext，是一个抽象类，提供 SPI 可插拔机制。只有一个属性
`final ScopeDecorator[] scopeDecorators;`
单纯看这个属性不是很明白，让我们先看 CurrentTraceContext 中重要的两个内部类

### 1.1 Scope

```java
  /**
   * Scope 就是上文中提到链路上下文作用域的抽象, 巧妙的将一次方法调用的作用域看作是一个资源，并实现了 Closeable 从而可以使用 try-with-resources 的方式进行调用
   */
  public interface Scope extends Closeable {
    // 提供了一个关闭资源默认什么都不做的实现
    Scope NOOP = new Scope() {
      @Override public void close() {
      }

      @Override public String toString() {
        return "NoopScope";
      }
    };

    // 一次调用结束会触发 close 清理资源，因为链路追踪不能影响业务，因此这里重新 close 方法改为不抛出异常
    @Override void close();
  }
```

### 1.2 ScopeDecorator

```java
  /**
   * 用于在调用前做一些增强
   */
  public interface ScopeDecorator {

    // 提供了一个默认什么都不做的实现
    ScopeDecorator NOOP = new ScopeDecorator() {
      @Override public Scope decorateScope(TraceContext context, Scope scope) {
        return scope;
      }

      @Override public String toString() {
        return "NoopScopeDecorator";
      }
    };

    /**
     * @param context 如果是 null 就清理 scope
     */
    Scope decorateScope(@Nullable TraceContext context, Scope scope);
  }
```

综上 ScopeDecorator 是作用域开始前的扩展，Scope 是作用域抽象并提供关闭前清理资源的方法。下面我们再看下 CurrentTraceContext 的其他方法

### 1.3 核心方法

```java
// 获取上下文,可能空  
public abstract @Nullable TraceContext get();

// 创建上下文作用域  
public abstract Scope newScope(@Nullable TraceContext context);

// 执行所有作用域装饰器  
protected Scope decorateScope(@Nullable TraceContext context, Scope scope) {  
 for (ScopeDecorator scopeDecorator : scopeDecorators) {  
 scope = scopeDecorator.decorateScope(context, scope);  
 }  
 return scope;  
}

// 主要处理回调，回调可能是同一个上下文需要特殊处理  
public Scope maybeScope(@Nullable TraceContext context) {  
 TraceContext current = get();  
 if (equals(current, context)) return decorateScope(context, Scope.NOOP);  
 return newScope(context);  
}


```

### 1.4 跨线程

brave 提供了简单的跨线程作用域使用方式如下

```java
 // 通过包装 Runnable 将异步调用放入作用域，实现较为简单，且 sleuth 处理时并没有使用
  public Runnable wrap(Runnable task) {
    // 获取当前上下文
    final TraceContext invocationContext = get();
    class CurrentTraceContextRunnable implements Runnable {
      @Override public void run() {
        // maybeScope 将 TraceContext 放入 Scope，并触发所有 ScopeDecorator，task.run 执行完后自动触发 Scope.close 方法
        try (Scope scope = maybeScope(invocationContext)) {
          task.run();
        }
      }
    }
    return new CurrentTraceContextRunnable();
  }


  // 包装线程池，实现较为简单, 且 sleuth 处理时并没有使用
  public Executor executor(Executor delegate) {
    class CurrentTraceContextExecutor implements Executor {
      @Override public void execute(Runnable task) {
        delegate.execute(CurrentTraceContext.this.wrap(task));
      }
    }
    return new CurrentTraceContextExecutor();
  }
```

### 1.5 默认实现类

CurrentTraceContext 中有一个默认实现类 Default，

## 参考链接


##### 标签
