# Java8新特性之Lambda

## 为什么要Lambda

Java8应该是目前最大的一次更新了，更新后我们迎来了很多新特性，其中便包括Lambda表达式，函数式编程的思想正式进入Java，让我们看一个经典案例。

### 例1 按照两个人的年龄排序的功能

采用匿名内部类已经算简介了，如果专门用一个类去实现Comparator再new出来就更烦了，过去的写法：

```java
// 已经创建好了三个Person实例
List<Person> people = Arrays.asList(person1, person2, person3);

Collections.sort(people, new Comparator<Person>() {
    @Override
    public int compare(Person o1, Person o2) {
        return o1.getAge().compareTo(o2.getAge());
    }
});
```

Lambda版本写法：

```java
Collections.sort(people, (p1, p2) -> p1.getAge().compareTo(p2.getAge()));
```

还有更简介的方法引用写法：

```java
Collections.sort(people, Comparator.comparing(Person::getAge));
```

是不是真的短真的易读，语法糖真的甜！已经9102年了，函数式编程被提到的越来越多，深谙照猫画虎已经行不通了，而且函数式编程和设计模式的碰撞也很多，真的有必要了解下相关概念

## 哪里用Lambda

### 函数式编程

>  函数式编程是一种思想，核心是**行为参数化**，把一段代码像值一样传递给方法，传入不同的代码实现不同的功能

这是不是很像策略模式以及模板模式？如例1所示，不需要大量的套路代码了，也不需要把代码写到一个类中然后新建实例对象最后把实例对象传递

### 函数式接口

>  函数式接口就是只定义一个抽象方法的接口来表示行为，抽象方法不允许抛出受检异常，Java8接口可以有default方法了，函数式接口是允许有default方法的

Lambda表达式看上去确实很有吸引力，我能在任何地方都使用么？答案是不能的，我们只能通过Lambda表达式把代码传到函数式接口中，拿例1中的Comparator接口来看

```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

Comparator接口只有compare一个抽象方法，Java8特意给它加了注解告诉我们这就是个函数式接口，其实也很好理解，我们没用创建任何类，只传了一串代码，如果Comparator有两个抽象方法，编译器怎么知道我们实现的是compare方法呢

## 怎么用Lambda

上面都是说的函数式编程，那么什么是Lambda

> Lambda就是匿名的行为参数化的一种语法实现，它没有名称，但它有参数列表、函数主体、返回类型，可能还有一个可以抛出的异常列表

### 语法

- (parameters) -> expression
  - 默认Return的，expression只能是一句代码
- (parameters) -> { statements; }
  - 没有默认Retrun，就相当于Comparator.compare( statements; )，statements可以是好多行

**tips**：如果statements很长，那么我们就不应该用Lambda，而应该单独实现一个方法，然后使用方法引用这样可读性更好，继续例1

```java
// 比如说在MyUtils类下写个方法，故意加长
public static Integer sortPersonByName(Person person1, Person person2) {
    Integer age1 = person1.getAge();
    Integer age2 = person2.getAge();
    return age1.compareTo(age2);
}

// 又用到了方法引用，我们可以把方法引用当作一种便于阅读的语法糖，功能也是传递代码
Collections.sort(people, Comparator.comparing(MyUtils::sortPersonByAge));
```

## 凭什么Lambda

在使用Lambda的时候我们没有任何类型声明就能工作这是怎么做到的呢？

### 函数描述符

> 函数式接口的抽象方法的签名基本上就是Lambda表达式的签名。我们将这种抽象方法叫作
> 函数描述符

Comparator.compare的签名

- ```java
  int compare(T o1, T o2);
  ```

- 这个函数式接口的签名就可以描述成需要两个相同类型的变量，然后返回int
- ( T, T ) -> int

Lambda的签名

- ```java
  (p1, p2) -> p1.getAge().compareTo(p2.getAge())
  // 这是易读的写法，我们也可以写成方便说明
  (Person p1, Person p2) -> p1.getAge().compareTo(p2.getAge())
  ```

- 可以描述成需要两个Person变量，然后compareTo方法返回int

- (Person, Person) -> int

编译器会做类型推断和类型检查，发现两个签名匹配，我们的Lambda表达式就可以顺利执行了

**tips**：Lambda主体是语句表达式的时候(parameters) -> expression ，尽管expression返回可能不是void，但是也是兼容 T -> void 签名的

## 进阶

### 新增函数式接口

除了 Runnable，Comparator等常用函数式接口，为了推动函数式编程，Java8又在 java.com.github.freshchen.keeping.util.function 包下为我们提供了大量好用的函数式，因为基本数据类型不能抽象成对象，所以可以看到有大量Double，Int，Long前缀的接口，我们只看比较核心的：

| 接口名              | 抽象方法              | 描述符              |
| ------------------- | --------------------- | ------------------- |
| Predicate<T>        | boolean test(T t)     | T -> boolean        |
| BiPredicate<T, U>   | test(T t, U u)        | ( T, U ) -> boolean |
| Consumer<T>         | void accept(T t)      | T -> void           |
| BiConsumer<T, U>    | void accept(T t, U u) | ( T, U ) -> void    |
| Function<T, R>      | R apply(T t)          | T -> R              |
| BiFunction<T, U, R> | R apply(T t, U u)     | ( T, U ) -> R       |
| Supplier<T>         | T get()               | void -> T           |

### 复合Lambda

令人惊喜的是，java8提供的函数式接口还有许多好用的default方法，可以让我们把多个Lambda复合起来，组成流水线。拿用的比较多的Function接口举例，我们要写一封邮件，我们关注的是信的内容

```java
Function<String, String> writeEmailHeader = text -> "Hi ," + "\n" + text;
Function<String, String> writeEmailText = text -> text + "\n";
Function<String, String> writeEmailFooter = text -> text + "BRs" + "\n" + "Chen";
Function<String, String> writeEmail = writeEmailHeader.andThen(writeEmailText).andThen(writeEmailFooter);

System.out.println(writeEmail.apply("I will take half day sick leave today"));

/**
 * 输入如下：
 * Hi ,
 * I will take half day sick leave today
 * BRs
 * Chen
 */
```
