# 设计模式之代理模式（Java）

## 简介

代理模式出场率真的相当的高，几乎所有框架中无一例外都用到了代理模式，所以了解一下收益还是很高的。

### 代理模式是什么

如果用一句话来描述代理模式：

> 代理模式就是为其他对象提供一种代理以控制对被代理对象的访问，也就是我们常说的中介

在开发以及生活中经常听到正向代理，反向代理这样的词，举例说明

- **正向代理**

  由于网络原因我们访问不了谷歌，这时候我们就需要找个梯子，替我们去访问谷歌，并且把我们需要的信息返回，这个梯子代理

- **反向代理**

  作为服务端为了安全，我们不想把实际服务器的信息暴露出去，已防止不法分子的攻击，这时候我们我需要一个代理统一接受用户的请求，并且帮助用户请求后端用户返回给用户

### 代理模式的作用

一言以蔽之就是解耦合，创建一个没法访问对象的代理供我们使用，同时我们又可以在代理对象中加入一些补充的功能，这样完全不会破坏封装，满足开闭原则

### UML

动物有一个睡觉行为，大多数人都没法见到北极熊（RealSubject），我们只能通过动物世界节目组的摄影师（Proxy）去北极拍摄，从传回的画面中我们看到一只北极熊在洞里睡觉，并且画面上还加上了字幕“快看这里有只冬眠的北极熊！”

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/draw/dp-proxy.png)

## 实践

代理模式的实现有多种方式主要分为静态代理和动态代理

### 静态代理

- Subject

```java
public interface LifeService {
    String sleep();
}
```

- RealSubject

```java
public class WhiteBear implements LifeService {
    @Override
    public String sleep() {
        return "Zzzzzzz";
    }
}
```

- Proxy

```java
public class Proxy implements LifeService {

    // 被代理对象
    private LifeService target;

    public Proxy(LifeService target) {
        this.target = target;
    }

    @Override
    public String sleep() {
        // 拿到被代理对象行为的返回值，加上辅助功能，一起返回
        return "快看这里有只冬眠的北极熊！ \n" + this.target.sleep();
    }
}
```

- Factory，也可以不用工厂客户端直接new

```java
public class ProxyFactory {

    public static Proxy getLifeServiceProxy(Class clz) throws IllegalAccessException, InstantiationException {
        LifeService target = (LifeService) clz.newInstance();
        return new Proxy(target);
    }
}
```

- Client

```java
public class Test {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Proxy proxy = ProxyFactory.getLifeServiceProxy(WhiteBear.class);
        System.out.println(proxy.sleep());
    }

    /**
     * 输出：
     * 快看这里有只冬眠的北极熊！
     * Zzzzzzz
     */
}
```

可以看到静态代理其实挺好理解的，就是我们把被代理和代理类都写好，生成两个class字节码文件， 所谓静态也就是在程序运行前就已经存在代理类的字节码文件 

**不足**

-  代理对象的一个接口只服务于一种类型的对象，如果要代理的方法很多，势必要为每一种方法都进行代理，静态代理在程序规模稍大时就无法胜任了。  
-  如果接口增加一个方法，除了所有实现类需要实现这个方法外，所有代理类也需要实现此方法。增加了代码维护的复杂度 

### 动态代理

#### JDK自带

大致思路是在运行过程中JVM进行监控，发生指定行为时动态的创建的代理，通过反射去访问被代理对象

- Subject

```java
public interface LifeService {
    String sleep();
    String wake();
}
```

- RealSubject

```java
public class Person implements LifeService {
    @Override
    public String sleep() {
        return "晚安晚安";
    }

    @Override
    public String wake() {
        return "早鸭";
    }
}
```

- Proxy

  我们实现InvocationHandler这个接口，通过invoke方法去执行代理行为

```java
public class InvocationProxy implements InvocationHandler {

    // 被监控的对象（此例中为Person类实例）
    private LifeService lifeService;

    // 监控启动拿到需要被监控的对象
    public InvocationProxy(LifeService lifeService) {
        this.lifeService = lifeService;
    }

    /**
     * 监控的行为发生时，JVM会拦截到行为执行invoke
     *
     * @param proxy  监控对象：监控行为是否发生
     * @param method 被监控的行为方法
     * @param args   被监控行为方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 因为我们拦截了行为，并且加了一些辅助行为，完成之后我们要替被拦截行为把值返回
        Object jsonResult = null;
        String methodName = method.getName();
        if ("sleep".equals(methodName)) {
            jsonResult = getTime();
            jsonResult += (String) method.invoke(this.lifeService, args);
        } else if ("wake".equals(methodName)) {
            jsonResult = getTime();
            jsonResult += (String) method.invoke(this.lifeService, args);
        }
        return jsonResult;
    }

    // 辅助方法
    private String getTime() {
        return Clock.systemDefaultZone().instant().toString() + "\n";
    }

}
```

