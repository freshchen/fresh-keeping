# Java读源码之Thread

## 前言

> JDK版本：1.8

阅读了Object的源码，wait和notify方法与线程联系紧密，而且多线程已经是必备知识，那保持习惯，就从多线程的源头Thread类开始读起吧。由于该类比较长，只读重要部分

## 源码

### 类声明和重要属性

```java
package java.lang;

public class Thread implements Runnable {

    private volatile String name;
    // 优先级
    private int            priority;
    //是否后台
    private boolean     daemon = false;
    /* JVM state */
    private boolean     stillborn = false;
    // 要跑的任务
    private Runnable target;
    // 线程组
    private ThreadGroup group;
    // 上下文加载器
    private ClassLoader contextClassLoader;
    // 权限控制上下文
    private AccessControlContext inheritedAccessControlContext;
    // 线程默认名字“Thread-{{ threadInitNumber }}”
    private static int threadInitNumber;
    // 局部变量，每个线程拥有各自独立的副本
    ThreadLocal.ThreadLocalMap threadLocals = null;
    // 有时候局部变量需要被子线程继承
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
    // 线程初始化时申请的JVM栈大小
    private long stackSize;
    // 线程ID
    private long tid;
    // 线程init之后的ID
    private static long threadSeqNumber;
    // 0就是线程还处于NEW状态，没start
    private volatile int threadStatus = 0;
    // 给LockSupport.park用的需要竞争的对象
    volatile Object parkBlocker;
    // 给中断用的需要竞争的对象
    private volatile Interruptible blocker;
    // 线程最小优先级
    public final static int MIN_PRIORITY = 1;
    // 线程默认优先级
    public final static int NORM_PRIORITY = 5;
    // 线程最大优先级
    public final static int MAX_PRIORITY = 10;
```

### Java线程有几种状态？

```java
// Thread类中的枚举
public enum State {
    // 线程刚创建出来还没start
    NEW,
    // 线程在JVM中运行了，需要去竞争资源，例如CPU
    RUNNABLE,
    // 线程等待获取对象监视器锁，损被别人拿着就阻塞
    BLOCKED,
    // 线程进入等待池了，等待觉醒
    WAITING,
    // 指定了超时时间
    TIMED_WAITING,
    // 线程终止
    TERMINATED;
}
```

下面这个图可以帮助理解Java线程的生命周期，**这个图要会画**！面试中被问到，当时画的很不专业，难受！

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/thread-status.jpg)

### 创建

那么线程如何进入初始New状态呢？让我们来看看构造，头皮发麻，怎么有七八个构造，这里只贴了一个

```java
public Thread() {
    init(null, null, "Thread-" + nextThreadNum(), 0);
}
```

还好都是调用init()方法，怕怕的点开了

```java
private void init(ThreadGroup g, Runnable target, String name,
                  long stackSize, AccessControlContext acc,
                  boolean inheritThreadLocals) {
    if (name == null) {
        throw new NullPointerException("name cannot be null");
    }

    this.name = name;
	// 获取当前线程，也就是需要被创建线程的爸爸
    Thread parent = currentThread();
    SecurityManager security = System.getSecurityManager();
    if (g == null) {
        // 通过security获取线程组，其实就是拿的当前线程的组
        if (security != null) {
            g = security.getThreadGroup();
        }

        // 获取当前线程的组，这下确保肯定有线程组了
        if (g == null) {
            g = parent.getThreadGroup();
        }
    }

    // check一下组是否存在和是否有线程组修改权限
    g.checkAccess();

    // 子类执行权限检查，子类不能重写一些不是final的敏感方法
    if (security != null) {
        if (isCCLOverridden(getClass())) {
            security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
        }
    }
	// 组里未启动的线程数加1，长时间不启动就会被回收
    g.addUnstarted();
	// 线程的组，是否后台，优先级，初始全和当前线程一样
    this.group = g;
    this.daemon = parent.isDaemon();
    this.priority = parent.getPriority();
    if (security == null || isCCLOverridden(parent.getClass()))
        // 子类重写check没过或者就没有security，这里要check下是不是连装载的权限都没有
        this.contextClassLoader = parent.getContextClassLoader();
    else
        this.contextClassLoader = parent.contextClassLoader;
    // 访问控制上下文初始化
    this.inheritedAccessControlContext =
        acc != null ? acc : AccessController.getContext();
    // 任务初始化
    this.target = target;
    // 设置权限
    setPriority(priority);
    // 如果有需要继承的ThreadLocal局部变量就copy一下
    if (inheritThreadLocals && parent.inheritableThreadLocals != null)
        this.inheritableThreadLocals =
        ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
    // 初始化JVM中待创建线程的栈大小
    this.stackSize = stackSize;

    // threadSeqNumber线程号加1
    tid = nextThreadID();
}
```

