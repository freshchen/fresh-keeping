#  Java读源码之ReentrantLock

## 前言

ReentrantLock 可重入锁，应该是除了 synchronized 关键字外用的最多的线程同步手段了，虽然JVM维护者疯狂优化 synchronized 使其已经拥有了很好的性能。但 ReentrantLock 仍有其存在价值，例如可以感知线程中断，公平锁模式，可以指定超时时间的抢锁等更细粒度的控制都是目前的 synchronized 做不到的。

如果不是很了解 Java 中线程的一些基本概念，可以看之前这篇：

[Java读源码之Thread](https://www.cnblogs.com/freshchen/p/11674575.html)

## 案例

用一个最简单的案例引出我们的主角

```java
public class ReentrantLockDemo {

    // 默认是非公平锁和 synchronized 一样
    private static ReentrantLock reentrantLock = new ReentrantLock();

    public void printThreadInfo(int num) {
        reentrantLock.lock();
        try {
            System.out.println(num + " : " + Thread.currentThread().getName());
            System.out.println(num + " : " + Thread.currentThread().toString());
        } finally {
            reentrantLock.unlock();
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        IntStream.rangeClosed(0, 3)
                .forEach(num -> executorService
                        .execute(() -> new ReentrantLockDemo().printThreadInfo(num))
                );
    }

    /**
     * 输出:
     * 0 : pool-1-thread-1
     * 0 : Thread[pool-1-thread-1,5,main]
     * 3 : pool-1-thread-4
     * 3 : Thread[pool-1-thread-4,5,main]
     * 1 : pool-1-thread-2
     * 1 : Thread[pool-1-thread-2,5,main]
     * 2 : pool-1-thread-3
     * 2 : Thread[pool-1-thread-3,5,main]
     */
```

可以看到使用起来也很简单，而且达到了同步的效果。废话不多说一起来瞅一瞅 lock() 和 unlock() 两个同步方法是怎么实现的。

## 源码分析

### 公平锁与非公平锁

公平锁顾名思义。就是每个线程排队抢占锁资源。而非公平锁线程什么时候能执行更多的看缘分，例如一个线程需要执行临界区代码，不管之前有多少线程在等，直接去抢锁，说白了就是插队。对于 ReentrantLock 的实现，从构造器看出，当我们传入 true 代表选择了公平锁模式

```java
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```

为什么先看公平锁实现，而不是默认的非公平锁，因为 synchronized 就是非公平锁，1.7开始 synchronized  的实现改变了，并且基本借鉴了 ReentrantLock 的实现，加入了自旋，偏向锁减少系统调用，所以如果需要非公平锁且不需要特别精细的控制，完全没有必要因为性能选择 ReentrantLock 了。

### AQS 结构

从案例中的 lock 方法进入

- **ReentrantLock.FairSync#lock**

```java
final void lock() {
    // 要一把锁，向谁要锁？
    acquire(1);
}
```


在继续深入之前让我们先熟悉一下 AbstractQueuedSynchronizer（AKA ：AQS） 的结构

- 首先继承了 AbstractOwnableSynchronizer ，主要属性：

```java
// 保存当前持有锁的线程
private transient Thread exclusiveOwnerThread;
```

- AQS 自身主要属性：

```java
// 阻塞队列的头
private transient volatile Node head;

// 阻塞队列的尾
private transient volatile Node tail;

// 同步器的状态
private volatile int state;
```

从 head 和 tail 可以猜想到，AQS 应该是用一个链表作为等待队列，给等待的线程排队， status 字段默认是0，一旦锁被某个线程占有就 +1，那为啥要用int呢？ 如果当前持有锁的这个线程（exclusiveOwnerThread）还要再来把锁，那状态还可以继续 +1，也就实现了可重入。

- 上面的 Node 节点长啥样呢，不要被注释中 CLH 锁高大上的名称吓到，其实就是双向链表，主要属性：

```java
// 标识次节点是共享模式
static final Node SHARED = new Node();
// 标识次节点是独占模式
static final Node EXCLUSIVE = null;
// 节点里装着排队的线程
volatile Thread thread;
// 节点里装的线程放弃了，不抢锁了，可能超时了，可能中断了
static final int CANCELLED =  1;
// 下一个节点里的线程等待被通知出队
static final int SIGNAL    = -1;
// 节点里装的线程在等待执行条件，结合 Condition 使用
static final int CONDITION = -2;
// 节点状态需要被传播到下一个节点，主要用在共享模式
static final int PROPAGATE = -3;
// 标识节点的等待状态，初始0，取值是上面的 -3 ~ 1
volatile int waitStatus;
// 前一个节点
volatile Node prev;
// 后一个节点
volatile Node next;
// 指向下一个等待条件 Condition
Node nextWaiter;
```

去掉一些普通情况不会涉及的属性，如果有四个线程竞争，结构如下图所示：

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/draw/aqs-1-1.png)

