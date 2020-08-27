# Java读源码之LockSupport

## 前言

> JDK版本: 1.8 

### 作用

LockSupport类主要提供了park和unpark两个native方法，用于阻塞和唤醒线程。注释中有这么一段：

**这个类是为拥有更高级别抽象的并发类服务的，开发中我们不会用到这个类**

既然只是native方法，开发中也用不到，那么还有必要去看么？

了解LockSupport可以帮助我们更好理解并发，而且大家熟悉的并发中最核心的AQS类中也大量的使用了LockSupport，所以还是有必要看一看的，至少熟悉其中的概念。

### 为什么需要LockSupport

已经知道了这个类就是阻塞唤醒，Object.wait和Object.notify，Thread.suspend和Thread.resume这两对方法也是类似效果，那么还有必要去看么？？？

#### Thread.suspend和Thread.resume为什么被弃用

- suspend将线程挂起，从运行状态阻塞状态，但是并不释放所占用的锁
- suspend方法至少已经满足互斥，不可剥夺两个死锁的条件了
- resume将线程解除挂起，从阻塞状态到运行状态，通常是等待其他任务完成， 请求与保持条件也成立了
- 最后只差 循环等待条件 就死锁了，这实在太危险了，一不小心就容易死锁，而且死锁的问题是很难排查的

#### Object.wait和Object.notify存在什么问题

- 不满足条件时我们需要在代码中保证拿到锁才能调用，把线程放到等待队列中
- notify是从等待池中随机放一个线程出来，当需要唤醒特定线程时，只能notifyAll

LockSupport会有上面的问题么，又有哪些特点呢，让我们进入源码

## 源码

### 类声明和属性

```java
package java.util.concurrent.locks;

public class LockSupport {
    // 工具类，ban掉构造
    private LockSupport() {}
    
    private static final sun.misc.Unsafe UNSAFE;
    // parkBlocker的内存偏移量
    private static final long parkBlockerOffset;
    private static final long SEED;
    private static final long PROBE;
    private static final long SECONDARY;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> tk = Thread.class;
            // 反射拿到Thread类中的parkBlocker属性，然后获取其在内存中的偏移量
            parkBlockerOffset = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("parkBlocker"));
            SEED = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSeed"));
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
            SECONDARY = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSecondarySeed"));
        } catch (Exception ex) { throw new Error(ex); }
    }

}
```

### park()

```java
// 最简单的方式，但是不推荐
public static void park() {
    /**
     * 将当前线程挂起，是通过二元信号量，获取许可证实现的，拿到许可证后才执行挂起
     * 不是基于对象的监视器锁，所以不需要显示的同步
     * 如果超时了，被中断了或者unpark了就会return并且释放许可证
     * 需要注意的是和wait一样也会因为JVM内部未知原因return，所以我们如果使用也需要放在循环内
     * 第一个参数 flase代表纳秒级别超时控制，此级别下第二个参数timeout为0代表无限等待
     * 第一个参数 true代表毫秒级别超时控制，此级别下第二个参数timeout为0会立即返回
     */ 
    UNSAFE.park(false, 0L);
}

// 推荐方式，blocker是个辅助对象，用于跟踪许可证的获取，以及定位一些阻塞问题，一般情况park(this)就行
public static void park(Object blocker) {
    Thread t = Thread.currentThread();
    // 标记对于当前线程t，blocker正在获取许可证，出问题通过getBlocker方法去定位
    setBlocker(t, blocker);
    UNSAFE.park(false, 0L);
    // park操作return了，标记许可证已经释放
    setBlocker(t, null);
}

private static void setBlocker(Thread t, Object arg) {
    // 通过偏移量，把给当前线程t的parkBlocker属性赋值为arg
    UNSAFE.putObject(t, parkBlockerOffset, arg);
}
```

相信大家已经基本了解park操作了，LockSupport还给我们提供了其他功能

```java
// 推荐，纳秒级别timeout后return
public static void parkNanos(Object blocker, long nanos) {
    if (nanos > 0) {
        Thread t = Thread.currentThread();
        setBlocker(t, blocker);
        UNSAFE.park(false, nanos);
        setBlocker(t, null);
    }
}

// 推荐，毫秒级别timeout后return
public static void parkUntil(Object blocker, long deadline) {
    Thread t = Thread.currentThread();
    setBlocker(t, blocker);
    UNSAFE.park(true, deadline);
    setBlocker(t, null);
}

// 不推荐，纳秒级别timeout后return
public static void parkNanos(long nanos) {
    if (nanos > 0)
        UNSAFE.park(false, nanos);
}

// 不推荐，毫秒级别timeout后return
public static void parkUntil(long deadline) {
    UNSAFE.park(true, deadline);
}
```

### unpark

```java
public static void unpark(Thread thread) {
    if (thread != null)     
        //释放线程thread的许可证，如果已经是释放状态那就什么都不会发生，因为总共就1个许可，所以unpark可以先于park执行没有任务问题
        UNSAFE.unpark(thread);
}
```

### 其他方法

```java
// 由于包权限问题从ThreadLocalRandom类中copy过来的，用于生成随机数种子
static final int nextSecondarySeed() {
    int r;
    Thread t = Thread.currentThread();
    if ((r = UNSAFE.getInt(t, SECONDARY)) != 0) {
        r ^= r << 13;   // xorshift
        r ^= r >>> 17;
        r ^= r << 5;
    }
    else if ((r = java.util.concurrent.ThreadLocalRandom.current().nextInt()) == 0)
        r = 1; // avoid zero
    UNSAFE.putInt(t, SECONDARY, r);
    return r;
}
```

## 实践

```java
public class LockSupportTest {
    
    public static void main(String[] args) {
        AtomicBoolean flag = new AtomicBoolean(true);
        Thread thread = new Thread(() -> {
            Thread curr = Thread.currentThread();
            System.out.println("线程1 即将被阻塞");
            while (flag.get()) {
                LockSupport.park(curr);
                System.out.println("线程1 复活");
            }
            System.out.println("线程1 结束使命");
        });
        thread.start();

        new Thread(() -> {
            System.out.println("唤醒线程1");
            flag.compareAndSet(true, false);
            LockSupport.unpark(thread);
        }).start();
    }
    
    /**
     * 输出：
     * 线程1 即将被阻塞
     * 唤醒线程1
     * 线程1 复活
     * 线程1 结束使命
     */
}
```