### 运行

现在线程已经是NEW状态了，我们还需要调用start方法，让线程进入RUNNABLE状态，真正在JVM中快乐的跑起来，当获得了执行任务所需要的资源后，JVM便会调用target（Runnable）的run方法。

**注意：我们永远不要对同一个线程对象执行两次start方法**

```java
public synchronized void start() {
    // 0就是NEW状态
    if (threadStatus != 0)
        throw new IllegalThreadStateException();

    // 把当前线程加到线程组的线程数组中，然后nthreads线程数加1，nUnstartedThreads没起的线程数减1
    group.add(this);

    boolean started = false;
    // 请求资源
    try {
        start0();
        started = true;
    } finally {
        try {
            if (!started) {
    // 起失败啦，把当前线程从线程组的线程数组中删除，然后nthreads减1，nUnstartedThreads加1
                group.threadStartFailed(this);
            }
        } catch (Throwable ignore) {
            // start0出问题会自己打印堆栈信息
        }
    }
}

private native void start0();
```

### 终止

现在我们的线程已经到RUNNABLE状态了，一切顺利的话任务执行完成，自动进入TERMINATED状态，天有不测风云，我们还会再各个状态因为异常到达TERMINATED状态。

Thread类为我们提供了interrupt方法，可以设置中断标志位，设置了中断之后不一定有影响，还需要满足一定的条件才能发挥作用：

- **RUNNABLE**状态下
  - 默认什么都不会发生，需要代码中循环检查 中断标志位
- **WAITING**/**TIMED_WAITING**状态下
  - 这两个状态下，会从对象等待池中出来，等拿到监视器锁会抛出**InterruptedException**异常，然后中断标志位被清空。
- **BLOCKED**状态下
  - 如果线程在等待锁，对线程对象调用interrupt()只是会设置线程的中断标志位，**线程依然会处于BLOCKED状态**
- **NEW**/**TERMINATE**状态下
  - 啥也不发生

```java
// 设置别的线程中断
public void interrupt() {
    if (this != Thread.currentThread())
        checkAccess();
	// 拿一个可中断对象Interruptible的锁
    synchronized (blockerLock) {
        Interruptible b = blocker;
        if (b != null) {
            interrupt0();           // 设置中断标志位
            b.interrupt(this);
            return;
        }
    }
    interrupt0();
}

// 获取当前线程中断标志位，然后重置中断标志位
public static boolean interrupted() {
    return currentThread().isInterrupted(true);
}

// 检查线程中断标志位
public boolean isInterrupted() {
    return isInterrupted(false);
}
```

### 等待

主线已经做完了，下面来看下支线任务，同样重要哦。从线程状态图看到，RUNNABLE状态可以变成BLOCKED，WAITING或TIMED_WAITING。

其中BLOCKED主要是同步方法竞争锁等同步资源造成的，而TIMED_WAITING主要是加了超时时间，其他和WAITING的内容差不多，唯一多了一个sleep方法。

#### sleep

果不其然，sleep方法和Object.wait方法如出一辙，都是调用本地方法，提供毫秒和纳秒两种级别的控制，唯一区别就是，**sleep不会放弃任何占用的监视器锁**

```java
public static native void sleep(long millis) throws InterruptedException;

// 纳秒级别控制
public static void sleep(long millis, int nanos) throws InterruptedException {
    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (nanos < 0 || nanos > 999999) {
        throw new IllegalArgumentException(
            "nanosecond timeout value out of range");
    }

    if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
        millis++;
    }

    sleep(millis);
}
```

#### join

join方法会让线程进入WAITING，等待另一个线程的终止，整个方法和Object.wait方法也是很像，而且实现中也用到了wait

