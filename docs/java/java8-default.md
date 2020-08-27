# Java8新特性之接口defualt,static方法

## 简介

### 作用

Java8中接口引入了defualt,static两种方法提供默认实现，彻底打破了接口不能有默认实现的规定

- static
  - 让接口类似于工具类，提供一些静态方法
  - static方法不会被子类继承
- defualt
  - 给接口加入了默认方法实现
  - defualt方法会被子类继承

### 为什么

当我们写好一个库发布出去，很快收到了很多star，是不是开心的不行，可是有一天看到一个issue，确实最初有一个接口设计有缺陷，需要新加一个方法，这时怎么办呢？

- 直接在过去的接口中新增一个方法？
  - 这是在犯罪！
- 使用defualt提供新增方法的默认实现？
  - 没错，这就是defualt方法的重要运用场景，可以帮助我们向后兼容的同时，不断演进

### 影响

这一变动让接口和抽象类的区别越来越小了

- 一个类只能继承一个抽象类，但是可以实现多个接口
- 抽象类可以保存一些通用的成员属性，接口中不能有属性

也就是说除了我们需要抽象方法的同时还需要一些成员属性时我们使用抽象类，其他情况我们都应该使用接口。

同时我们应该发现这样一来Java也相当于有了部分多重继承的能力，那么我们会遇到臭名昭著的菱形继承问题么？让我们在实践中寻找答案

## 实践

### static

工具接口

```java
public interface Support {
    static void weather(){
        System.out.println("晴转多云");
    }
}
```

```java
public class Test {
    public static void main(String[] args) {
        Support.weather();
    }

    /**
     * 输出:
     * 晴转多云
     */
}
```

### defualt

运动Sports接口，默认实现了打乒乓，然后Person实现这个接口

```java
public interface Sports {

    default void pingPong(){
        System.out.println("打乒乓球");
    }

}
```

```java
public class Person implements Sports{
}
```

```java
public class Test {
    public static void main(String[] args) {
        new Person().pingPong();
    }

    /**
     * 输出:
     * 打乒乓球
     */
}
```

#### 多重继承

defualt的使用真的很简单，但是多重继承的情景下表现的如何呢，当多个接口有相同的方法时会如何执行呢，有三大规则，按顺序匹配，保证了可靠性。

##### 规则1.类或者父类中声明的方法的优先级高于任何默认方法

我们新加一个接口有相同的函数签名

```java
public interface Programs {

    default void pingPong(){
        System.out.println("观看乒乓球比赛");
    }
}
```

然后再Person中提供具体实现

```java
public class Person implements Sports{

    @Override
    public void pingPong() {
        System.out.println("我在打乒乓球");
    }
}
```

```java
public class Chinese extends Person implements Programs, Sports{
}
```

```java
public class Test {
    public static void main(String[] args) {
        new Chinese().pingPong();
    }

    /**
     * 输出:
     * 我在打乒乓球
     */
}
```

##### 规则2.同函数签名的方法中实现得最具体的那个接口的方法

我们新增一个球类运动接口

```java
public interface BallSports extends Sports{

    @Override
    default void pingPong() {
        System.out.println("打乒乓球（球类运动）");
    }
}
```

```java
public class Person implements Sports, BallSports{
}
```

```java
public class Test {
    public static void main(String[] args) {
        new Person().pingPong();
    }

    /**
     * 输出:
     * 打乒乓球（球类运动）
     */
}
```

##### 规则3.显示声明

如果上面两条都不能满足，那就过不了编译需要我们显示声明怎么执行了，没有extend，同时Sports，Programs也没有继承关系，这时候就只能我们显示声明了

```java
public class Person implements Sports, Programs {
    @Override
    public void pingPong() {
        Programs.super.pingPong();
    }
}
```

```java
public class Test {
    public static void main(String[] args) {
        new Person().pingPong();
    }

    /**
     * 输出:
     * 观看乒乓球比赛
     */
}
```