- Factory，也可以不用工厂客户端直接new

```java
public class ProxyFactory {

    public static LifeService getLifeServiceProxyInstance(Class clz) throws IllegalAccessException, InstantiationException {
        // 创建被代理对象
        LifeService target = (LifeService) clz.newInstance();
        // 绑定到代理执行器中
        InvocationHandler handler = new InvocationProxy(target);
        // JVM层面对被代理对象进行监控，行为发生就动态创建代理对象处理
        LifeService $proxy = (LifeService) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                handler);
        return $proxy;
    }
}
```

- Client

```java
public class Test {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InterruptedException {
        LifeService zhang = ProxyFactory.getLifeServiceProxyInstance(Person.class);
        System.out.println(zhang.sleep());
        System.out.println(zhang.wake());
    }

    /**
     * 输出：
     * 2019-11-10T05:24:16.932Z
     * 晚安晚安
     * 2019-11-10T05:24:16.942Z
     * 早鸭
     */
}
```

用了动态代理我们把所有代理需要实现的行为集中到了invoke这一个方法去执行，不要再写大量模板代码了，并且我们实际上可以在一个InvocationHandler代理多个接口

**不足**：

- 如果InvocationHandler中代理了两个接口，两个接口中有完全一模一样的两个方法，就没法去区分了
- 代理必须基于接口，没有实现接口的类没法被代理

#### 三方库Cglib

Cglib 基于 ASM 框架操作字节码帮我们生成需要的代理对象，并且不要求实现接口

加入依赖

```xml
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>3.3.0</version>
</dependency>
```

- RealSubject

  我们不需要实现特定接口了

```java
public class Person {

    public String sleep() {
        return "晚安晚安";
    }

    public String wake() {
        return "早鸭";
    }
}
```

- Proxy

  我们的逻辑和JDK自带的动态代理是一样的

```java
public class CglibProxy implements MethodInterceptor {
    //需要代理的目标对象
    private Object target;

    public CglibProxy(Object target) {
        this.target = target;
    }

    /**
     *
     * @param o 监控对象：监控行为是否发生
     * @param method 被监控的行为方法
     * @param objects 被监控行为方法的参数
     * @param methodProxy 代理中生成的方法
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object jsonResult = null;

        String methodName = method.getName();
        if ("sleep".equals(methodName)) {
            jsonResult = getTime();
            jsonResult += (String) method.invoke(this.target, objects);
        } else if ("wake".equals(methodName)) {
            jsonResult = getTime();
            jsonResult += (String) method.invoke(this.target, objects);
        }
        return jsonResult;
    }

    // 辅助行为
    private String getTime() {
        return Clock.systemDefaultZone().instant().toString() + "\n";
    }
}
```

- Factory，也可以不用工厂客户端直接new

```java
public class ProxyFactory {

    public static Object getCglibProxyInstance(Class clz) throws IllegalAccessException, InstantiationException {
        // Enhancer类是CGLib中的一个字节码增强器
        Enhancer enhancer=new Enhancer();
        // 设置被代理类的字节码文件，这里我们关注的不再是接口
        enhancer.setSuperclass(clz);
        // 创建被代理对象
        Object target = clz.newInstance();
        // 绑定到代理执行器中
        CglibProxy proxy = new CglibProxy(target);
        // 设置回调这个代理对象
        enhancer.setCallback(proxy);
        // 生成返回代理对象
        return enhancer.create();
    }
}
```

- Client

```java
public class Test {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Person zhang = (Person) ProxyFactory.getCglibProxyInstance(Person.class);
        System.out.println(zhang.sleep());
        System.out.println(zhang.wake());
    }

    /**
     * 输出：
     * 2019-11-10T06:01:13.105Z
     * 晚安晚安
     * 2019-11-10T06:01:13.115Z
     * 早鸭
     */
}
```

不需要实现接口也可以动态代理啦，真的很了不起

**不足**：

- 依赖三方库
- 对于final的类和方法不能代理， 因为Cglib 生成的代理类需要重写代理类中所有的方法