可以看到就是一个标准的头节点为空的双链表，为什么头节点是空？

### 公平锁加锁

- **AbstractQueuedSynchronizer#acquire**

```java
public final void acquire(int arg) {
    // 如果尝试拿锁没成功，那就进等待队列
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        // 检测到线程被中断了，因为重置了中断信号但没做处理，再设置下中断位，让用户去处理，中断标准操作
        selfInterrupt();
}

static void selfInterrupt() {
    Thread.currentThread().interrupt();
}
```

- **ReentrantLock.FairSync#tryAcquire**

```java
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    // 取AQS的 state 值
    int c = getState();
    // 当前没有线程持有锁
    if (c == 0) {
        // 如果没有其他线程在排队（公平）
        if (!hasQueuedPredecessors() &&
            // 这里可能存在竞争 CAS 试着去抢一次锁
            compareAndSetState(0, acquires)) {
            // 抢到锁了，把锁持有者改成自己，其他线程往后稍稍
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    // 锁已经被持有了，但如果锁主人就是自己，那欢迎光临（可重入）
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        // 因为其他线程进不来，这里不存在竞争，直接改锁状态
        setState(nextc);
        return true;
    }
    return false;
}
```

- **AbstractQueuedSynchronizer#hasQueuedPredecessors**

```java
// 返回 false 代表不需要排队，true 代表要排队
public final boolean hasQueuedPredecessors() {
    Node t = tail;
    Node h = head;
    Node s;
    // h == t 头等于尾只可能是刚初始的状态或者已经没有节点等待了
    // h.next == null ？ 下面介绍进队的过程中，如果其他线程与此同时 tryAcquire 成功了，会把之前的head.next置为空，说明被捷足先登了，差一点可惜
    // 如果到最后一个判断了，也就是队列中至少有一个等待节点，直接看第一个等待节点是不是自己，如果不是自己就乖乖排队去
    return h != t &&
        ((s = h.next) == null || s.thread != Thread.currentThread());
}
```

- **AbstractQueuedSynchronizer#addWaiter**

tryAcquire如果没有拿到锁，就需要进等待队列了，变成一个 Node 实例

```java
// 这里 mode 为独占模式
private Node addWaiter(Node mode) {
    Node node = new Node(Thread.currentThread(), mode);
    Node pred = tail;
    // 如果尾节点不为空，说明等待队列已经初始化过
    if (pred != null) {
        node.prev = pred;
        // 尝试把自己放到队尾
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
    // 进来这里说明，等待队列没有被初始化过，或者尝试失败了
    enq(node);
    return node;
}
```

- **AbstractQueuedSynchronizer#enq**

```java
private Node enq(final Node node) {
    for (;;) {
        Node t = tail;
        // 如果尾节点是空，说明队列没有初始化
        if (t == null) {
            // 初始化一个空节点（延迟加载），head ，tail都指向它
            if (compareAndSetHead(new Node()))
                tail = head;
        } 
        // 一直尝试把自己塞到队尾（自旋）
        else {
            node.prev = t;
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
}
```

- **AbstractQueuedSynchronizer#acquireQueued**

addWaiter方法已经把等待线程包装成节点放到等待队列了，acquireQueued自旋抢锁，醒了就抢，为啥要返回中断标识呢？主要是为了给一些需要处理中断的方式复用，例如 **ReentrantLock#lockInterruptibly**，以及带超时的锁，以及Condition

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        // 这边逻辑开始绕起来了
        for (;;) {
            // 拿前一个节点
            final Node p = node.predecessor();
            // 前一个节点是head，说明自己排在第一个
            if (p == head && 
                // 在让出cpu前再试一次，此时可能锁持有者已经让位了
                tryAcquire(arg)) {
                // 抢到锁了
                setHead(node);
                // 把之前没用的头节点释放
                p.next = null; // help GC
                failed = false;
                return interrupted;
            }
            // 两次尝试都失败了，只能准备被挂起,让出cpu了（调了内核，重量级）
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        // 普通的锁不处理中断异常，不会进这个方法
        if (failed)
            cancelAcquire(node);
    }
}