```java
public final synchronized void join(long millis) throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (millis == 0) {
        // 判断调用join的线程是否活着，这里的活着是指RUNNABLE,BLOCKED,WAITING,TIMED_WAITING这四种状态，如果活着就一直等着，wait(0)意味着无限等
        while (isAlive()) {
            wait(0);
        }
    } else {
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}

// 纳秒级别控制
public final synchronized void join(long millis, int nanos)
    throws InterruptedException {

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (nanos < 0 || nanos > 999999) {
        throw new IllegalArgumentException(
            "nanosecond timeout value out of range");
    }

    if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
        millis++;
    }

    join(millis);
}

public final void join() throws InterruptedException {
    join(0);
}
```

### 其他方法

#### yield

告诉操作系统的调度器：我的cpu可以先让给其他线程,但是我占有的同步资源不让。

**注意，调度器可以不理会这个信息**。这个方法几乎没用，调试并发bug可能能派上用场

```java
public static native void yield();
```

#### setPriority

有些场景是需要根据线程的优先级来调度的，优先级越大越先执行，最大10，默认5，最小1

```java
public final void setPriority(int newPriority) {
    ThreadGroup g;
    checkAccess();
    if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
        throw new IllegalArgumentException();
    }
    if((g = getThreadGroup()) != null) {
        // 如果设置的优先级，比线程所属线程组中优先级的最大值还大，我们需要更新最大值
        if (newPriority > g.getMaxPriority()) {
            newPriority = g.getMaxPriority();
        }
        // 本地方法
        setPriority0(priority = newPriority);
    }
}
```

### 弃用方法

有些熟悉的方法已经被弃用了，我们要避免使用

```java
@Deprecated
public final void stop()
@Deprecated
public final synchronized void stop(Throwable obj)
@Deprecated
public void destroy()
@Deprecated
public final void suspend()
@Deprecated
public final void resume()
@Deprecated
public native int countStackFrames()
```

## 实践

#### interrupt()

```java
public class ThreadInterruptTest {

    /**
     * 如果我们同时调用了notify和interrupt方法，程序有可能正常执行结束，有可能抛出异常结束,
     * 原因是不管是因为notify还是interrupt,线程离开了等待池，都需要去竞争锁,
     * 如果interrupt调用瞬间拿到锁，notify还没有调用，就抛中断异常
     * 如果是interrupt调用瞬间拿不到锁，此时中断标志位被重置，然后notify把线程拉到正常轨道，就继续执行不抛中断异常
     */
    private static void testInterrupt() {
        Object object = new Object();
        Thread thread1 = new Thread(() -> {
            synchronized (object) {
                try {
                    object.wait();
                    System.out.println("我还活着！");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

            }

        });

        thread1.start();

        new Thread(() -> {
            // 只为了演示，实际很少用到这些方法，而且我们在执行中断的同步代码块中最好不要做别的事情，例如这里的notify
            synchronized (object) {
                thread1.interrupt();
                object.notify();
            }
        }).start();
    }

    public static void main(String[] args) {
        for (int i = 0; i <5 ; i++) {
            ThreadInterruptTest.testInterrupt();
        }

    }
}
/**
 * 输出：
 * 我还活着！
 * java.lang.InterruptedException
 * 	at java.lang.Object.wait(Native Method)
 * 	at java.lang.Object.wait(Object.java:502)
 * 	at study.ThreadInterruptTest.lambda$testInterrupt$0(ThreadInterruptTest.java:15)
 * 	at java.lang.Thread.run(Thread.java:748)
 * java.lang.InterruptedException
 * 	at java.lang.Object.wait(Native Method)
 * 	at java.lang.Object.wait(Object.java:502)
 * 	at study.ThreadInterruptTest.lambda$testInterrupt$0(ThreadInterruptTest.java:15)
 * 	at java.lang.Thread.run(Thread.java:748)
 * 我还活着！
 * java.lang.InterruptedException
 * 	at java.lang.Object.wait(Native Method)
 * 	at java.lang.Object.wait(Object.java:502)
 * 	at study.ThreadInterruptTest.lambda$testInterrupt$0(ThreadInterruptTest.java:15)
 * 	at java.lang.Thread.run(Thread.java:748)
 *
 */
```

#### join()

```java
public class ThreadJoinTest {
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            System.out.println("你好");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("你更好！");
        });

        thread1.start();

        new Thread(() -> {
            System.out.println("你也好");
            try {
                thread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("你最好！！");
        }).start();
    }

    /**
     * 输出：
     * 你好
     * 你也好
     * 你更好！
     * 你最好！！
     */
}
```

