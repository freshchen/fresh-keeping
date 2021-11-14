---
begin: 2021-11-14
status: done
rating: 1
---

# Object源码

## 前言

> JDK版本: 1.8

## 源码

```java
package java.lang;

/**
 * Java中的始祖，万物皆Object
 * @since   JDK1.0
 */
public class Object {

    private static native void registerNatives();
    static {
        // 保证在clinit()最先执行，从而调native方法
        registerNatives();
    }

    /**
     * 返回运行时的Class类文件，返回的是个泛型，运行时泛型会进行类型擦除，实际返回以自身为边界
     * 例如 new HelloWorld().getClass()
     * 返回 Class<? extends HelloWorld>
     */
    public final native Class<?> getClass();

    /**
     * 返回一个hash code整数，主要用于集合类使用，例如HashMap
     * 多次调用返回值必须一样，但是同一个应用执行了多份的时候，不保证多个执行中值相等
     * 如果两个对象调equels方法返回ture，那么hash code值一定相等（所以重写equels方法一定要重写hashCode方法）
     * 如果两个对象调equels方法返回false，hash code值不一定不相等，但是我们重写hashCode方法时，应该尽量做到不等，从而减少hash碰撞
     * 不同虚拟机的实现不太一样，但是主要是根据对象的内存地址来计算的
     */
    public native int hashCode();

    /**
     * 对于非空的对象进行逻辑上是否相等的比较，默认是比较的内存地址
     * 再次强调哈哈，如果两个对象调equels方法返回ture，那么hash code值一定相等（所以重写equels方法一定要重写hashCode方法）
     * 四大特性（1）reflexive自反性（2）symmetric对称性（3）transitive传递性（4）consistent一致性
     */
    public boolean equals(Object obj) {
        return (this == obj);
    }

    /**
     * 返回一个当前对象的副本，一般需要满足如下性质：
     * 		x.clone() != x
     *		x.clone().getClass() == x.getClass()
     *		x.clone().equals(x)
     * 可以根据需要实现浅拷贝和深拷贝，一般是浅拷贝
     * @throws  CloneNotSupportedException  Object没有实现Cloneable接口，本身是不能使用clone方法的，其子类调用clone方法会抛出此异常.只有实现了Cloneable才能正常使用
     * @see java.lang.Cloneable
     */
    protected native Object clone() throws CloneNotSupportedException;

    /**
     * 返回一个对象的字符串表示
     * 建议所有类都重写这个方法，因为不直观
     * 字符串内容，类名加上hash code转成16进制
     */
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * 用于随机唤醒一个在对象等待池中的线程
     * 被唤醒的线程不会立刻拿到对象的监视器锁，而是和其他在线程共同竞争，拿到锁之后恢复到调用wait方法时的同步状态
     * 同一时间只有一个线程能拿到监视器锁
     *
     * @throws  IllegalMonitorStateException  如果当前执行notify方法的线程没拿到对象的监视器锁
     */
    public final native void notify();

    /**
     * 一次唤醒所有等待池中的线程，其他和notify方法一样
     */
    public final native void notifyAll();

    /**
     * 持有对象监视器的线程可以执行awit方法把自己放入等待池中，并且放弃持有该对象的所有同步资源，不会放弃获取的其他对象的同步资源
     * 有四种情况线程可以逃出等待池：
     *		其他线程调用notify方法
     *		其他线程调用notifyAll方法
     *		其他线程调用interrupts方法
     *		超时时间到了
     * 逃出等待池拿到锁之后恢复到调用wait方法时的同步状态
     * 特别强调，如果因为interrupts逃出了等待池，不会立刻抛出InterruptedException，需要等到线程拿到锁
     * 由于偶尔会发生伪唤醒（没有调用notify或interrupts也没超时），所以我们在编码时应该如下例：
     *      synchronized (obj) {
     *    		while (<condition does not hold>)
     *        		obj.wait(timeout);
     *    			... // Perform action appropriate to condition
     *		}
     * @param      timeout   在等待池中的最长等待时间，时间到了就出狱了，如果值为0，则无期徒刑等待保释
     * @throws  IllegalArgumentException     超时时间是负数
     * @throws  IllegalMonitorStateException  当前线程没有拿到监视器锁
     * @throws  InterruptedException 中断位为true
     */
    public final native void wait(long timeout) throws InterruptedException;

    /**
     * 提供纳秒级别的等待控制，其他同wait(long timeout)
     * @param      nanos     额外的纳秒时间
     *                       0-999999.
     * @throws  IllegalArgumentException     timeout是负数，nanos是负数或者大于999999.
     */
    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos > 0) {
            timeout++;
        }

        wait(timeout);
    }

    /**
     * 和上面wait(long timeout)方法一样，只是没有超时时间
     */
    public final void wait() throws InterruptedException {
        wait(0);
    }

    /**
     * 当垃圾收集器判断此对象没有被任何引用，是垃圾对象了，由垃圾收集器执行此方法销毁对象，所以何时执行是不确定的
     * JVM保证垃圾收集器调用此方法时候，该对象的锁不被任何线程持有了
     * finalize方法只会被执行一次，所以我们可以重写此方法特定一些清除工作，或者给对象一次复活机会，但是不建议重写此方法。jdk9之后已废弃
     * @throws Throwable the {@code Exception} raised by this method
     * @see java.lang.ref.WeakReference
     * @see java.lang.ref.PhantomReference
     */
    protected void finalize() throws Throwable { }
}

```