private void setHead(Node node) {
    // 把头节点设为自己
    head = node;
    // 因为已经抢到锁了，不需要记录这个线程在等待了，保持了头节点中线程永远为 null
    node.thread = null;
    node.prev = null;
}
```

- **AbstractQueuedSynchronizer#shouldParkAfterFailedAcquire**

```java
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    if (ws == Node.SIGNAL)
        // 已经告诉前一个节点自己需要被通知了
        return true;
    if (ws > 0) {
        // 只有 CANCELLED 这个状态大于0，如果前面的节点不排队了，就一直找到一个没 CANCELLED 的
        do {
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        pred.next = node;
    } 
    // 进到这里，只剩下PROPAGATE（共享锁时候才有） CONDITION（本文不涉及） 和 未赋值状态也就是0， 
    else {
        // 这里把 默认状态0 置为 -1，也就代表着后面有线程在等着被唤醒了
        compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    // 返回false，就暂时不会让线程挂起，继续自旋，直到返回true
    return false;
}
```

- **AbstractQueuedSynchronizer#parkAndCheckInterrupt**

```java
private final boolean parkAndCheckInterrupt() {
    // 挂起，标准用法this充当blocker 
    LockSupport.park(this);
    // 一旦恢复，返回线程在挂起阶段是否被中断，此方法会重置中断位
    return Thread.interrupted();
}
```

到这里加锁流程就介绍差不多了，用一个最简单流程的图来总结一下：

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/draw/aqs-1-2.png)



### 公平锁解锁

- **AbstractQueuedSynchronizer#release**

```java
public final boolean release(int arg) {
    // 尝试释放锁
    if (tryRelease(arg)) {
        Node h = head;
        // 如果等待队列已经被初始化过，并且后面有节点等待操作
        if (h != null && h.waitStatus != 0)
            // 恢复挂起的线程
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

- **ReentrantLock.FairSync#tryRelease**

```java
protected final boolean tryRelease(int releases) {
    int c = getState() - releases;
    // 能执行释放锁的肯定是锁的持有者，除非虚拟机魔怔了
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    boolean free = false;
    // 考虑可重入
    if (c == 0) {
        free = true;
        // 锁现在没有持有者了
        setExclusiveOwnerThread(null);
    }
    setState(c);
    return free;
}
```

- **ReentrantLock.FairSync#unparkSuccessor**

```java
// node 是头节点
private void unparkSuccessor(Node node) {
    int ws = node.waitStatus;
    // 如果状态不是 CANCELED，就把状态置为初始状态 
    if (ws < 0)
        compareAndSetWaitStatus(node, ws, 0);

    Node s = node.next;
    // s == null 这个条件成立主要是在共享模式下自旋释放。
    if (s == null || s.waitStatus > 0) {
        // 把 CANCELED 状态的节点置为空
        s = null;
        // 因为 head 这条路已经断了，从尾巴开始找到第一个排队的节点，然后把队列接上
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    if (s != null)
        // 把第一个排队的节点中的线程唤醒，
        LockSupport.unpark(s.thread);
}
```

线程从加锁代码里介绍的 **AbstractQueuedSynchronizer#parkAndCheckInterrupt** 方法中醒来，继续自旋拿锁。如果此时后面还有人排队就一定能拿到锁了。如图所示：

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/draw/aqs-1-3.png)



### 非公平锁加锁

- **ReentrantLock.NonfairSync#lock**

```java
final void lock() {
    // 不管三七二十一，直接抢锁，如果运气好，锁正好被释放了，就不排队了
    if (compareAndSetState(0, 1))
        setExclusiveOwnerThread(Thread.currentThread());
    else
        // 和上面介绍的公平锁一样，只是 tryAcquire 实现不一样
        acquire(1);
}
```

- **ReentrantLock.Sync#nonfairTryAcquire**

上面公平锁我们已经知道，线程真正挂起前会尝试两次，由于不考虑别人有没有入队，实现非常简单

```java
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    // 如果没有线程持有锁，直接抢锁
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    // 如果是重入，状态累加
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
```

### 非公平锁解锁

因为都是独占锁模式，解锁和公平锁逻辑一样。

## 总结

至此，总算看完了 ReentrantLock 常规的加锁解锁源码，好好体会下 AQS 的结构，还是能看懂的，且很有收获，总之 Doug Lea 大神牛B。**AbstractQueuedSynchronizer#acquireQueued**

本文还是挖了很多坑的：

- 带超时的锁是如何实现的？
- 检测中断的锁是如何实现的？
- 各种 Condition 是如何实现的？

以后有时间再一探究竟吧。







