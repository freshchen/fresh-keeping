---
begin: 2021-10-12
status: ongoing
rating: 1
---

# mybatis简介

[官方文档](https://mybatis.org/mybatis-3/zh/index.html)

## 什么是 MyBatis

MyBatis 是一款持久层框架，它支持自定义 SQL、存储过程以及高级映射。MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO 为数据库中的记录。

## 核心对象

#### SqlSessionFactoryBuilder

用于通过环境（XML、注解或 Java 配置代码）配置创建 SqlSessionFactory，最佳作用域是方法作用域（也就是局部方法变量），可以重用 SqlSessionFactoryBuilder 来创建多个 SqlSessionFactory 实例，但最好还是不要一直保留着它，以保证环境配置资源被释放。

#### SqlSessionFactory

用于创建 SqlSession，使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码“坏习惯”。因此 SqlSessionFactory 的最佳作用域是应用作用域。 

#### SqlSession

**核心接口**，包含了所有执行语句、提交或回滚事务以及获取映射器实例的方法。每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。 绝对不能将 SqlSession 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 HttpSession。 如果你现在正在使用一种 Web 框架，考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。

## 简单实战

### 引入依赖

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>${mybatis.version}</version>
</dependency>
```

### 使用

[代码示例](https://github.com/freshchen/fresh-keeping/tree/master/java/sql-orm/spring-mybatis)


## 参考链接

##### 标签
#orm #database
