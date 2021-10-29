---
begin: 2021-10-28
status: ongoing
rating: 1
---

# springboot 2.2 升级 2.3 指南

## 1构建工具
如果用 Gradle 最低版本要求 6.3+

## 2web容器
如果用 Jetty 最低版本要求 9.4.22+

## 3pom文件

### 3.2

spring-boot-starter-web 中移除了 validation，确保已经手动引入如下依赖

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

## 4注意事项

### 4.1 不再支持 Maven Exec Plugin

<plugin>
   <groupId>org.codehaus.mojo</groupId>
   <artifactId>exec-maven-plugin</artifactId>
   <configuration>
       <mainClass>${start-class}</mainClass>
   </configuration>
</plugin>

## 参考链接
https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes

##### 标签
#springboot