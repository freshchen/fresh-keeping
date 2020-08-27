# Java自定义注解

## 概念

### 作用

> 构建或者运行阶段提供一些元数据，不影响正常运行逻辑，简化开发

### 内置注解

Java提供了一些内置注解，并且实现了相关功能

- @Override 检查该方法是否是重载方法，如果发现其父类，或者是引用的接口中并没有该方法时，会报编译错误
- @Deprecated 标记过时方法。如果使用该方法，会报编译警告
- @SuppressWarnings 指示编译器去忽略注解中声明的警告
- @SafeVarargs 忽略任何使用参数为泛型变量的方法或构造函数调用产生的警告
- @FunctionalInterface 开始支持，标识一个匿名函数或函数式接口

### 元注解

Java提供了一些注解来构建自定义注解

- @Retention 指定生命周期
  - RetentionPolicy.RUNTIME：运行时可以被反射捕获到
  - RetentionPolicy.CLASS：注解会保留在.class字节码文件中，这是注解的默认选项，运行中获取不到
  - RetentionPolicy.SOURCE：只在编译阶段有用，不被保存到class文件中
- @Target 指定注解可以加在哪里
  - ElementType.ANNOTATION_TYPE：只能用于定义其他注解
  - ElementType.CONSTRUCTOR
  - ElementType.FIELD
  - ElementType.LOCAL_VARIABLE
  - ElementType.METHOD
  - ElementType.PACKAGE
  - ElementType.PARAMETER
  - ElementType.TYPE： 可以是类、接口、枚举或注释 
- @Inherited    使用了注解的类的子类会继承这个注解
- @Documented     用于在JavaDoc中生成
- @Repeatable 标识某注解可以在同一个声明上使用多次

## 实践

首先我们定义两个比较常见作用域的自定义注解，在开发过程中我们一般都是定义运行时的注解，编译时的注解一般都是实现APT，用于一些编译时候的校验和生成字节码，代表的有Lombok框架。

### 自定义注解

```java
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Hello {
    String value() default "";
}
```

```java
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Chinese {
}
```

### 注解功能实现

#### JDK动态代理

现在我们有了自定义注解但是他没有实现任何功能，就只起装饰作用，下面我们来模拟一个场景，一个Person类有order行为，我们希望通过注解在点单前加上打招呼，Person有一个属性name，我们希望校验这个人名字由汉字组成

##### Bean

```java
public class Person implements Action {

    @Chinese
    private String name;

    @Override
    @Hello("服务员")
    public void order() {
        System.out.println("可以给我一个汉堡包么？");
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Person() {
    }
}
```

##### 代理

```java
public class Proxys implements InvocationHandler {

    private Object target;

    public Proxys(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (this.target instanceof Person) {
            Person person = (Person) this.target;
            // 判断Person类，name字段有没有加Chinese注解
            if (person.getClass()
                    .getDeclaredField("name")
                    .isAnnotationPresent(Chinese.class)) {
                // 判断名字是不是汉字
                if (Objects.nonNull(person.getName()) &&
                        !person.getName().matches("[\\u4E00-\\u9FA5]+")) {
                    throw new IllegalArgumentException("Person Name is not chinese");
                }
            }
            Method targetMethod = person.getClass().getMethod(methodName);
            if ("order".equals(methodName)) {
                // 拦截接口实现类中order方法判断是否有Hello注解
                if (targetMethod.isAnnotationPresent(Hello.class)) {
                    System.out.println("你好," +
                            targetMethod.getAnnotation(Hello.class).value());
                } else if (method.isAnnotationPresent(Hello.class)) { // 拦截接口中order方法判断是否有Hello注解
                    System.out.println("你好," +
                            method.getAnnotation(Hello.class).value());
                }
                return method.invoke(this.target, args);
            }
        }
        return null;
    }

    public static Object getProxy(Object action) {
        Proxys handler = new Proxys(action);
        return Proxy.newProxyInstance(
                action.getClass().getClassLoader(),
                action.getClass().getInterfaces(),
                handler);
    }
}
```

##### 测试

可以看到我们的注解起到效果了

```java
public class Test {

    public static void main(String[] args) {
        Action person1 = (Action) Proxys.getProxy(new Person("匿名"));
        person1.order();
        Action person2 = (Action) Proxys.getProxy(new Person("Sun"));
        person2.order();
    }

    /**
     * 输出：
     * 你好,服务员
     * 可以给我一个汉堡包么？
     * Exception in thread "main" java.lang.IllegalArgumentException: Person Name is not chinese
     * 	at reflect.annotations.Proxys.invoke(Proxys.java:32)
     * 	at com.sun.proxy.$Proxy0.order(Unknown Source)
     * 	at reflect.annotations.Test.main(Test.java:9)
     */
}
```

#### Spring AOP

目前Spring框架用的比较多，我们定义和上面一样的hello注解

##### 切面

```java
@Aspect
@Component
public class HelloAspect {

    @Pointcut("@ (com.github.freshchen.springbootcore.annotation.Hello)")
    private void pointcut(){}

    @Before("pointcut() && @annotation(hello)")
    public void hello(Hello hello){
        System.out.println("你好，" + hello.value());
    }
}
```

##### 测试

同样起到了效果，Spring真香

```java
@SpringBootApplication
public class SpringbootCoreApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringbootCoreApplication.class, args);
        Person person = context.getBean("person", Person.class);
        person.order();
    }

    /**
     * 输出：
     * 你好，服务员
     * 可以给我一个汉堡包么？
     */

}
```

