# Java读源码之CountDownLatch

## 前言

相信大家都挺熟悉 CountDownLatch 的，顾名思义就是一个栅栏，其主要作用是多线程环境下，让多个线程在栅栏门口等待，所有线程到齐后，栅栏打开程序继续执行。

## 案例

用一个最简单的案例引出我们的主角

```java
public class CountDownLatchDemo {

    public void run(CountDownLatch countDownLatch) {
        System.out.println(Thread.currentThread().getName() + "就位");
        countDownLatch.countDown();
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatchDemo countDownLatchDemo = new CountDownLatchDemo();
        CountDownLatch countDownLatch = new CountDownLatch(5);
        IntStream.rangeClosed(0, 4)
                .forEach(num -> executorService
                        .execute(() -> countDownLatchDemo.run(countDownLatch))
                );

        countDownLatch.await();
        System.out.println("已到齐");
    }

    /**
     * 输出：
     * pool-1-thread-2就位
     * pool-1-thread-5就位
     * pool-1-thread-4就位
     * pool-1-thread-3就位
     * pool-1-thread-1就位
     * 已到齐
     */
}
```

## 源码分析

看源码前最好先熟悉下 AQS 的大致结构，之前有两篇文章仅供参考，大致熟悉下即可

[Java读源码之ReentrantLock](https://www.cnblogs.com/freshchen/p/12655320.html)

[Java读源码之ReentrantLock(2)](https://www.cnblogs.com/freshchen/p/12733690.html)

在看 AQS 的 Node 节点的时候看到有共享模式和独占模式，ReentrantLock 用了独占模式，CountDownLatch 正式用了共享模式，相信看完能够对 AQS 有更深的理解。

### 初始化

- **CountDownLatch#CountDownLatch**

```java
public CountDownLatch(int count) {
  if (count < 0) throw new IllegalArgumentException("count < 0");
  // 可以看到 CountDownLatc 内部也实现了一个 AQS
  this.sync = new Sync(count);
}
```

- **CountDownLatch.Sync#Sync**

```java
Sync(int count) {
  // 直接拿了 count 把锁（当前线程可重入 Sync 锁 count 次）
  setState(count);
}
```

### 等待

- **CountDownLatch#await**

```java
public void await() throws InterruptedException {
  sync.acquireSharedInterruptibly(1);
}
```

- **AbstractQueuedSynchronizer#acquireSharedInterruptibly**

```java
public final void acquireSharedInterruptibly(int arg)
  throws InterruptedException {
  // 注意，持有锁的线程被中断是直接抛异常的 
  if (Thread.interrupted())
    throw new InterruptedException();
  // tryAcquireShared很简单，如果全员到齐了返回1，其他时候都返回 -1
  if (tryAcquireShared(arg) < 0)
    // 所以没到齐前都会以共享模式入同步队列
    doAcquireSharedInterruptibly(arg);
}
```

- **AbstractQueuedSynchronizer#doAcquireSharedInterruptibly**

```java
private void doAcquireSharedInterruptibly(int arg)
  throws InterruptedException {
  //	addWaiter之前看过，作用就是把节点放到同步队列的末尾，但是这里节点类型是共享模式
  //  值得注意的是，模式是存在节点的 nextWaiter 中，所以不管 nextWaiter 可能三种情况 1。独占模式的空节点	2.共享模式的空节点	3。Condition条件队列的下一个节点
  final Node node = addWaiter(Node.SHARED);
  boolean failed = true;
  // 下面的自旋和 acquireQueued 方法基本一模一样，重点看下区别
  try {
    for (;;) {
      final Node p = node.predecessor();
      // 如果前驱节点是 head 说明没人排队
      if (p == head) {
        // 再次尝试
        int r = tryAcquireShared(arg);
        // 只有调用了足够次数countDown，栅栏才会打开
        if (r >= 0) {
          // 这里的 r 一定为 1，会一个一个唤醒所有节点
          setHeadAndPropagate(node, r);
          p.next = null; // help GC
          failed = false;
          return;
        }
      }
      // 一般入队肯定有人排队的，之前也看过，主要作用，通知前驱节点，然后挂起
      if (shouldParkAfterFailedAcquire(p, node) &&
          parkAndCheckInterrupt())
        throw new InterruptedException();
    }
  } finally {
    if (failed)
      cancelAcquire(node);
  }
}
```

- **AbstractQueuedSynchronizer#setHeadAndPropagate**

```java
private void setHeadAndPropagate(Node node, int propagate) {
  Node h = head;
  setHead(node);
  // propagate = 1 次判断一定为 true
  if (propagate > 0 || h == null || h.waitStatus < 0 ||
      (h = head) == null || h.waitStatus < 0) {
    Node s = node.next;
    // CountDownLatch节点所有节点都是共享模式，一定满足
    if (s == null || s.isShared())
      // 直接唤醒下一个
      doReleaseShared();
  }
}
```

- **AbstractQueuedSynchronizer#doReleaseShared**

```java
private void doReleaseShared() {
  for (;;) {
    Node h = head;
    // 同步队列不为空则进入
    if (h != null && h != tail) {
      int ws = h.waitStatus;
      // 如果有节点需要被唤醒
      if (ws == Node.SIGNAL) {
        // 不断重试 CAS 吧 头节点设为全新
        if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
          continue;
        // 唤醒下一个节点
        unparkSuccessor(h);
      }
      // 到这里 下一个已经唤醒了，把节点状态设置为 PROPAGATE，说明是共享状态唤醒的
      else if (ws == 0 &&
               !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
        continue;
    }
    // 直到头节点变化结束，也就是下一个一个被唤醒了，然后再由下一个接着唤醒
    if (h == head)
      break;
  }
}
```

### 签到

看等待过程，栅栏打开后，所有共享模式的节点会一个一个的唤醒，让我们一起看看如何打开栅栏并唤醒第一个节点。

- **AbstractQueuedSynchronizer#releaseShared**

```java
public final boolean releaseShared(int arg) {
  if (tryReleaseShared(arg)) {
    // 这个方法和等待时候自悬的一样，用于唤醒第一个节点
    doReleaseShared();
    return true;
  }
  return false;
}
```

- **CountDownLatch#tryReleaseShared**

```java
protected boolean tryReleaseShared(int releases) {
  for (;;) {
    int c = getState();
    if (c == 0)
      // c == 0 说明栅栏已经打开过了，CountDownLatch 是一次性的，直接false
      return false;
    int nextc = c-1;
    if (compareAndSetState(c, nextc))
      // cas 递减状态，达到0的时候返回 true 栅栏打开
      return nextc == 0;
  }
}
```

