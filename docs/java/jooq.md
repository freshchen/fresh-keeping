# Springboot + 持久层框架JOOQ

## 简介

[官网链接](https://www.jooq.org/)

JOOQ是一套持久层框架，主要特点是：

- 逆向工程，自动根据数据库结构生成对应的类
- 流式的API，像写SQL一样
  - 提供类型安全的SQL查询，JOOQ的主要优势，可以帮助我们在写SQL时就做检查
  - 支持几乎所有DDL，DML
  - 可以内部避免SQL注入安全问题
  - 支持SQL渲染，打印，绑定
- 使用非常轻便灵活
  - 可以用JPA做大部分简单的查询，用JOOQ写复杂的
  - 可以只用JOOQ作为SQL执行器
  - 可以只用来生成SQL语句（类型安全）
  - 可以只用来处理SQL执行结果
  - 支持Flyway，JAX-RS，JavaFX，Kotlin，Nashorn，Scala，Groovy，NoSQL
  - 支持XML，CSV，JSON，HTML导入导出
  - 支持事物回滚

## Springboot+JOOQ初体验

持久层框架很多，这里参考官网和其他博客用Springboot迅速搭建一个简单demo看看是否好用

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/jooq-2.png)

### 配置依赖

#### pom.xml

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jooq</artifactId>
</dependency>
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
    <plugin>
      <groupId>org.jooq</groupId>
      <artifactId>jooq-codegen-maven</artifactId>
      <executions>
        <execution>
          <goals>
            <goal>generate</goal>
          </goals>
        </execution>
      </executions>
      <dependencies>
        <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>5.1.45</version>
        </dependency>
      </dependencies>
      <configuration>
        <!--逆向生成配置文件-->
        <configurationFile>src/main/resources/library.xml</configurationFile>
        <generator>
          <generate>
            <pojos>true</pojos>
            <fluentSetters>true</fluentSetters>
          </generate>
        </generator>
      </configuration>
    </plugin>
  </plugins>
</build>
```

#### application.properties

```properties
#datasource
spring.datasource.url=jdbc:mysql://localhost:3306/demo
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 逆向工程

#### 配置文件

在项目的resources目录下新建library.xml，由于网上JOOQ的教程比较少，且比较老，所以建议去官网拷贝**对应版本**的配置文件，并酌情修改，否则会无法生成。

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<configuration xmlns="http://www.jooq.org/xsd/jooq-codegen-3.12.0.xsd">
    <!-- Configure the database connection here -->
    <jdbc>
        <driver>com.mysql.cj.jdbc.Driver</driver>
        <url>jdbc:mysql://localhost:3306/demo</url>
        <user>root</user>
        <password>123456</password>
    </jdbc>

    <generator>
        <!-- The default code generator. You can override this one, to generate your own code style.Supported generators:- org.jooq.codegen.JavaGenerator-org.jooq.codegen.ScalaGenerator Defaults to org.jooq.codegen.JavaGenerator -->
        <name>org.jooq.codegen.JavaGenerator</name>

        <database>
            <!-- The database type. The format here is:
                 org.jooq.meta.[database].[database]Database -->
            <name>org.jooq.meta.mysql.MySQLDatabase</name>

            <!-- The database schema (or in the absence of schema support, in your RDBMS this
                 can be the owner, user, database name) to be generated -->
            <inputSchema>demo</inputSchema>

            <!-- All elements that are generated from your schema
                 (A Java regular expression. Use the pipe to separate several expressions)
                 Watch out for case-sensitivity. Depending on your database, this might be important! -->
            <includes>.*</includes>

            <!-- All elements that are excluded from your schema
                 (A Java regular expression. Use the pipe to separate several expressions).
                 Excludes match before includes, i.e. excludes have a higher priority -->
            <excludes></excludes>
        </database>

        <target>
            <!-- The destination package of your generated classes (within the destination directory) -->
            <packageName>com.example.springbootjooq.generated</packageName>

            <!-- The destination directory of your generated classes. Using Maven directory layout here -->
            <directory>src/main/java</directory>
        </target>
    </generator>
</configuration>
```

#### 自动生成

- 我们在mysql中创建demo库，并创建一张User表如下（点的比较快，年龄字段用的varchar勿喷）

```mysql
mysql> describe user;
+-------+-------------+------+-----+---------+----------------+
| Field | Type        | Null | Key | Default | Extra          |
+-------+-------------+------+-----+---------+----------------+
| id    | int(11)     | NO   | PRI | NULL    | auto_increment |
| name  | varchar(45) | NO   |     | NULL    |                |
| age   | varchar(45) | NO   |     | NULL    |                |
+-------+-------------+------+-----+---------+----------------+
3 rows in set (0.00 sec)
```



- 执行compile，会把表结构的抽象，以及表对应的pojo自动生成到指定目录，然后就可以愉快的coding了

```bash
mvn clean compile
```

### Demo

这里实现了最基本的功能

##### Controller

```java
@RestController
@RequestMapping("/demo/")
public class DemoController {

    @Autowired
    private DemoService service;

    @RequestMapping("/insert/user/{name}/{age}")
    public void insert(@PathVariable String age, @PathVariable String name){
        service.insert(new User().setAge(age).setName(name));
    }

    @RequestMapping("/update/user/{name}/{age}")
    public void update(@PathVariable String age, @PathVariable String name){
        service.update(new User().setAge(age).setName(name));
    }

    @RequestMapping("/delete/user/{id}")
    public void delete(@PathVariable Integer id){
        service.delete(id);
    }

    @RequestMapping("/select/user/{id}")
    public User selectByID(@PathVariable Integer id){
        return service.selectById(id);
    }

    @RequestMapping("/select/user/")
    public List<User> selectByID(){
        return service.selectAll();
    }
}
```

##### Service

```java
@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    DSLContext create;

    com.example.springbootjooq.generated.tables.User USER = com.example.springbootjooq.generated.tables.User.USER;

    @Override
    public void delete(int id) {
        create.delete(USER).where(USER.ID.eq(id)).execute();
    }

    @Override
    public void insert(User user) {

        create.insertInto(USER)
                .columns(USER.NAME,USER.AGE)
                .values(user.getName(), user.getAge())
                .execute();
    }

    @Override
    public int update(User user) {
        create.update(USER).set((Record) user);
        return 0;
    }

    @Override
    public User selectById(int id) {
        return create.select(USER.NAME,USER.AGE).from(USER).where(USER.ID.eq(id)).fetchInto(User.class).get(0);
    }

    @Override
    public List<User> selectAll() {
        return create.select().from(USER).fetchInto(User.class);
    }
}
```

[Demo源码地址](https://github.com/freshchen/fresh-java-practice/tree/master/springboot-jooq)





## 参考

https://blog.csdn.net/weixin_40826349/article/details/89887355