## 实践

### clone()

```java
public class ObjectCloneTest {


    /**
     * 不实现 Cloneable 接口
     *
     * 输出：
     * Exception in thread "main" java.lang.CloneNotSupportedException: study.ObjectCloneTest$Obj
     * 	at java.lang.Object.clone(Native Method)
     * 	at study.ObjectCloneTest$Obj.main(ObjectCloneTest.java:24)
     */
    private static class Obj {
        public static void main(String[] args) throws CloneNotSupportedException {
            Obj o1 = new Obj();
            Object o2 = o1.clone();
        }
    }

    /**
     * 实现 Cloneable 接口，从结果看出默认实现是浅拷贝
     *
     * 输出：
     * 两个对象地址相同么
     * false
     * 两个对象的属性地址相同么
     * true
     * 改变其中一个属性会变么？
     * [1, 2]
     * [1, 2]
     */
    private static class ObjShallowCopy implements Cloneable {

        private List<String> list = new ArrayList<>();

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public static void main(String[] args) throws CloneNotSupportedException {
            ObjShallowCopy o1 = new ObjShallowCopy();
            o1.list.add("1");
            ObjShallowCopy o2 = (ObjShallowCopy) o1.clone();
            System.out.println("两个对象地址相同么");
            System.out.println(o1 == o2);
            System.out.println("两个对象的属性地址相同么");
            System.out.println(o1.list == o2.list);
            System.out.println("改变其中一个属性会变么？");
            o2.list.add("2");
            System.out.println(o1.list.toString());
            System.out.println(o2.list.toString());

        }

    }

    /**
     * 实现 Cloneable 接口，比较笨拙的做了一层深拷贝，只为了演示，深拷贝也可以通过序列号等方式实现
     *
     * 输出：
     * 两个对象地址相同么
     * false
     * 两个对象的属性地址相同么
     * false
     * 改变其中一个属性会变么？
     * [1]
     * [1, 2]
     */
    private static class ObjDeepCopy implements Cloneable {

        private List<String> list = new ArrayList<>();

        @Override
        protected Object clone() throws CloneNotSupportedException {
            ObjDeepCopy obj = (ObjDeepCopy) super.clone();
            List<String> list = new ArrayList<>(obj.list.size());
            list.addAll(obj.list);
            obj.list = list;
            return obj;
        }

        public static void main(String[] args) throws CloneNotSupportedException {
            ObjDeepCopy o1 = new ObjDeepCopy();
            o1.list.add("1");
            ObjDeepCopy o2 = (ObjDeepCopy) o1.clone();
            System.out.println("两个对象地址相同么");
            System.out.println(o1 == o2);
            System.out.println("两个对象的属性地址相同么");
            System.out.println(o1.list == o2.list);
            System.out.println("改变其中一个属性会变么？");
            o2.list.add("2");
            System.out.println(o1.list.toString());
            System.out.println(o2.list.toString());

        }

    }
}
```

### wait() 和 notify()

```java
public class ObjectWaitNotifyTest {

    private volatile boolean flag = true;

    public static void main(String[] args) {
        ObjectWaitNotifyTest obj = new ObjectWaitNotifyTest();

        new Thread(() -> {
            synchronized (obj) {
                System.out.println("绝食!");
                while (obj.flag == true) {
                    try {
                        obj.wait();
                        System.out.println("真香!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } 
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (obj) {
                obj.flag = false;
                obj.notify();
                System.out.println("海底捞走起!");
            }
        }).start();

        /**
         * 输出：
         * 绝食!
         * 海底捞走起!
         * 真香!
         */
    }
}
```

## 参考链接


##### 标签
#java #interview #source_code